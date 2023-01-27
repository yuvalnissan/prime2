import React, { useEffect, useRef } from "react"
import { Network, Node, Edge} from "vis-network"
import {DataSet} from "vis-data"
import Box from '@mui/material/Box'
import List from '@mui/material/List'
import TextField from '@mui/material/TextField'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemIcon from '@mui/material/ListItemIcon'
import ListItemText from '@mui/material/ListItemText'
import InboxIcon from '@mui/icons-material/Inbox'
import { Neuron } from '../../businessLogic/neuron'
import { Expression } from '../../businessLogic/data'
import styles from './DataList.module.scss'

const shapeMapping = {
    infer: 'triangleDown',
    value: 'dot',
    rel: 'square',
    rule: 'triangle'
} as Record<string, string>

export interface DataListProps {
    className?: string
    neurons: Record<string, Neuron>
    setSelectedExpressionId: Function
    selectedExpressionId: string
}

const options = {
    layout: {
        randomSeed: 2
    },
    nodes: {
        shape: "dot",
        size: 10,
    }
}

export const DataList = ({neurons, selectedExpressionId, setSelectedExpressionId, className }: DataListProps) => {
    const [nodes, setNodes] = React.useState<DataSet<Node>>(new DataSet([]))
    const [edges, setEdges] = React.useState<DataSet<Edge>>(new DataSet([]))
    const [network, setNetwork] = React.useState<Network | null>(null)
    const [filter, setFilter] = React.useState<string>('')

    const handleListItemClick = (
        event: React.MouseEvent<HTMLDivElement, MouseEvent>,
        id: string,
    ) => {
        setSelectedExpressionId(id);
    }

    const ListNode = (expression: Expression) => {
        return <ListItemButton
        key = { expression.id}
        selected={selectedExpressionId === expression.id}
        onClick={(event) => handleListItemClick(event, expression.id)}
    >
        <ListItemIcon>
        <InboxIcon />
        </ListItemIcon>
        <ListItemText primary={expression.id.replaceAll(',', ', ')} />
    </ListItemButton>
    }

    const visJsRef = useRef<HTMLDivElement>(null)

    const getColor = (confidence: string) => {
        const strength = parseFloat(confidence.substring(0, confidence.indexOf("|")))
        const red = 255 * Math.min(strength, 0.0) * (-1.0)
        const green = 255 * Math.max(strength, 0.0)
        const blue = 255 * (1.0 - Math.abs(strength)) * 0.5

        return `rgb(${red},${green},${blue})`
    }

    const getShape = (type: string): string => shapeMapping[type] || 'dot'

    useEffect(() => {
        Object.values(neurons).forEach(neuron => {
            const color = getColor(neuron.nodes.confidence?.props?.confidence || "0|0|0")
            const shape = getShape(neuron.data.type)
            const id = neuron.data.id
            const label = id

            if (nodes.get(id)) {
                nodes.update({ id, label, color})
            } else {
                //, fixed: true
                nodes.add({ id, label, color, shape})
            }

            neuron.links.forEach(link => {
                const edgeId = link.from.id + '-' + link.to.id
                if (edges.get(edgeId)) {
                    edges.update({id: edgeId, from: link.from.id, to: link.to.id})
                } else {
                    edges.add({id: edgeId, from: link.from.id, to: link.to.id})
                }
            })
        })

        if (!network && nodes.length > 0) {
            console.log('rendering')
            setNetwork(visJsRef.current && new Network(visJsRef.current, { nodes, edges }, options))
        }
    

        if (selectedExpressionId) {
            network?.setSelection({nodes: [selectedExpressionId]})
        }

        network?.on( 'click', function(properties) {
            var ids = properties.nodes;
            if (ids?.length > 0) {
                console.log('Select', ids[0])
                setSelectedExpressionId(ids[0])
            }
        })
        
        // Use `network` here to configure events, etc
    }, [visJsRef, neurons, selectedExpressionId])

    const compareIds = (neuron1: Neuron, neuron2: Neuron) => neuron1.data.id === neuron2.data.id ? 0 : (neuron1.data.id > neuron2.data.id ? 1 : -1)

    const filterBoxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const value = event.target.value
        setFilter(value)
    }

    const matchesFilter = (neuron: Neuron) => {
        const regexStr = filter.toLowerCase().replaceAll(' ', '.*')
        const re = new RegExp(regexStr)

        return !!neuron.data.id.toLowerCase().match(re)
    }

    return (
        <Box className={styles['frame']} sx={{ width: '100%', bgcolor: 'background.paper' }}>
            <Box className={styles['list']}>
                <TextField className={styles['search']} id="outlined-basic" fullWidth variant="outlined" onChange={filterBoxChange} />
                <List className={styles['results']} component="nav" aria-label="main mailbox folders">
                    {Object.values(neurons).filter(matchesFilter).sort(compareIds).map(neuron => ListNode(neuron.data))}
                </List>
            </Box>
            <div className={styles['graph']} ref={visJsRef} />
        </Box>
    )
}

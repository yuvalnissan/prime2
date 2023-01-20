import React, { useEffect, useRef } from "react"
import { Network, Node, Edge} from "vis-network"
import {DataSet} from "vis-data"
import Box from '@mui/material/Box'
import List from '@mui/material/List'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemIcon from '@mui/material/ListItemIcon'
import ListItemText from '@mui/material/ListItemText'
import InboxIcon from '@mui/icons-material/Inbox'
import { Neuron } from '../../businessLogic/neuron'
import { Expression } from '../../businessLogic/data'
import styles from './DataList.module.scss'

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

    useEffect(() => {
        const nodesArray = [] as Node[]
        const nodes = new DataSet(nodesArray)
        const edgesArray = [] as Edge[]
        const edges = new DataSet(edgesArray)
        
        

        edges.clear()
        nodes.clear()
        Object.values(neurons).forEach(neuron => {
            nodes.add({ id: neuron.data.id, label: neuron.data.id})
            neuron.links.forEach(link => {
                edges.add({from: link.from.id, to: link.to.id})
            })
        })

        //TODO this is stable, but always rebuilds. Need to move outside of the useEffect
        const network =
                visJsRef.current &&
                new Network(visJsRef.current, { nodes, edges }, options)

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

    return (
        <Box className={styles['frame']} sx={{ width: '100%', bgcolor: 'background.paper' }}>
            <List className={styles['list']} component="nav" aria-label="main mailbox folders">
                {Object.values(neurons).map(neuron => ListNode(neuron.data))}
            </List>
            <div className={styles['graph']} ref={visJsRef} />
        </Box>
    )
}

import React, { useEffect, useRef } from "react"
import { Network, Node, Edge} from "vis-network"
import { DataSet } from "vis-data"
import Box from '@mui/material/Box'
import { Neuron } from '../../businessLogic/neuron'
import styles from './Graph.module.scss'

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

export const Graph = ({neurons, selectedExpressionId, setSelectedExpressionId }: DataListProps) => {
    const [nodes] = React.useState<DataSet<Node>>(new DataSet([]))
    const [edges] = React.useState<DataSet<Edge>>(new DataSet([]))
    const [network, setNetwork] = React.useState<Network | null>(null)

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
        console.log('Refreshing graph')
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
    }, [visJsRef, neurons, nodes, edges])

    useEffect(() => {
        if (selectedExpressionId) {
            network?.setSelection({nodes: [selectedExpressionId]})
        }
    }, [visJsRef, selectedExpressionId])

    return (
        <div className={styles['graph']} ref={visJsRef} />
    )
}

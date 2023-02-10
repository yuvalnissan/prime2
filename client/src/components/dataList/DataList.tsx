import React from 'react'
import Box from '@mui/material/Box'
import List from '@mui/material/List'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemIcon from '@mui/material/ListItemIcon'
import ListItemText from '@mui/material/ListItemText'
import Typography from '@mui/material/Typography'
import { Neuron } from '../../businessLogic/neuron'
import styles from './DataList.module.scss'
import { Graph } from '../graph/Graph'

export interface DataListProps {
    className?: string
    neurons: Record<string, Neuron>
    setSelectedExpressionId: Function
    selectedExpressionId: string
    filteredIds: string[]
    focused: number
    setFocused: Function
    loadGraph: boolean
}

export const DataList = ({neurons, selectedExpressionId, setSelectedExpressionId, filteredIds, focused, setFocused, loadGraph, className }: DataListProps) => {
    const handleListItemClick = (
        event: React.MouseEvent<HTMLDivElement, MouseEvent>,
        id: string,
        index: number
    ) => {
        console.log('List item clicked', index, id)
        setSelectedExpressionId(id)
        setFocused(index)
    }

    const ListNode = (id: string, index: number) => {
        const confidenceNode = neurons[id]?.nodes['confidence']
        const confidence = confidenceNode?.props['confidence'] || '0|0|0'
        const strength = confidence.split('|')[0]

        return <ListItemButton
            key = {id}
            selected={focused === index}
            onClick={(event) => handleListItemClick(event, id, index)}
        >
            <ListItemIcon>
            <Typography variant="subtitle1" display="inline">
                {strength}
            </Typography>
            </ListItemIcon>
            <ListItemText primary={id.replaceAll(',', ', ')} />
        </ListItemButton>
    }

    return (
        <Box className={styles['selectionFrame']} sx={{ width: '100%', bgcolor: 'background.paper' }}>
            <Box className={styles['list']}>
                <List id="data-list" component="nav" aria-label="main mailbox folders">
                    {filteredIds.slice(0, 20).map(ListNode)}
                </List>
            </Box>
            {loadGraph ? <Graph neurons={neurons} setSelectedExpressionId={setSelectedExpressionId} selectedExpressionId={selectedExpressionId} /> : null}
        </Box>
    )
}

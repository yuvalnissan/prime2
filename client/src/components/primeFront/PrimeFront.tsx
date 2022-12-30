import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import TextField from '@mui/material/TextField'
import {DataView} from '../data/Data'
import styles from './PrimeFront.module.scss'
import { Expression } from '../../businessLogic/data'
import { DataList } from '../dataList/DataList'

export interface PrimeFrontProps {
    className?: string
}

const defaultExpressions = {
    'Ford ISA (something otherThanA Car)': {
        id: 'Ford ISA (something otherThanA Car)',
        type: 'rel',
        val: 'isa',
        expressions: [
            {
                id: '2',
                type: 'obj',
                val: 'Ford'
            },
            {
                id: '3',
                type: 'rel',
                val: 'otherThanA',
                expressions: [
                    {
                        id: '7',
                        type: 'obj',
                        val: 'something'
                    },
                    {
                        id: '6',
                        type: 'obj',
                        val: 'Car'
                    }
                ]
            }
        ]
    },
    'Ford ISA not Horse': {
        id: 'Ford ISA not Horse',
        type: 'rel',
        val: 'isa',
        expressions: [
            {
                id: '4',
                type: 'obj',
                val: 'Ford'
            },
            {
                id: '5',
                type: 'obj',
                val: 'Horse',
                negative: true
            }
        ]
    }
}

export const PrimeFront = ({ className }: PrimeFrontProps) => {
    const searchParams = new URLSearchParams(document.location.search)
    const scenario = searchParams.get('scenario') || 'Missing scenario'
    const [expressions, setExpressions] = React.useState<Record<string, Expression>>(defaultExpressions)
    const [selectedExpressionId, setSelectedExpressionId] = React.useState<string>('')

    return <Box className={`${styles.root} ${className} ${styles.all}`}>
        <Box className={styles['left-panel']}>
            <TextField id="outlined-basic" label={scenario} variant="outlined" />
            <Button>Refresh</Button>
            <DataList expressions = {expressions} setSelectedExpressionId = {setSelectedExpressionId} selectedExpressionId = {selectedExpressionId}/>
        </Box>
        <Box className={styles['right-panel']}>
            {(expressions[selectedExpressionId]) ? <DataView expression={expressions[selectedExpressionId]} /> : null}
        </Box>
    </Box>
}

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

const geDataFromNeuron = (neuronData: any): Expression => {
    const expression = {
        id: neuronData.displayName as string,
        type: neuronData.type.predicate as string,
        val: neuronData.displayName as string,
        expressions: neuronData.expressions?.map((exp: any) => {
            const data = geDataFromNeuron(exp.data)
            const negative = exp.modifier === 'NEGATIVE'

            return {...data, negative}
        })
    }

    return expression
}

export const PrimeFront = ({ className }: PrimeFrontProps) => {
    const searchParams = new URLSearchParams(document.location.search)
    const scenario = searchParams.get('scenario') || 'Missing scenario'
    const [expressions, setExpressions] = React.useState<Record<string, Expression>>({})
    const [selectedExpressionId, setSelectedExpressionId] = React.useState<string>('')

    const setData = (agent: any) => {
        console.log(agent)

        const memory = agent.memory
        const keys: string[] = Object.keys(agent.memory)
        const expressions = {} as Record<string, Expression>
        keys.forEach(key => {
            expressions[key] = geDataFromNeuron(memory[key].data)
        })

        console.log(expressions)
        setExpressions(expressions)
    }


    React.useEffect(() => {
        fetch('http://localhost:8080/agent?name=default')
            .then(response => {
                if (response.ok) {
                    return response.json()
                }
            })
            .then(data => {
                setData(data)
            })
            .catch(err => {
                console.error('Failed to fetch', err)
            })
    }, [])

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

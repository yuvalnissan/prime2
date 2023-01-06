import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Typography from '@mui/material/Typography'
import { NeuronView } from '../neuron/NeuronView'
import styles from './PrimeFront.module.scss'
import { Expression } from '../../businessLogic/data'
import { DataList } from '../dataList/DataList'
import { Neuron } from '../../businessLogic/neuron'

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

const getNeuron =  (neuron: any): Neuron => {
    const data = geDataFromNeuron(neuron.data)
    const nodes = neuron.nodes

    return {data, nodes}
}

export const PrimeFront = ({ className }: PrimeFrontProps) => {
    const searchParams = new URLSearchParams(document.location.search)
    const scenario = searchParams.get('scenario')
    const agent = searchParams.get('agent')
    const [expressions, setExpressions] = React.useState<Record<string, Expression>>({})
    const [neurons, setNeurons] = React.useState<Record<string, Neuron>>({})
    const [selectedExpressionId, setSelectedExpressionId] = React.useState<string>('')

    const setData = (agent: any) => {
        const memory = agent.memory
        const keys: string[] = Object.keys(agent.memory)
        const neurons = {} as Record<string, Neuron>
        const expressions = {} as Record<string, Expression>
        keys.forEach(key => {
            const neuron = getNeuron(memory[key])
            neurons[key] = neuron
            expressions[key] = neuron.data
        })

        setNeurons(neurons)
        setExpressions(expressions)
    }


    React.useEffect(() => {
        fetch(`http://localhost:8080/scenario?name=${scenario}&agent=${agent}`)
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
            <Box className={styles['controls']}>
                <Box className={styles['context']}>
                    <Typography variant="subtitle1" display="inline">
                        Scenario: {scenario}
                    </Typography>
                </Box>
                <Box className={styles['context']}>
                    <Typography variant="subtitle1" display="inline">
                        Agent: {agent}
                    </Typography>
                </Box>
                <Button>Refresh</Button>
            </Box>
            <DataList expressions = {expressions} setSelectedExpressionId = {setSelectedExpressionId} selectedExpressionId = {selectedExpressionId}/>
        </Box>
        <Box className={styles['right-panel']}>
            {(expressions[selectedExpressionId]) ? <NeuronView neuron={neurons[selectedExpressionId]} /> : null}
        </Box>
    </Box>
}

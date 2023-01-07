import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Typography from '@mui/material/Typography'
import { NeuronView } from '../neuron/NeuronView'
import styles from './PrimeFront.module.scss'
import { Expression } from '../../businessLogic/data'
import { DataList } from '../dataList/DataList'
import { Neuron } from '../../businessLogic/neuron'
import { getScenarioURL } from '../../communication/urls'

export interface PrimeFrontProps {
    className?: string
}

export const PrimeFront = ({ className }: PrimeFrontProps) => {
    const searchParams = new URLSearchParams(document.location.search)
    const scenarioName = searchParams.get('scenario')!
    const agentName = searchParams.get('agent')!

    const [expressions, setExpressions] = React.useState<Record<string, Expression>>({})
    const [neurons, setNeurons] = React.useState<Record<string, Neuron>>({})
    const [shouldRefresh, setShouldRefresh] = React.useState<boolean>(true)
    const [selectedExpressionId, setSelectedExpressionId] = React.useState<string>('')

    const setData = (agent: any) => {
        const memory = agent.memory
        const keys: string[] = Object.keys(agent.memory)
        const neurons = {} as Record<string, Neuron>
        const expressions = {} as Record<string, Expression>
        keys.forEach(key => {
            const neuron = memory[key]
            neurons[key] = neuron
            expressions[key] = neuron.data
        })

        setNeurons(neurons)
        setExpressions(expressions)
    }

    const toggleRefresh = () => {
        setShouldRefresh(!shouldRefresh)
    }

    const refreshData = () => {
        if (shouldRefresh) {
            console.log(`Refreshing scenario ${scenarioName} and agent ${agentName}`)
            fetch(getScenarioURL(scenarioName, agentName))
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
        }
    }

    React.useEffect(() => {
        if (shouldRefresh) {
            const interval = setInterval(() => {
                refreshData()
            },1000)
           
            return () => clearInterval(interval)
        }
    }, [shouldRefresh])

    return <Box className={`${styles.root} ${className} ${styles.all}`}>
        <Box className={styles['left-panel']}>
            <Box className={styles['controls']}>
                <Box className={styles['context']}>
                    <Typography variant="subtitle1" display="inline">
                        Scenario: {scenarioName}
                    </Typography>
                </Box>
                <Box className={styles['context']}>
                    <Typography variant="subtitle1" display="inline">
                        Agent: {agentName}
                    </Typography>
                </Box>
                <Button onClick={toggleRefresh}>
                    Refreshing: {shouldRefresh + ''}
                </Button>
            </Box>
            <DataList expressions = {expressions} setSelectedExpressionId = {setSelectedExpressionId} selectedExpressionId = {selectedExpressionId}/>
        </Box>
        <Box className={styles['right-panel']}>
            {(expressions[selectedExpressionId]) ? <NeuronView neuron={neurons[selectedExpressionId]} scenarioName={scenarioName} agentName={agentName} /> : null}
        </Box>
    </Box>
}

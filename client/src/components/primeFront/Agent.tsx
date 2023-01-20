import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Typography from '@mui/material/Typography'
import { NeuronView } from '../neuron/NeuronView'
import styles from './Agent.module.scss'
import { DataList } from '../dataList/DataList'
import { Neuron } from '../../businessLogic/neuron'
import { getScenarioURL, getResetURL } from '../../communication/urls'

export interface AgentProps {
    className?: string
}

export const Agent = ({ className }: AgentProps) => {
    const searchParams = new URLSearchParams(document.location.search)
    const scenarioName = searchParams.get('scenario')!
    const agentName = searchParams.get('agent')!

    const [neurons, setNeurons] = React.useState<Record<string, Neuron>>({})
    const [shouldRefresh, setShouldRefresh] = React.useState<boolean>(true)
    const [isStable, setIsStable] = React.useState<boolean>(false)
    const [messageCount, setMessageCount] = React.useState<number>(0)
    const [selectedExpressionId, setSelectedExpressionId] = React.useState<string>('')

    const setData = (agent: any) => {
        const memory = agent.memory
        const keys: string[] = Object.keys(agent.memory)
        const neurons = {} as Record<string, Neuron>
        
        keys.forEach(key => {
            const neuron = memory[key]
            neurons[key] = neuron
        })

        setIsStable(agent.stable)
        if (agent.stable) {
            console.log('Stable, stopping refresh')
            setShouldRefresh(false)
        } else {
            console.log('Still not stable', isStable)
        }
        setNeurons(neurons)
        setMessageCount(agent.messageCount)
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

    const resetScenario = async () => {
        console.log(`Resetting scenario ${scenarioName}`)
        try {
            const body = {}
            const response = await fetch(getResetURL(scenarioName), {
                method: 'POST',
                body: JSON.stringify(body),
                headers: {
                    'Content-Type': 'application/json',
                    Accept: 'application/json',
                },
            })
      
            if (!response.ok) {
                throw new Error(`Error! status: ${response.status}`);
            }
    
            setShouldRefresh(true)

        } catch (err) {
          console.error(err)
          window.alert('Failed resetting scenario')
        }
    }

    React.useEffect(() => {
        refreshData()
        const interval = setInterval(() => {
            refreshData()
        },1000)
       
        return () => clearInterval(interval)
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
                        Agent: {agentName} ({isStable ? 'Stable' : 'Not stable'} {messageCount})
                    </Typography>
                </Box>
                <Button onClick={toggleRefresh}>
                    Refreshing: {shouldRefresh + ''}
                </Button>
                <Button onClick={resetScenario}>
                    Reset scenario
                </Button>
            </Box>
            <DataList className={styles['data-list']} neurons = {neurons} setSelectedExpressionId = {setSelectedExpressionId} selectedExpressionId = {selectedExpressionId}/>
        </Box>
        <Box className={styles['right-panel']}>
            {(neurons[selectedExpressionId]) ? 
                <NeuronView
                    neuron={neurons[selectedExpressionId]}
                    scenarioName={scenarioName}
                    agentName={agentName}
                    setSelectedExpressionId={setSelectedExpressionId}
                    setShouldRefresh={setShouldRefresh}
                /> : null}
        </Box>
    </Box>
}

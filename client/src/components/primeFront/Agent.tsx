import * as React from 'react'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import { NeuronView } from '../neuron/NeuronView'
import styles from './Agent.module.scss'
import { DataList } from '../dataList/DataList'
import { Controls } from '../controls/Controls'
import { Neuron } from '../../businessLogic/neuron'
import {RequestHandler} from '../../communication/RequestHandler'

export interface AgentProps {
    className?: string
}

export const Agent = ({ className }: AgentProps) => {
    const searchParams = new URLSearchParams(document.location.search)
    const scenarioName = searchParams.get('scenario')!
    const agentName = searchParams.get('agent')!
    document.title = scenarioName

    const [resetState, setResetState] = React.useState<number>(0)
    const [neurons, setNeurons] = React.useState<Record<string, Neuron>>({})
    const [shouldRefresh, setShouldRefresh] = React.useState<boolean>(true)
    const [isStable, setIsStable] = React.useState<boolean>(false)
    
    const [messageCount, setMessageCount] = React.useState<number>(0)
    const [selectedExpressionId, setSelectedExpressionId] = React.useState<string>('')
    const [filteredIds, setFilteredIds] = React.useState<string[]>([])
    const [focused, setFocused] = React.useState<number>(-1)
    const [filter, setFilter] = React.useState<string>('')
    const [requestHandler] = React.useState<RequestHandler>(new RequestHandler(scenarioName, agentName, setShouldRefresh))

    const inputRef = React.useRef<HTMLInputElement>(null)

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

    const refreshData = async () => {
        if (shouldRefresh) {
            console.log(`Refreshing scenario ${scenarioName} and agent ${agentName}`)
            
            try {
                const data = await requestHandler.getScenario()
                setData(data)
            } catch (err) {
                console.error('Failed to fetch', err)
            }
        }
    }

    React.useEffect(() => {
        refreshData()
        const interval = setInterval(() => {
            refreshData()
        },1000)
       
        return () => clearInterval(interval)
    }, [shouldRefresh])

    const reset = async () => {
        console.log(`Resetting scenario ${scenarioName}`)
        try {
            setSelectedExpressionId('')
            setNeurons({})
            await requestHandler.sendReset()
            setResetState(resetState + 1)
            setShouldRefresh(true)
        } catch (err) {
          console.error(err)
          window.alert('Failed resetting scenario')
        }   
    }

    const getCleanFilter = () => filter.replaceAll(' ', '')

    const addData = async (id: string) => {
        await requestHandler.sendAddData(id)
    }

    const onSubmit = async () => {
        const cleanId = getCleanFilter()
        if (selectedExpressionId === filteredIds[focused]) {
            await addData(selectedExpressionId)
        } else if (focused > -1) {
            setSelectedExpressionId(filteredIds[focused])
        } else if (neurons[cleanId]) {
            setSelectedExpressionId(cleanId)
        } else {
            await addData(filter)
        }
    }

    const focusOnInput = () => inputRef?.current?.focus()

    const onFilterKey = async (event: any) => {
        if (event.key === 'Enter') {
            event.preventDefault()
            await onSubmit()
            console.log('Submit')
            focusOnInput()
            inputRef?.current?.select()
        }

        if (event.key === 'Escape') {
            event.preventDefault()
            focusOnInput()
            if (selectedExpressionId !== '') {
                setSelectedExpressionId('')
            } else {
                setFocused(-1)
            }
        }

        if (event.key === 'ArrowDown') {
            event.preventDefault()
            focusOnInput()
            setFocused((focused + 1) % filteredIds.length)
        }
        
        if (event.key === 'ArrowUp') {
            event.preventDefault()
            focusOnInput()
            setFocused((focused - 1 + filteredIds.length) % filteredIds.length)
        }
    }

    return <Box onKeyDown={onFilterKey}
        className={`${styles.root} ${className} ${styles.all}`}>
        <Box className={styles['left-panel']}>
            <Box>
                <Typography variant="subtitle1" display="inline">
                    <b>Scenario:</b> {scenarioName}  <b>Agent:</b> {agentName} ({isStable ? 'Stable' : 'Not stable'} {messageCount})
                </Typography>
            </Box>
            <Controls className={styles['data-list']}
                scenarioName={scenarioName}
                agentName={agentName}
                setShouldRefresh={setShouldRefresh}
                setSelectedExpressionId={setSelectedExpressionId}
                setFilteredIds={setFilteredIds}
                filteredIds={filteredIds}
                reset={reset}
                neurons={neurons}
                shouldRefresh={shouldRefresh}
                selectedExpressionId={selectedExpressionId}
                focused={focused}
                setFocused={setFocused}
                filter={filter}
                setFilter={setFilter}
                requestHandler={requestHandler}
                inputRef={inputRef}
            />
            <DataList className={styles['data-list']} key = {resetState}
                neurons = {neurons}
                setSelectedExpressionId = {setSelectedExpressionId}
                selectedExpressionId = {selectedExpressionId}
                filteredIds={filteredIds}
                focused={focused}
                setFocused={setFocused}
            />
        </Box>
        <Box className={styles['right-panel']}>
            {(neurons[selectedExpressionId]) ? 
                <NeuronView
                    neuron={neurons[selectedExpressionId]}
                    scenarioName={scenarioName}
                    agentName={agentName}
                    selectedExpressionId={selectedExpressionId}
                    setSelectedExpressionId={setSelectedExpressionId}
                    setShouldRefresh={setShouldRefresh}
                    requestHandler={requestHandler}
                /> : null}
        </Box>
    </Box>
}

import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import TextField from '@mui/material/TextField'
import Checkbox from '@mui/material/Checkbox'
import FormGroup from '@mui/material/FormGroup'
import FormControlLabel from '@mui/material/FormControlLabel'
import FormControl from '@mui/material/FormControl'
import FormLabel from '@mui/material/FormLabel'
import styles from './Controls.module.scss'
import { Neuron } from '../../businessLogic/neuron'
import { RequestHandler } from '../../communication/RequestHandler'

export interface ControlsProps {
    className?: string
    scenarioName: string
    agentName: string
    setShouldRefresh: Function
    setSelectedExpressionId: Function
    setFilteredIds: Function
    filteredIds: string[]
    reset: Function
    neurons: Record<string, Neuron>
    shouldRefresh: boolean
    selectedExpressionId: string
    focused: number
    setFocused: Function
    filter: string
    setFilter: Function
    requestHandler: RequestHandler
    inputRef: React.Ref<HTMLInputElement>
    setLoadGraph: Function
    loadGraph: boolean
}

export const Controls = ({
    className,
    scenarioName,
    agentName,
    setShouldRefresh,
    setSelectedExpressionId,
    setFilteredIds,
    filteredIds,
    reset,
    neurons,
    shouldRefresh,
    selectedExpressionId,
    focused,
    setFocused,
    filter,
    setFilter,
    requestHandler,
    inputRef,
    setLoadGraph,
    loadGraph
}: ControlsProps) => {

    const [isPaused, setIsPaused] = React.useState<boolean>(false)
    const [showEmpty, setShowEmpty] = React.useState<boolean>(false)
    const [showPositive, setShowPositive] = React.useState<boolean>(true)
    const [showNegative, setShowNegative] = React.useState<boolean>(true)

    const toggleRefresh = () => {
        setShouldRefresh(!shouldRefresh)
    }

    const resetScenario = async () => {
        setFocused(-1)
        reset()
        setIsPaused(false)
    }

    const pauseScenario = async () => {
        await requestHandler.sendPause()
        setShouldRefresh(false)
        setIsPaused(true)
    }

    const resumeScenario = async () => {
        await requestHandler.sendResume()
        setShouldRefresh(true)
        setIsPaused(false)
    }

    const addDataClick = () => requestHandler.sendAddData(filter)

    const togglePause = async () => {
        if (isPaused) {
            await resumeScenario()
        } else {
            await pauseScenario()
        }
    }

    React.useEffect(() => {
        const matchesFilter = (id: string) => {
            const regexStr = filter.toLowerCase().replaceAll(' ', '.*').replaceAll('(', '\\(').replaceAll(')', '\\)')
            const re = new RegExp(regexStr)
            const idMatch = !!id.toLowerCase().match(re)
            
            if (!idMatch) {
                return false
            }

            const neuron = neurons[id]
            const confidenceNode = neuron?.nodes['confidence']
            const confidence = confidenceNode?.props['confidence'] || '0|0|0'
            const strength = parseFloat(confidence.split('|')[0])

            return ((strength === 0.0 && showEmpty) || (strength > 0.0 && showPositive) || (strength < 0.0 && showNegative))
        }

        const newFiltered = Object.keys(neurons).filter(matchesFilter).sort(compareIds)
        setFilteredIds(newFiltered)

        if (!neurons[selectedExpressionId]) {
            console.log('Removing selected')
            setSelectedExpressionId('')
        }
    }, [neurons, filter, setFilteredIds, setSelectedExpressionId, selectedExpressionId, showEmpty, showPositive, showNegative])

    const setFilterValue = (value: string) => {
        setFilter(value)
    }

    const toggleGraph = () => {
        setLoadGraph(!loadGraph)
    }

    const toggleShowEmpty = () => {
        setShowEmpty(!showEmpty)
    }

    const toggleShowPositive = () => {
        setShowPositive(!showPositive)
    }

    const toggleShowNegative = () => {
        setShowNegative(!showNegative)
    }

    const filterBoxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const value = event.target.value
        setFilterValue(value)
        setFocused(-1)
    }

    const handleIgniteClick = async () => {
        await requestHandler.sendIgnite(selectedExpressionId)
    }

    const handlePositiveSenseClick = async () => {
        await requestHandler.sendSensePositive(selectedExpressionId)
    }

    const handleNegativeSenseClick = async () => {
        await requestHandler.sendSenseNegative(selectedExpressionId)
    }

    const compareIds = (id1: string, id2: string) => id1 === id2 ? 0 : (id1 > id2 ? 1 : -1)

    return <Box className={`${styles.root} ${className} ${styles.all}`}>
        <Box>
           <TextField inputRef={inputRef} id="outlined-basic" autoFocus autoComplete='off' fullWidth variant="outlined" onChange={filterBoxChange}/>
        </Box>
        <Box>
            <Button onClick={handlePositiveSenseClick}>
                Sense+
            </Button>
            <Button onClick={handleNegativeSenseClick}>
                Sense-
            </Button>
            <Button onClick={addDataClick}>
                Add data
            </Button>
            <Button onClick={handleIgniteClick}>
                Ignite
            </Button>
            <Button onClick={resetScenario}>
                Reset scenario
            </Button>
            <Button onClick={toggleRefresh}>
                Refreshing: {shouldRefresh + ''}
            </Button>
            <Button onClick={togglePause}>
                {isPaused ? 'Resume' : 'Pause'}
            </Button>
        </Box>
        <Box>
            <FormControl component="fieldset">
                <FormGroup aria-label="position" row>
                    <FormControlLabel
                        value="positive"
                        checked={showPositive}
                        control={<Checkbox />}
                        onChange={toggleShowPositive}
                        label="positive"
                        labelPlacement="start"
                    />
                    <FormControlLabel
                        value="negative"
                        checked={showNegative}
                        control={<Checkbox />}
                        onChange={toggleShowNegative}
                        label="negative"
                        labelPlacement="start"
                    />
                    <FormControlLabel
                        value="empty"
                        checked={showEmpty}
                        control={<Checkbox />}
                        onChange={toggleShowEmpty}
                        label="empty"
                        labelPlacement="start"
                    />
                    <FormControlLabel
                        value="graph"
                        checked={loadGraph}
                        control={<Checkbox />}
                        onChange={toggleGraph}
                        label="graph"
                        labelPlacement="start"
                    />
                </FormGroup>
            </FormControl>
        </Box>
    </Box>
}

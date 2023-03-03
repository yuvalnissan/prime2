import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import TextField from '@mui/material/TextField'
import Checkbox from '@mui/material/Checkbox'
import FormGroup from '@mui/material/FormGroup'
import FormControlLabel from '@mui/material/FormControlLabel'
import FormControl from '@mui/material/FormControl'
import styles from './Controls.module.scss'
import { Neuron } from '../../businessLogic/neuron'
import { RequestHandler } from '../../communication/RequestHandler'

export interface ControlsProps {
    className?: string
    scenarioName: string
    agentName: string
    setSelectedExpressionId: Function
    setFilteredIds: Function
    filteredIds: string[]
    neurons: Record<string, Neuron>
    selectedExpressionId: string
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
    setSelectedExpressionId,
    setFilteredIds,
    filteredIds,
    neurons,
    selectedExpressionId,
    setFocused,
    filter,
    setFilter,
    requestHandler,
    inputRef,
    setLoadGraph,
    loadGraph
}: ControlsProps) => {

    const [showEmpty, setShowEmpty] = React.useState<boolean>(false)
    const [showPositive, setShowPositive] = React.useState<boolean>(true)
    const [showNegative, setShowNegative] = React.useState<boolean>(true)

    const addDataClick = () => requestHandler.sendAddData(filter)

    React.useEffect(() => {
        const firstFilterPart = () => {
            const separatorIndex = filter.indexOf(' ')
            if (separatorIndex === -1) {
                return filter
            }

            return filter.substring(0, separatorIndex)
        }

        const matchesFilter = (id: string) => {
            if (!filter.startsWith(' ') && !id.startsWith(firstFilterPart())) {
                return false
            }

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

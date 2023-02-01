import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import TextField from '@mui/material/TextField'
import styles from './Controls.module.scss'
import { Neuron } from '../../businessLogic/neuron'
import { getPauseURL, getResumeURL, getAddDataURL, getMessageURL, postToUrl } from '../../communication/urls'

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
    setFocused
}: ControlsProps) => {

    const [isPaused, setIsPaused] = React.useState<boolean>(false)
    const [filter, setFilter] = React.useState<string>('')

    const toggleRefresh = () => {
        setShouldRefresh(!shouldRefresh)
    }

    const getCleanFilter = () => filter.replaceAll(' ', '')

    const resetScenario = async () => {
        setFocused(-1)
        reset()
        setIsPaused(false)
    }

    const pauseScenario = async () => {
        console.log(`Pausing scenario ${scenarioName}`)
        try {
            await postToUrl(getPauseURL(scenarioName), {})
            setShouldRefresh(false)
            setIsPaused(true)
        } catch (err) {
          console.error(err)
          window.alert('Failed pausing scenario')
        }
    }

    const resumeScenario = async () => {
        console.log(`Resuming scenario ${scenarioName}`)
        try {
            await postToUrl(getResumeURL(scenarioName), {})
            setShouldRefresh(true)
            setIsPaused(false)
        } catch (err) {
          console.error(err)
          window.alert('Failed resuming scenario')
        }
    }

    const addData = async (dataToAdd: string) => {
        console.log(`Adding data: ${dataToAdd}`)
        try {
            await postToUrl(getAddDataURL(scenarioName, agentName), dataToAdd)
            setShouldRefresh(true)
        } catch (err) {
          console.error(err)
          window.alert('Failed sending new data')
        }
    }

    const addDataClick = () => addData(filter)

    const togglePause = async () => {
        if (isPaused) {
            await resumeScenario()
        } else {
            await pauseScenario()
        }
    }

    const matchesFilter = (id: string) => {
        const regexStr = filter.toLowerCase().replaceAll(' ', '.*').replaceAll('(', '\\(').replaceAll(')', '\\)')
        const re = new RegExp(regexStr)

        return !!id.toLowerCase().match(re)
    }

    React.useEffect(() => {
        const newFiltered = Object.keys(neurons).filter(matchesFilter).sort(compareIds)
        setFilteredIds(newFiltered)

        if (!neurons[selectedExpressionId]) {
            console.log('Removing selected')
            setSelectedExpressionId('')
        }
    }, [neurons, filter])

    const setFilterValue = (value: string) => {
        setFilter(value)
    }

    const filterBoxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const value = event.target.value
        setFilterValue(value)
        setFocused(-1)
    }

    const filterSubmit = async () => {
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

    const sendMessage = async (type: string) => {
        console.log('Sending message', type)
        await postToUrl(getMessageURL(scenarioName, agentName), {type, data: neurons[selectedExpressionId].data})
        setShouldRefresh(true)
    }

    const handleIgniteClick = async () => {
        await sendMessage('ignite')
    }

    const handlePositiveSenseClick = async () => {
        await sendMessage('sense-positive')
    }

    const handleNegativeSenseClick = async () => {
        await sendMessage('sense-negative')
    }

    const onFilterKey = async (event: any) => {
        console.log(event.key)

        if (event.key === 'Enter') {
            await filterSubmit()
            event.preventDefault()
            event.target.select()
        }

        if (event.key === 'Escape') {
            event.preventDefault()
            if (focused === -1) {
                setSelectedExpressionId('')
            } else {
                setFocused(-1)
            }
        }

        if (event.key === 'ArrowDown') {
            event.preventDefault()
            setFocused((focused + 1) % filteredIds.length)
        }
        
        if (event.key === 'ArrowUp') {
            event.preventDefault()
            setFocused((focused - 1 + filteredIds.length) % filteredIds.length)
        }
    }

    const compareIds = (id1: string, id2: string) => id1 === id2 ? 0 : (id1 > id2 ? 1 : -1)

    return <Box className={`${styles.root} ${className} ${styles.all}`}>
        <Box>
           <TextField id="outlined-basic" autoFocus fullWidth variant="outlined" onChange={filterBoxChange} onKeyDown={onFilterKey}/>
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
    </Box>
}

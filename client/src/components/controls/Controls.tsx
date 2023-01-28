import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import TextField from '@mui/material/TextField'
import Autocomplete from '@mui/material/Autocomplete'
import styles from './Controls.module.scss'
import { Neuron } from '../../businessLogic/neuron'
import { getResetURL, getPauseURL, getResumeURL, getAddDataURL, getMessageURL, postToUrl } from '../../communication/urls'

export interface ControlsProps {
    className?: string
    scenarioName: string
    agentName: string
    setShouldRefresh: Function
    setSelectedExpressionId: Function
    setFilteredIds: Function
    reset: Function
    neurons: Record<string, Neuron>
    shouldRefresh: boolean
    selectedExpressionId: string
}

export const Controls = ({
    className,
    scenarioName,
    agentName,
    setShouldRefresh,
    setSelectedExpressionId,
    setFilteredIds,
    reset,
    neurons,
    shouldRefresh,
    selectedExpressionId
}: ControlsProps) => {

    const [isPaused, setIsPaused] = React.useState<boolean>(false)
    const [filter, setFilter] = React.useState<string>('')

    const toggleRefresh = () => {
        setShouldRefresh(!shouldRefresh)
    }

    const getCleanFilter = () => filter.replaceAll(' ', '')

    const resetScenario = async () => {
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

    const addData = async () => {
        console.log(`Adding data: ${filter}`)
        try {
            await postToUrl(getAddDataURL(scenarioName, agentName), filter)
            setShouldRefresh(true)
        } catch (err) {
          console.error(err)
          window.alert('Failed sending new data')
        }
    }

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
        setFilteredIds(Object.keys(neurons).filter(matchesFilter).sort(compareIds))
        if (!!neurons[filter]) {
            setSelectedExpressionId(filter)
        } else {
            setSelectedExpressionId('')
        }
    }, [neurons, filter])

    const setFilterValue = (value: string) => {
        setFilter(value)
        console.log(value)
        if (!!neurons[value]) {
            setSelectedExpressionId(value)
        }
    }

    const filterBoxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const value = event.target.value
        setFilterValue(value)
    }

    const filterSubmit = async () => {
        const cleanId = getCleanFilter()
        if (neurons[cleanId]) {
            setSelectedExpressionId(cleanId)
        } else {
            await addData()
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
    }

    const compareIds = (id1: string, id2: string) => id1 === id2 ? 0 : (id1 > id2 ? 1 : -1)

    const filterOptions = (options: string[], { inputValue }: {inputValue: string}) => options.filter(id => {
        const regexStr = inputValue.toLowerCase().replaceAll(' ', '.*').replaceAll('(', '\\(').replaceAll(')', '\\)')
        const re = new RegExp(regexStr)

        return !!id.toLowerCase().match(re)
    })

    return <Box className={`${styles.root} ${className} ${styles.all}`}>
        <Box>
            <Autocomplete
                id="auto-complete"
                className={styles['search']}
                options={Object.keys(neurons)}
                filterOptions={filterOptions}
                onInputChange={(event, newInputValue) => {
                    setFilterValue(newInputValue)
                }}
                renderInput={(params) => <TextField {...params} id="outlined-basic" fullWidth variant="outlined" onChange={filterBoxChange} onKeyDown={onFilterKey}/>}
            />
        </Box>
        <Box>
            <Button onClick={handlePositiveSenseClick}>
                Sense+
            </Button>
            <Button onClick={handleNegativeSenseClick}>
                Sense-
            </Button>
            <Button onClick={addData}>
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

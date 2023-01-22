import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Typography from '@mui/material/Typography'
import styles from './NeuronView.module.scss'
import { Link, Neuron } from '../../businessLogic/neuron'
import { DataView } from '../data/DataView'
import { NodeList } from '../nodeList/NodeList'
import { LinkList } from '../linkList/LinkList'
import { getMessageURL } from '../../communication/urls'
import { Data } from '../../businessLogic/data'


export interface NeuronViewProps {
  className?: string
  neuron: Neuron
  scenarioName: string
  agentName: string
  setSelectedExpressionId: Function
  setShouldRefresh: Function
}

const compare = (a: Link, b: Link) => (a.to.id > b.to.id) ? 1 : ((b.to.id > a.to.id) ? -1 : 0)

export const NeuronView = ({neuron, scenarioName, agentName, setSelectedExpressionId, setShouldRefresh, className}: NeuronViewProps) => {
    const sendMessage = async (type: string, data: Data) => {
        try {
            const body = {data, type}
            const response = await fetch(getMessageURL(scenarioName, agentName), {
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
            window.alert('Failed sending message')
          }
    }
  
    const handleIgniteClick = async () => {
        console.log('Sending Ignite')
        await sendMessage('ignite', neuron.data)
    }

    const handlePositiveSenseClick = async () => {
        console.log('Sending Positive Sense')
        await sendMessage('sense-positive', neuron.data)
    }

    const handleNegativeSenseClick = async () => {
        console.log('Sending Negative Sense')
        await sendMessage('sense-negative', neuron.data)
    }

    const confidenceNode = neuron?.nodes['confidence']
    const confidence = confidenceNode.props['confidence']

    return <Box className={`${styles.root} ${className} ${styles.all}`}>
        <Box className={styles['details']} >
            <Box>
                <Button onClick={handleIgniteClick} >Ignite</Button>
                <Button onClick={handlePositiveSenseClick} >Sense+</Button>
                <Button onClick={handleNegativeSenseClick} >Sense-</Button>
            </Box>
            <Box>
                <Typography variant="subtitle1" display="inline">
                    <b>Confidence: {confidence}</b>
                </Typography>
                <DataView expression={neuron.data} />
            </Box>
        </Box>
        <Box className={styles['list']} >
            <NodeList nodes={neuron.nodes} />
            <LinkList links={neuron.links.sort(compare)} setSelectedExpressionId={setSelectedExpressionId} />
        </Box>
    </Box>
}

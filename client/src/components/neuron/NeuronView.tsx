import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import styles from './NeuronView.module.scss'
import { Neuron } from '../../businessLogic/neuron'
import { DataView } from '../data/DataView'
import { NodeList } from '../nodeList/NodeList'
import { getMessageURL } from '../../communication/urls'


export interface NeuronViewProps {
  className?: string;
  neuron: Neuron,
  scenarioName: string,
  agentName: string
}

export const NeuronView = ({neuron, scenarioName, agentName, className}: NeuronViewProps) => {
  const handleClick = async () => {
    console.log('Trying')
    try {
      const body = {
        data: neuron.data,
        type: 'ignite'
      }
      const response = await fetch(getMessageURL(scenarioName, agentName), {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
        },
      });
  
      if (!response.ok) {
        throw new Error(`Error! status: ${response.status}`);
      }
    } catch (err) {
      console.error(err)
      window.alert('Failed sending message')
    }
  }
  return <Box className={`${styles.root} ${className} ${styles.all}`}>
    <Button onClick={handleClick} >Ignite</Button>
    <DataView expression={neuron.data} />
    <NodeList nodes={neuron.nodes} />
  </Box>
}

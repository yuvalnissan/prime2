import * as React from 'react'
import Box from '@mui/material/Box'
import styles from './NeuronView.module.scss'
import { Neuron } from '../../businessLogic/neuron'
import { DataView } from '../data/DataView'
import { NodeList } from '../nodeList/NodeList'


export interface NeuronViewProps {
  className?: string;
  neuron: Neuron
}

export const NeuronView = ({neuron, className }: NeuronViewProps) => {
  return <Box className={`${styles.root} ${className} ${styles.all}`}>
    <DataView expression={neuron.data} />
    <NodeList nodes={neuron.nodes} />
  </Box>
}

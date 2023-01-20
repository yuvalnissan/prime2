import * as React from 'react'
import Box from '@mui/material/Box'
import Adjust from '@mui/icons-material/Adjust'
import Typography from '@mui/material/Typography'
import styles from './NodeList.module.scss'
import { Node } from '../../businessLogic/neuron'
import Divider from '@mui/material/Divider'


export interface NodeViewProps {
  name: string
  node: Node
}

export interface NodeListProps {
  className?: string;
  nodes: Record<string, Node>
}


export const NodeList = ({nodes, className }: NodeListProps) => {

  const NodeView = ({name, node}: NodeViewProps) => {
    return (
      <Box className={styles['node']} key={name}>
        <Divider />
        <Adjust />
        <Typography variant="subtitle1" display="inline">
          {name}
        </Typography>
        <Box>
          {Object.entries(node.props).map(([prop, value]) => {
            return <Box><Typography variant="subtitle1" display="inline" key={prop}>
              {prop}: {value}
            </Typography></Box>
          })}
        </Box>
      </Box>
    )
  }

  return (
    <Box className={`${styles.root} ${className} ${styles.all}`}>
      {Object.entries(nodes).map(([name, node]) => NodeView({node, name}))}
    </Box>
  )
}

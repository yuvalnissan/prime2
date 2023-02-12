import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Typography from '@mui/material/Typography'
import styles from './NeuronView.module.scss'
import { Link, Neuron } from '../../businessLogic/neuron'
import { DataView } from '../data/DataView'
import { NodeList } from '../nodeList/NodeList'
import { LinkList } from '../linkList/LinkList'
import { RequestHandler } from '../../communication/RequestHandler'


export interface NeuronViewProps {
  className?: string
  neuron: Neuron
  scenarioName: string
  agentName: string
  selectedExpressionId: string
  setSelectedExpressionId: Function
  setShouldRefresh: Function
  requestHandler: RequestHandler
}

const compare = (a: Link, b: Link) => (a.to.id > b.to.id) ? 1 : ((b.to.id > a.to.id) ? -1 : 0)

export const NeuronView = ({neuron, scenarioName, agentName, selectedExpressionId, setSelectedExpressionId, setShouldRefresh, requestHandler, className}: NeuronViewProps) => {
    const handleIgniteClick = async () => {
        await requestHandler.sendIgnite(selectedExpressionId)
    }

    const handlePositiveSenseClick = async () => {
        await requestHandler.sendSensePositive(selectedExpressionId)
    }

    const handleNegativeSenseClick = async () => {
        await requestHandler.sendSenseNegative(selectedExpressionId)
    }

    const copyPopup = () => {
        window.prompt("Selected ID", selectedExpressionId);
    }

    const confidenceNode = neuron?.nodes['confidence']
    const confidence = confidenceNode.props['confidence']

    return <Box className={`${styles.root} ${className} ${styles.all}`}>
        <Box className={styles['view']}>
            <Box>
                <Box>
                    <Button onClick={handleIgniteClick} >Ignite</Button>
                    <Button onClick={handlePositiveSenseClick} >Sense+</Button>
                    <Button onClick={handleNegativeSenseClick} >Sense-</Button>
                    <Button onClick={copyPopup} >ID</Button>
                </Box>
                <Box>
                    <Typography variant="subtitle1" display="inline">
                        <b>Confidence: {confidence}</b>
                    </Typography>
                    <DataView expression={neuron.data} />
                </Box>
            </Box>
            <Box>
                <NodeList nodes={neuron.nodes} />
                <LinkList links={neuron.links.sort(compare)} setSelectedExpressionId={setSelectedExpressionId} />
            </Box>
        </Box>
    </Box>
}

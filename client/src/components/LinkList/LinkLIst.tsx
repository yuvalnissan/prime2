import * as React from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import ArrowRightAlt from '@mui/icons-material/ArrowRightAlt'
import Typography from '@mui/material/Typography'
import styles from './LinkList.module.scss'
import { Link } from '../../businessLogic/neuron'
import { DataView } from '../data/DataView'
import Divider from '@mui/material/Divider'


export interface LinkViewProps {
  link: Link
}

export interface LinkListProps {
  className?: string;
  links: Link[]
  setSelectedExpressionId: Function
}


export const LinkList = ({links, className, setSelectedExpressionId }: LinkListProps) => {
  const LinkView = ({link}: LinkViewProps) => {
    const handleClick = () => setSelectedExpressionId(link.to.id)

    return (
      <Box className={styles['link']} key={link.type + '-' + link.to.id}>
        <Divider />
        <ArrowRightAlt />
        <Typography variant="subtitle1" display="inline">
          {link.type}
        </Typography>
        <Button onClick={handleClick} >Go</Button>
        <DataView expression={link.to} />
      </Box>
    )
  }

  return (
    <Box className={`${styles.root} ${className} ${styles.all}`}>
      {links.map(link => LinkView({link}))}
    </Box>
  )
}

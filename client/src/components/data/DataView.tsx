import * as React from 'react'
import TreeView from '@mui/lab/TreeView'
import Box from '@mui/material/Box'
import { alpha, styled } from '@mui/material/styles'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'
import Typography from '@mui/material/Typography'
import ChevronRightIcon from '@mui/icons-material/ChevronRight'
import TreeItem, { TreeItemProps, treeItemClasses } from '@mui/lab/TreeItem'
import styles from './DataView.module.scss'
import { Expression } from '../../businessLogic/data'


export interface DataViewProps {
  className?: string
  expression: Expression
}

const StyledTreeItem = styled((props: TreeItemProps) => (
  <TreeItem {...props} />
))(({ theme }) => ({
  [`& .${treeItemClasses.iconContainer}`]: {
    '& .close': {
      opacity: 0.3,
    },
  },
  [`& .${treeItemClasses.group}`]: {
    marginLeft: 15,
    paddingLeft: 18,
    borderLeft: `1px dashed ${alpha(theme.palette.text.primary, 0.4)}`,
  },
}))

const getExpressionIds = (expression: Expression) => {
  const ids = [expression.id]
  expression.expressions?.forEach(subExpression => ids.push(...getExpressionIds(subExpression)))

  return ids
}

export const DataView = ({expression, className }: DataViewProps) => {
  const [expanded, setExpanded] = React.useState<boolean>(false)
  const allIds = getExpressionIds(expression)
  
  const handleToggle = (event: React.SyntheticEvent, nodeIds: string[]) => {
    // setExpanded(nodeIds)
  }

  const DataNode = ({ expression }: DataViewProps) => {
    return <StyledTreeItem
      nodeId={expression.id}
      label={`${expression.negative ? 'not ' : ''} ${(expression.type === 'value') ? expression.id : expression.type}`}>
        {expression.expressions?.map((subExpression: Expression) => <DataNode expression={subExpression} key={subExpression.id}/>)}
    </StyledTreeItem>
  }

  const toggleExpanded = () => {
    setExpanded(!expanded)
  }

  return <Box className={`${styles.root} ${className} ${styles.all}`} onClick={toggleExpanded}>
    {
      !expanded ? 
        <Typography variant="subtitle1" display="inline">
            {expression.id.replaceAll(',', ', ')}
        </Typography> :
        <TreeView 
        aria-label="Data"
        defaultCollapseIcon={<ExpandMoreIcon />}
        defaultExpandIcon={<ChevronRightIcon />}
        expanded={allIds} // {expanded}
        onNodeToggle={handleToggle}
      >
        <DataNode expression={expression} />
      </TreeView>
    }
  </Box>
}

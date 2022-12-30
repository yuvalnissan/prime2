import * as React from 'react';
import TreeView from '@mui/lab/TreeView';
import Box from '@mui/material/Box';
import { alpha, styled } from '@mui/material/styles';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import TreeItem, { TreeItemProps, treeItemClasses } from '@mui/lab/TreeItem';
import styles from './Data.module.scss';
import { Expression } from '../../businessLogic/data';


export interface DataViewProps {
  className?: string;
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
  // const [expanded, setExpanded] = React.useState<string[]>([]);
  const allIds = getExpressionIds(expression)
  
  const handleToggle = (event: React.SyntheticEvent, nodeIds: string[]) => {
    // setExpanded(nodeIds);
  }

  const DataNode = ({ expression }: DataViewProps) => {
    return <StyledTreeItem
      nodeId={expression.id}
      label={`${expression.negative ? 'not ' : ''} ${expression.val} (${expression.type})`}>
        {expression.expressions?.map((subExpression: Expression) => <DataNode expression={subExpression} key={subExpression.id}/>)}
    </StyledTreeItem>
  }

  return <Box className={`${styles.root} ${className} ${styles.all}`}>
    <TreeView 
      aria-label="Data"
      defaultCollapseIcon={<ExpandMoreIcon />}
      defaultExpandIcon={<ChevronRightIcon />}
      expanded={allIds} // {expanded}
      onNodeToggle={handleToggle}
    >
      <DataNode expression={expression} />
    </TreeView>
  </Box>
}

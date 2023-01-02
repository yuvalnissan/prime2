import * as React from 'react';
import Box from '@mui/material/Box';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import InboxIcon from '@mui/icons-material/Inbox';
import { Expression } from '../../businessLogic/data';

export interface DataListProps {
  className?: string;
  expressions: Record<string, Expression>,
  setSelectedExpressionId: Function,
  selectedExpressionId: string
}

export const DataList = ({expressions, selectedExpressionId, setSelectedExpressionId, className }: DataListProps) => {
  const handleListItemClick = (
    event: React.MouseEvent<HTMLDivElement, MouseEvent>,
    id: string,
  ) => {
    setSelectedExpressionId(id);
  }

  const ListNode = (expression: Expression) => {
    return <ListItemButton
    key = { expression.id}
    selected={selectedExpressionId === expression.id}
    onClick={(event) => handleListItemClick(event, expression.id)}
  >
    <ListItemIcon>
      <InboxIcon />
    </ListItemIcon>
    <ListItemText primary={expression.id.replaceAll(',', ', ')} />
  </ListItemButton>
  }

  return (
    <Box sx={{ width: '100%', maxWidth: 360, bgcolor: 'background.paper' }}>
      <List component="nav" aria-label="main mailbox folders">
        {Object.values(expressions).map(expression => ListNode(expression))}
      </List>
    </Box>
  )
}

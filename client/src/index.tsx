import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import { Agent } from './components/primeFront/Agent';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <React.StrictMode>
    <Agent/>
  </React.StrictMode>
);

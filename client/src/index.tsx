import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import { PrimeFront } from './components/primeFront/PrimeFront';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <React.StrictMode>
    <PrimeFront/>
  </React.StrictMode>
);

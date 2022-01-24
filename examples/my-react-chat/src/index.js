import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {AdamaConnection, AdamaTree} from 'adama-ws-client';

window.Adama = new AdamaConnection("ws://adama-lb-us-east-2-2073537616.us-east-2.elb.amazonaws.com/s");
window.Adama.start();

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();

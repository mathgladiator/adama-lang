import React from 'react';
import './App.css';

export default class App extends React.Component {
    state = {
        name: "UserName"
    };

    render() {
        return (
            <div className="App">
                <header className="App-header">
                    This is the beginning... {this.state.name}
                </header>
            </div>
        );
    }
}
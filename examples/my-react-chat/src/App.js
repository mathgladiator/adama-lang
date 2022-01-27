import React from 'react';
import './App.css';
import {
    fileOpen,
    supported,
} from 'browser-fs-access';

export default class App extends React.Component {
    state = {
        name: "Connecting..."
    };

    componentDidMount() {
        var self = this;
        window.Adama.wait_connected().then(function() {
                self.setState({name:"Connected"});
                var connection = window.Adama.ConnectionCreate(
                    "eyJhbGciOiJFUzI1NiJ9.eyJzdWIiOiIxIiwiaXNzIjoiYWRhbWEifQ.eqo02oPRxALrmHUKRaUNHZWyr2cPLkP470gzuE1EjYEn1-VZDlYlh5cz-osZbdBSxuwC2nBKA7-_399kfCO-2A",
                    "demo1",
                    "test1",
                    {
                        next: function(payload) {
                            self.setState({name:JSON.stringify(payload)});
                        },
                        finish: function() {
                        },
                        failure: function(code) {
                        }
                    });
                window.setInterval(() => {
                    connection.send("foo", {}, {
                        success: function() {
                        },
                        failure: function(code) {

                        }
                    });
                }, 5000);
            }
        );
    }

    async onclick() {
        var fileHandle = await fileOpen();
        console.log(fileHandle);

        // TODO: for each chunk, compute md5, send to adama, then complete
        console.log(await fileHandle.slice(0, 65536).arrayBuffer());

    }

    render() {
        return (
            <div className="App">
                <header className="App-header">
                    This is the beginning... {this.state.name}
                </header>
                <button onClick={this.onclick}>Show Me</button>
            </div>
        );
    }
}
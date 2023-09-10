/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as path from 'path';
import * as net from 'net';
import * as vscode from 'vscode';
import { workspace, ExtensionContext } from 'vscode';
import { LanguageClient, LanguageClientOptions, StreamInfo } from 'vscode-languageclient/node';


let client: LanguageClient;
const connectionInfo = {
    port: 2423,
    host: "127.0.0.1"
};
let extensionContext: ExtensionContext;

let adamaTerminal: vscode.Terminal | undefined;

function writeToTerminal(...messages: string[]) {
    if (!adamaTerminal) {
        adamaTerminal = vscode.window.createTerminal('Adama Logs');
    }
    adamaTerminal.show();
    for (const message of messages) {
        adamaTerminal.sendText(`echo "${message}"`);
    }
}


export function activate(context: ExtensionContext) {
    writeToTerminal('Activating Adama extension...');
    extensionContext = context;
    startAdamaConfigCommand(context);
    startLanguageClient(context);
}

function startAdamaConfigCommand(context: ExtensionContext) {
    writeToTerminal('Setting up Adama Configuration Command...');
    const disposable = vscode.commands.registerCommand('showAdamaConfig', () => {
        createAdamaPanel();
    });
    context.subscriptions.push(disposable);
}

function startLanguageClient(context: ExtensionContext) {
    writeToTerminal('Starting Language Client...');
    const serverOptions = getServerOptions(context);
    const clientOptions: LanguageClientOptions = {
		documentSelector: [{ scheme: 'file', language: 'adama' }],
		synchronize: { fileEvents: workspace.createFileSystemWatcher('**/*.*') }
	};

    client = new LanguageClient(
        'LanguageServer', 
        'Language Server', 
        serverOptions, 
        clientOptions
    );

    client.start();

    vscode.languages.onDidChangeDiagnostics((diagnosticChangeEvent) => {
        writeToTerminal("Diagnostics changed for: " + JSON.stringify(diagnosticChangeEvent.uris));
    });    
}

function getServerOptions(context: ExtensionContext) {
    writeToTerminal('Fetching server options...');
    const serverModule = context.asAbsolutePath(path.join('server', 'out', 'server.js'));
    
    return () => {
        const socket = net.connect(connectionInfo);
        socket.on('connect', () => {
            writeToTerminal('Successfully connected to server.');
        });
        socket.on('error', (err) => {
            console.error('Error connecting to server:', err);
        });
        return Promise.resolve({ writer: socket, reader: socket });
    };    
}


function createAdamaPanel() {
    const panel = vscode.window.createWebviewPanel('adamaConfig', 'Adama Config', vscode.ViewColumn.One, { enableScripts: true });

    panel.webview.onDidReceiveMessage(message => {
        if (message.command === 'enableLSP') {
            handleConnectionUpdate(message.host, message.port);
        }
    }, undefined, extensionContext.subscriptions);

    panel.webview.html = generateWebviewHTML();
}

function handleConnectionUpdate(host: string, port: string) {
    writeToTerminal(`Updating connection to Host: ${host}, Port: ${port}`);
    if (isValidHost(host) && isValidPort(port)) {
        connectionInfo.host = host;
        connectionInfo.port = parseInt(port, 10);
        if (client) {
            client.stop().then(() => {
                startLanguageClient(extensionContext);
            });
        } else {
            startLanguageClient(extensionContext);
        }
    }
    vscode.window.showInformationMessage(`Connection updated to Host: ${host}, Port: ${port}`);
}

function isValidHost(host: string): boolean {
    return !!host;
}

function isValidPort(port: string): boolean {
    const portNumber = parseInt(port, 10);
    return !isNaN(portNumber) && portNumber > 0 && portNumber < 65536;
}

function generateWebviewHTML() {
    return `
    <html>
        <head>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: flex-start;
                    height: 100vh;
                    padding-top: 20px;
                }
                h1 {
                    font-size: 24px;
                    margin-bottom: 20px;
                }
                .input-group {
                    display: flex;
                    align-items: center;
                    margin-bottom: 10px;
                }
                .input-group input {
                    margin-right: 10px;
                }
            </style>
            <script>
                const vscode = acquireVsCodeApi();

                window.onload = () => {
                    document.getElementById('enableButton').addEventListener('click', () => {
                        const host = document.getElementById('hostInput').value;
                        const port = document.getElementById('portInput').value;
                        vscode.postMessage({ command: 'enableLSP', host, port });
                    });
                };
            </script>
        </head>
        <body>
            <h1>Adama Configuration</h1>
            <div class="input-group">
                <input type="text" id="hostInput" placeholder="localhost">
                <input type="text" id="portInput" placeholder="2423">
                <button id="enableButton">Enable</button>
            </div>
        </body>
    </html>`;
}

export function deactivate(): Thenable<void> | undefined {
	if (!client) {
		return undefined;
	}
	return client.stop();
}


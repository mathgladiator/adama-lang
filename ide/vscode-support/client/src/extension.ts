
import * as net from 'net';
import * as vscode from 'vscode';
import { workspace, ExtensionContext } from 'vscode';
import { LanguageClient, LanguageClientOptions } from 'vscode-languageclient/node';

let client: LanguageClient;
const connectionInfo = {
    port: 2423,
    host: "127.0.0.1"
};
let extensionContext: ExtensionContext;
let output: vscode.OutputChannel | undefined;
var status: vscode.StatusBarItem | undefined;

function writeToTerminal(...messages: string[]) {
  if (!output) {
    output = vscode.window.createOutputChannel('Adama Logs');
  }
  if (!status) {
    status = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Left, 1);
    status.backgroundColor = "#000000";
    status.color = "#ffffff";
    status.show();
  }
  output.appendLine(messages.join(" "));
  status.text = messages.join(" ");
}

export function activate(context: ExtensionContext) {
  writeToTerminal('[adama] activating adama extension');
  extensionContext = context;
  startAdamaConfigCommand(context);
  startLanguageClient(context);
  writeToTerminal('[adama] extension activation finished');
}

function startAdamaConfigCommand(context: ExtensionContext) {
    writeToTerminal("[adama] command 'showAdamaConfig' registered which allows you to change the host:port for lsp support");
    const disposable = vscode.commands.registerCommand('showAdamaConfig', () => {
        createAdamaPanel();
    });
    context.subscriptions.push(disposable);
}

function startLanguageClient(context: ExtensionContext) {
    writeToTerminal('[adama] language extension starting... (note: devbox should be running)');
    const serverOptions = () => {
        const socket = net.connect(connectionInfo);
        socket.on('connect', () => {
            writeToTerminal('[adama] found devbox @ ', connectionInfo.host, ":", '' + connectionInfo.port);
        });
        socket.on('error', (err) => {
            writeToTerminal('[error] ' + err);
            writeToTerminal('[note] if you restarted the devbox, then you need to start it again and re-load vscode');
        });
        return Promise.resolve({ writer: socket, reader: socket });
    };
    const clientOptions: LanguageClientOptions = {
		documentSelector: [{ scheme: 'file', language: 'adama' }],
		synchronize: { fileEvents: workspace.createFileSystemWatcher('**/*.*') }
	};
    client = new LanguageClient(
        'LanguageServer', 
        'Adama Language Server', 
        serverOptions, 
        clientOptions
    );
    client.start();
}

function createAdamaPanel() {
    const panel = vscode.window.createWebviewPanel('adamaConfig', 'Adama Config', vscode.ViewColumn.One, { enableScripts: true });

    panel.webview.onDidReceiveMessage(message => {
        if (message.command === 'enableLSP') {
            handleConnectionUpdate(message.host, message.port);
        }
    }, undefined, extensionContext.subscriptions);

    panel.webview.html = generateWebviewHTML(connectionInfo.host, connectionInfo.port);
}

function retry() {
    if (client) {
        client.stop().then(() => {
            startLanguageClient(extensionContext);
        });
    } else {
        startLanguageClient(extensionContext);
    }
}

function handleConnectionUpdate(host: string, port: string) {
    writeToTerminal(`[adama] updating connection: ${host}, Port: ${port}`);
    if (isValidHost(host) && isValidPort(port)) {
        connectionInfo.host = host;
        connectionInfo.port = parseInt(port, 10);
        retry();
    }
}

function isValidHost(host: string): boolean {
    return !!host;
}

function isValidPort(port: string): boolean {
    const portNumber = parseInt(port, 10);
    return !isNaN(portNumber) && portNumber > 0 && portNumber < 65536;
}

function generateWebviewHTML(host: string, port: number) {
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
                <input type="text" id="hostInput" placeholder="${host}">
                <input type="text" id="portInput" placeholder="${port}">
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


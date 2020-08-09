/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as net from 'net';
import { workspace, ExtensionContext, window } from 'vscode';


import {
	LanguageClient,
	LanguageClientOptions,
	StreamInfo
} from 'vscode-languageclient';

let client: LanguageClient;

export function activate(context: ExtensionContext) {
    let connectionInfo = {
        port: 2423
    };
    let serverOptions = () => {
        // Connect to language server via socket
        let socket = net.connect(connectionInfo);
        let result: StreamInfo = {
            writer: socket,
            reader: socket
        };
        return Promise.resolve(result);
    };

	// Options to control the language client
	let clientOptions: LanguageClientOptions = {
		diagnosticCollectionName: "adama",
		documentSelector: [{ scheme: 'file', language: 'adama' }],
		synchronize: {
			// Notify the server about file changes to '.clientrc files contained in the workspace
			fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
		}
	};

	// Create the language client and start the client.
	client = new LanguageClient(
		'adama',
		'adama',
		serverOptions,
		clientOptions
	);

	// Start the client. This will also launch the server
	client.start();
}

export function deactivate(): Thenable<void> | undefined {
	if (!client) {
		return undefined;
	}
	return client.stop();
}

# Adama Language Support

Hello! This vscode extension will allow you to seamless work with the Adama DevBox.
Adama is a next generation document store and platform as a service that provides developers a low latency differentiable data store using WebSockets.
The DevBox (java -jar ~/adama.jar devbox) allows projects to run entirely locally, and this extension brings syntax highlighting and language server support (i.e. red squiggles) to vscode for a great experience.

## Features

* Syntax highlighting
* Language server protocol integration: diagnostics, go to definition, find references
* A configuration screen to configure LSP support (> Show Adama Configuration)

## Contributor Build Instructions

1. Build extension

```
npm install
npm install -g vsce
vsce package
```

`adama-language-support-#.#.#.vsix` should have been created

2. In VS Code, Navigate to Extensions
3. Click the ... menu, select "Install from VISIX", select vsix file
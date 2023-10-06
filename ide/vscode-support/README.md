# vscode support for the Adama Language README

## Features

* Simple syntax highlighting
* Works with the Java LSP Server

## Instructions

1. Build extension
    ```
    npm install
    npm run compile
    npm install -g vsce
    vsce package
    ```

    `adama-language-support-#.#.#.vsix` should have been created

2. In VS Code, Navigate to Extensions
3. Click the ... menu, select "Install from VISIX", select vsix file
const path = require('path');

module.exports = {
    entry: './src/index.ts',
    target: 'web',
    devtool: 'inline-source-map',
    module: {
        rules: [
            {
                test: /\.ts$/,
                use: 'ts-loader',
                exclude: /node_modules/,
                include: [
                    path.resolve(__dirname, "src")
                ],
            },
        ],
    },
    resolve: {
        extensions: ['.tsx', '.ts', '.js'],
    },
    output: {
        filename: 'libadama.js',
        path: path.resolve(__dirname, 'dist'),
        libraryTarget: 'umd',
        globalObject: 'this',
        library: 'Adama'
    },
};
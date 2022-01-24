import replace from '@rollup/plugin-replace';
import {terser} from 'rollup-plugin-terser';
import babel from 'rollup-plugin-babel';

const configurePlugins = ({module, polyfill = false}) => {
    return [
        babel({
            presets: [['@babel/preset-env', {
                targets: {
                    browsers: ['ie 11'],
                },
            }]],
        }),
        terser({
            module,
            mangle: true,
            compress: true,
        }),
        replace({
            values: {
                'window.__LIBADAMA_POLYFILL__': polyfill,
            },
            preventAssignment: true,
        })
    ]
}

const configs = [
    {
        input: 'dist/modules/index.js',
        output: {
            format: 'esm',
            file: './dist/libadama.js',
        },
        plugins: configurePlugins({module: true, polyfill: false}),
    },
    {
        input: 'dist/modules/index.js',
        output: {
            format: 'umd',
            file: `./dist/libadama.umd.js`,
            name: 'webVitals',
        },
        plugins: configurePlugins({module: false, polyfill: false}),
    },
    {
        input: 'dist/modules/index.js',
        output: {
            format: 'iife',
            file: './dist/libadama.iife.js',
            name: 'libadama',
        },
        plugins: configurePlugins({module: false, polyfill: false}),
    },
    {
        input: 'dist/modules/index.js',
        output: {
            format: 'esm',
            file: './dist/libadama.base.js',
        },
        plugins: configurePlugins({module: true, polyfill: true}),
    },
    {
        input: 'dist/modules/index.js',
        output: {
            format: 'umd',
            file: `./dist/libadama.base.umd.js`,
            name: 'libadama',
            extend: true,
        },
        plugins: configurePlugins({module: false, polyfill: true}),
    },
    {
        input: 'dist/modules/index.js',
        output: {
            format: 'iife',
            file: `./dist/libadama.base.iife.js`,
            name: 'libadama',
            extend: true,
        },
        plugins: configurePlugins({module: false, polyfill: true}),
    }
];

export default configs;
#!/usr/bin/env sh

uglifyjs --compress --mangle -- tree.js connection.js rxhtml.js > "libadama.js"
uglifyjs --compress --mangle -- worker.js > "libadama-worker.js"

#/bin/sh
uglifyjs --compress --mangle -- tree.js connection.js rxhtml.js > libadama.js

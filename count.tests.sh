#!/bin/sh
./build.py jar | tee ../report.text
cat ../report.text  | grep "Tests run" | grep -v "in org" | cut -d ":" -f 2 | cut -d "," -f 1 | sed "s/ //" | awk '{ sum += $1 } END { print sum }'

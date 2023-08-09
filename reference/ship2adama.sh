#!/bin/sh
mdbook build
java -jar ~/adama.jar spaces upload --space adama-book --directory book


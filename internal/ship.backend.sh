#!/bin/sh
clear
java -jar ~/adama.jar spaces deploy --space ide --file ide.adama
java -jar ~/adama.jar spaces deploy --space billing --file billing.adama

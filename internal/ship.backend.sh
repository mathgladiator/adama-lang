#!/bin/sh
clear
echo "Shipping IDE"
java -jar ~/adama.jar spaces deploy --space ide --file ide.adama
echo "Shipping Billing"
java -jar ~/adama.jar spaces deploy --space billing --file billing.adama

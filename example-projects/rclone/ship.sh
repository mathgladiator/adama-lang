#!/bin/sh
ADAMAJAR=~/adama.jar
SPACENAME=rclone
clear
npx tailwindcss -i input.css --content frontend.rx.html -o style.css
java -jar $ADAMAJAR space upload --space $SPACENAME --file style.css
java -jar $ADAMAJAR space set-rxhtml --space $SPACENAME --file frontend.rx.html
java -jar $ADAMAJAR space deploy --space $SPACENAME --file backend.adama
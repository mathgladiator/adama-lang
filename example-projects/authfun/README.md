README
--------
# Local Development 
This command will (assuming you have adama.jar in your home directory). You can start the adama devbox where you just need to go to http://localhost:8080 to test the application. Running this in this path will start the devbox.

```sh
java -jar ~/adama.jar devbox
```
# Production Backend 
Now, Deploying backend changes requires first bundling up the backend.adama and backend/*.adama via
```sh
java -jar ~/adama.jar code bundle-plan --main backend.adama -o plan.json --imports backend
```
Now, deploy the plan
```sh
java -jar ~/adama.jar space deploy --space authfun --plan plan.json
```
You may keep or delete the created plan.json file.

# Production Frontend 
Deploying frontend changes requires bundling all the *.rx.html files from the frontend directory via:
```sh
java -jar ~/adama.jar frontend bundle
```
Now, deploy the frontend via:
```sh
java -jar ~/adama.jar spaces set-rxhtml --space authfun --file frontend.rx.html
```
You may keep or delete the created frontend.rx.html file. Changes are available via https://authfun.adama.games (usually after a minute for various caches to expire)


If you have any static resources in assets, then you can upload them to the space via
```sh
java -jar ~/adama.jar space upload --space authfun --directory assets
```


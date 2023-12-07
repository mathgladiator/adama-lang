# Building and Contributing

Building Adama requires
* Java SDK 17+
* Maven 3
* Python 3
* [uglify-js](https://www.npmjs.com/package/uglify-js) ( npm install uglify-js -g )

## build.py

Adama uses extensive code generation (however, there may be some platform unstable bugs), and code generation is required for:
* adding/updating APIs to saas
* building the error tables
* introducing new messages between web client (i.e. the load balancer) to the adama service
* regenerating and validating changes to the language and template (there are code generated tests that validate output is stable between check-ins)
* updating gossip codec
* enforcing copyright notice
* regen the platform version

### core workflow

While developing, regenerating is done via

```shell
./build.py --jar --fast --generate
```

where tests are skipped and generation marches onward.
Caveat: this introduces some platform-specific noise which is not desirable, and we need to eradicate those differences.

Prior to checking in:

```shell
./build.py --clean --jar
```

The binary will be **release/adama.jar** which you can copy to your home or project path.
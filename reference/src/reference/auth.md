# Authentication

Many parameters within the [API](./api.md) have an 'identity' field.
For securing your application or game, this field is a [JSON Web Token](https://jwt.io/) that is signed by either Adama or you.
There is a weaker form of security using a special string prefixed by 'anonymous'; this allows developers to open up documents to the internet.
Below is a table of the various forms of authentication supported by Adama.

| method    | description                                                                                                                                                                                                                                          |
|-----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| anonymous | an identity token of 'anonymous:$name' results in a principal of ($name, 'anonymous`).                                                                                                                                                               |
| adama     | The platform has a global authentication mechanism for all adama developers. The principal is ($adamaUserId, 'adama'). The identity token is always secured by a ephemeral private key.                                                              |
| authority | In the spirt of allowing developers top bring their own authentication, an authority is a named and uploaded keystore with public keys. This allows you to secure your private key however you want. [Click here for more information](#authorities) |
| document  | A document is able to grant a web visitor a special principal that is tied to the document; this token is generated via a web response. [Click here for more information](#document)                                                                 |

## Authorities
An authority is a named keystore where public keys are stored in Adama.

### Create an authority
You can create a keystore via the CLI tool.
```shell
java -jar ~/adama.jar authority create
```
This will return the name for your new keystore.

### OLD
The [tutorial lays out the basics](/tutorial/04-authorities.md), but we can do a bit more with the tooling.

TODO: illustrate more about the tooling
```shell
java -jar adama.jar authority append-local \
  --authority Z2YISR3YMJRYCHN29XZ2 \
  --keystore my.keystore.json
  --private second.private.key.json
```

## Document

TODO
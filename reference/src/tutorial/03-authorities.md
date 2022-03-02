# Bringing existing users into Adama

The Adama Platform use [JWT tokens](https://jwt.io/) to authenticate people via their devices, and all Adama developers can use their credentials to talk to the Adama Platform and their documents.
It would be an exceptionally limited (yet mephistophelian) requirement for all users to be an Adama developer, so the Adama Platform allows developers to manage public keys.
This is done by the developer creating an authority:

```shell
java -jar adama.jar authority create
```

This will return a document like
```json
{
  "authority" : "Z2YISR3YMJN29XZ2"
}
```

The **Z2YISR3YMJN29XZ2** is a unique key for developers to use to identify their users. If you accidentally clear your terminal or lose that id, then you can list your authorities via:

```shell
java -jar adama.jar authority list
```

With the name of the authority in hand, we will use the tool to create a keystore

```shell
java -jar adama.jar authority create-local \
 --authority Z2YISR3YMJN29XZ2 \
 --keystore my.keystore.json \
 --private first.private.key.json
```

This will create two files within your working directory:
* **my.keystore.json** is a collection of public keys used by Adama to validate a private
* **first.private.key.json** is a private key used by your software to sign your users' id. <font color="red">***This requires safe-keeping!***</font>

This keystore and private key were created entirely locally on your machine (for exceptional security), and now you upload ***only*** the keystore with:

```shell
java -jar adama.jar authority set \
  --authority Z2YISR3YMJN29XZ2 \
  --keystore my.keystore.json
```

This will allow the users signed by that private key into Adama.
Consuming the private key will require some crypto libraries in some infrastructure that you manage, but we can get started by using the Adama tooling to create an identity today!

```shell
java -jar adama.jar authority sign \
  --key first.private.key.json \
  --agent user001 
```

which will dump out a JWT token with the agent 'user001' as the subject:

```shell
eyJhbFUzNiJ9.eyJdWIiTjI5WFoyIn0.TQZbOkE9abE24_8w
```

This is the string that you use as the identity parameter with the Client API. For now, let's create a second token.

```shell
java -jar adama.jar authority sign \
  --key first.private.key.json
  --agent user002 \\
```

We will use the corresponding tokens for user001 and user002 to [chat with each other by configuring the space.](04-space.md)








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
This will return the name for your new keystore. For the remainder of this document, we will use ```3LZXGH9PUOEH25GZYQ17IL7W713XLJ``` as the name.

### Create the keystore
The tooling can create a keystore that contains an initial public key. The below command will create the keystore for the prior created authority.
```
java -jar adama.jar authority create-local \
--authority 3LZXGH9PUOEH25GZYQ17IL7W713XLJ \
--keystore my.keystore.json \
--private first.private.key.json
```

This will create two files within your working directory:
* **my.keystore.json** is a collection of public keys used by Adama to validate a private
* **first.private.key.json** is a private key used by your software to sign your users' id. <font color="red">***This requires safe-keeping!***</font>

This keystore and private key were created entirely locally on your machine (for exceptional security), and now you upload ***only*** the keystore with:

### Upload the keystore
With a keystore full of public keys, we can upload the keystore to Adama.
```shell
java -jar adama.jar authority set \
  --authority 3LZXGH9PUOEH25GZYQ17IL7W713XLJ \
  --keystore my.keystore.json
```
This will allow the users signed by that private key into Adama.

### Sign an example identity
Consuming the private key will require some crypto libraries in some infrastructure that you manage, but we can get started by using the Adama tooling to create an identity today!

```shell
java -jar adama.jar authority sign \
  --key first.private.key.json \
  --agent user001 
```

This will create a principal with agent ```user001``` and authority of ```3LZXGH9PUOEH25GZYQ17IL7W713XLJ```

### Adding another public key

Similar to create-local which initializes a keystore, we can generate and append a new public key and side-channel write the private key.
```shell
java -jar adama.jar authority append-local \
  --authority 3LZXGH9PUOEH25GZYQ17IL7W713XLJ \
  --keystore my.keystore.json
  --private second.private.key.json
```

## Document

Documents can create a signed identity via a document PUT by returning a object with a sign field like so:

```adama
message WebRegister {
  string email;
  string password;
}

@web put /register (WebRegister register) {
  // .. validate the registration and insert into a table
  return { sign: register.email; }
}
```

We leverage web put because we most likely don't have a connection to the document as we are trying to get credentials to connect to the document.
The web put allows us to register users, and passwords must be hashed prior to being sent to the server.
Since a web put will write a message to the change log, we shouldn't use it for authenticating a user.
Instead, there is an @authorize handler that accepts an username or email along with a plaintext password.

```adama
@authorize (email, password) {
  // the password is plaintext, and you should only store a hash
  if (password_hash.passwordCheck(email)) {
    return email; // this is going to the agent
  }
  abort;
}
```

Whatever these functions result is considered the agent (or subject) of the principal while the authority is the document (doc/$space/key).
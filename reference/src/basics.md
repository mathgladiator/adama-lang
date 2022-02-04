# The Basics of Adama

Hello there!
Welcome to the introduction of the Adama Platform book.
In this chapter, we are going to explore the core idioms and language of what Adama is and how it helps you.

So, what is the Adama Platform?
Well, we could kick this off with some buzzword bingo by saying that Adama is an open-source reactive server-less privacy-first document-oriented compute-centric key-value store, but that doesn't communicate much.
However, we have to start somewhere, so let's tear down those buzzwords with more words.

Let's start with [**document orientated key-value store**](https://en.wikipedia.org/wiki/Document-oriented_database).
Documents are identified via a key (hence the **key-value**), and documents are organized by a space (which is similar to the [bucket concept used by S3](https://en.wikipedia.org/wiki/Amazon_S3) except with a more mathematical feel).
Adama has variants of the four big [CRUD operations](https://en.wikipedia.org/wiki/Create,_read,_update_and_delete), but there are notable differences which make Adama unique.

## Creating
The first notable aspect is that Adama documents are created on the server side via a constructor.
This constructor is defined with code that is bound to the space holding the document.
*Wait, what?*
This where the **compute-centric** comes into play as each space has code as config, and that config is code.
The space is code, and ***everything*** is held within that code as there is no config.

All documents within a space share the same Adama code, and this is why a space is named a space. While buckets can have fixed config, spaces have infinite potential.
For example, the below code illustrates a valid Adama code which we will tear down.

```adama
// static code runs without a document
@static {

  // 1. a policy which is run to validate the given user can create the document
  create(who) {
    return who.isAdamaDeveloper();
  }
}

// 2. the document schema has a creator and an integer named x
private client creator;
public int x;

// 3. the constructor is a message named by the document
message ConsXYZ {
  int x;
}

// 4. connect the constructor message to code
@construct (client who, ConsXYZ c) {
  creator = who;
  x = c.x;
}
```

Admittedly, this is a lot of ceremony to get a document that looks like:

```json
{"x":123}
```

However, this document contains (1) an access control mechanism for who can create the document, (2) a document schema with **privacy as a first class citizen**, (3) a message interface for validating input, (4) logic to construct the state of the document.

As a rule, documents can only be created once and race conditions go to first creator. It's worth noting that the document's state is potentially different than the state used to construct it which enables developers to think in their domain rather than the document's schema.

## Reading

Once a document is created, documents can be read by connecting to the document. This requires further Adama code because there is a need to access control the document, and we append this code to the above document

```adama
// 5. gate who can connect to the document
@connected(who) {
   return who == creator;
}
```

This will allow people to connect or not. The reason we say *connect* instead of *read* or *get* is because we establish a long-lived stream between the client and the document which allow changes to the document to flow to the client very quickly with minimal latency, low overhead, less network; this explains the **reactive** buzzword.

## Updating

Updates to the document happen by sending messages to the document via channels; channels are basically functions exposed to clients. For example, we can open a few channels to manipulate our document in various ways.

```adama
message Nothing {}
message Param { int z; }

channel square(client who, Nothing n) {
  x = x * x;
}

channel zero(client who, Nothing n) {
  x = 0;
} 

channel add(client who, Param p) {
  x += p.z;
}
```

Messages will flow and hit a document exactly once, run the associated logic, change the document state, and all connected people will **reactively** receive an updated version of the document.
Access control is possible per channel, but it is worth noting that only connected clients can send to a document (by default).

## Deleting
Deleting happens from within the document via logic.

```adama
channel kill(client who, Nothing n) {
  if (who == creator) {
    Document.destroy();
  }
}
```

The core motivation for this is that access control for deletion requires business logic.

## Buzzword Bingo Summary
| Buzzword | Translation |
| --- | --- |
| open-source | Yes, all the source for the platform is [hosted on Github over](https://github.com/mathgladiator/adama-lang) |
| reactive | The connection from client to server uses a stream such that updates flow to client as they happen |
| server-less | The servers are managed by the platform, and developers only have to think about keys and documents |
| privacy-first | The language has many privacy mechanisms that happen during document schema definition enforced during run-time |
| document-oriented | Adama maps keys to values, and those values are documents |
| key-value store | Adama use a NoSQL design mapping keys to values |


## So, how does this help? What is Adama's value proposition?
This is the big question. At core, Adama takes the trinity of the cloud: compute, storage, and networking and bundles them into one offering.


# The Basics of Adama

Hello there!
Welcome to the introduction of the Adama Platform book.
In this chapter, we are going to explore the core idioms and language of what Adama is and how it helps you.

So, what is the Adama Platform?
Well, we could kick this off with some buzzword bingo by saying that the Adama platform is an open-source reactive server-less privacy-first document-oriented compute-centric key-value store acting like a platform as a service, but that doesn't communicate much (or, does it?).
However, we have to start somewhere, so let's tear down those buzzwords with more words.

Let's start with [**document orientated key-value store**](https://en.wikipedia.org/wiki/Document-oriented_database).
Adama stores documents, and documents are identified via a key (hence the **key-value**). Documents are organized by a space (which is similar to the [bucket concept used by S3](https://en.wikipedia.org/wiki/Amazon_S3) except with a more mathematical feel).
Adama has variants of the four big [CRUD operations](https://en.wikipedia.org/wiki/Create,_read,_update_and_delete), but there are notable differences which make the Adama platform unique. Deconstructing the CRUD operations is the best way to teardown the buzzword bingo.

## Creating
The first notable aspect is that Adama documents are created on the server via a constructor.
This constructor is defined with code that is bound to the space holding the document.
*Wait, what?*
This is where the **compute-centric** comes into play as each space has code as config. 
All documents within a space share the same Adama (the language) code, and the Adama code defines the document schema, logic for transformation, access control, and more.
This is why the name space was chosen over bucket because buckets can only have fixed config while spaces have infinite potential.

For a clear example, the below code illustrates valid Adama code which we will tear down.

```adama
// static code runs without a document instance
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
  x = 100 + c.x;
}
```

Admittedly, this is a lot of ceremony to get a document that looks like:

```json
{"x":123}
```

when constructed with a message like
```json
{"x":23}
```

However, this document contains (1) an access control mechanism for who can create a document, (2) a document schema with **privacy as a first class citizen**, (3) a message interface for validating input structure, and (4) logic to construct the state of the document.

As a rule, documents can only be created once and race conditions go to first creator. It's worth noting that the document's state is different from the state used to construct it which enables developers to think in their domain rather than the document's schema. This is very similar to [Alan Kay's original thinking around object-oriented programming](https://wiki.c2.com/?AlanKaysDefinitionOfObjectOriented) and [Carl Hewitt's actor model](https://en.wikipedia.org/wiki/Actor_model).

## Reading

Once a document is created, documents can be read by connecting to the document. This requires further Adama code because there is a need to gate access to the document, and we append this code to the above code.

```adama
// 5. gate who can connect to the document
@connected(who) {
   return who == creator;
}
```

This will allow people to connect or not.
The reason we say *connect* instead of *read* or *get* is because we establish a long-lived stream between the client and the document which allows changes to flow from the document to all clients with minimal cost; this explains the **reactive** buzzword.

## Updating

Updates to the document happen by sending messages to the document via channels; channels are basically procedures exposed to clients.
For example, we can open a few channels to manipulate our document in various ways.

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

Messages will hit a document exactly once, run the associated logic, change the document state, and all connected clients will **reactively** receive a change to update their version of the document.
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

The core motivation for this is that access control for deletion requires business logic. Since Adama documents can run code [based on time passing](./guide/state-machine.md), this also enables documents to self-destruct.

## Buzzword Bingo Summary
With the CRUD operations laid bare, we can analyze the buzzword bingo aspects in a table:

| Buzzword | Translation |
| --- | --- |
| open-source | Yes, _<u>all</u>_ the source for the platform is [hosted on Github](https://github.com/mathgladiator/adama-lang) |
| reactive | The connection from client to server uses a stream such that updates flow to client as they happen |
| server-less | The servers are managed by the platform, and developers only have to think about keys and documents |
| privacy-first | The language has many privacy mechanisms that happen during document schema definition enforced during run-time. It's entirely possible for the document to hold state that is never readable by any human without hacking. |
| document-oriented | Adama maps keys to values, and those values are documents |
| key-value store | Adama use a NoSQL design mapping keys to values |
| platform as a service | Adama provides the trinity of compute, storage, and networking which is enough to build many products. Adama is designed to pair exceptionally well with a web browser. |

## So, how does this help? What is Adama's value proposition?
This is the big question. At core, Adama takes the trinity of the cloud: compute, storage, and networking and bundles them into one offering. The value proposition is thusly multi-faceted depending on various markets:
* Jamstack developers are able to hook their application up directly to the Adama platform such that privacy, security, query, and transformation are provided out of the box.
* Game developers can leverage Adama platform to act as a serverless platform for both a game lobby and a game server (The [history of Adama](/reference/history.html) starts with board games).
* Any website can integrate Adama as a durable and reliable real-time service for chat, presence, web-rtc signalling, and more without managing servers.

Beyond making development easier, business operations is further aided by having a tunable history of changes to the document available which makes auditing changes easy as well as having a universal rewind.
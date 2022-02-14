# Single file infrastructure

The state of the world
----------------------

Building a web product is a complete pain in the ass. First, you must build the UI and then have the UI be hosted on a server which is usually stateless. The server then must implement an API to expose data to the UI, so the UI can be useful to enable people to do stuff. The API then must coordinate state with a database or other data stores, and if you want any modern features you will need a queue or use a messaging stack. The messaging stack must inter-op with the API server, and that stack also will reach out to a variety of notification services. All this then requires some form of infrastructure orchestration because you will inevitably give up on building a monolith and get a microservice architecture, and then reliability becomes a hard task because you will rely on the network to behave. The network will mostly behave, but everyone will periodically be forced to (re)learn how to deal with queueing theory.

Shit will happen, and services will fail due to growth. Holy crap.

There are so many things behind most websites, that it is a crazy mess. It's MADNESS, and it sure isn't fine. Is it any wonder that [I'm full of hate?](http://www.adama-platform.com/2020/04/09/and-so-it-begins.html)

A bold new world
----------------

Adama enables a different way of thinking such that a single file can define an entire product infrastructure. The UI still exists as a separate build, but the UI only really needs to talk to a single piece of infrastructure. This greatly simplifies the process of building software, but [there are no silver bullets](https://en.wikipedia.org/wiki/No_Silver_Bullet). This power is available because scale has been scoped and traded for ergonomics, so let's talk about limits.

A single **living document** can only live authoritatively on a single host. This limits two things: the update rate (CPU) and size (memory). The update rate must be balanced with the replication cost, and Adama is designed such that small updates manifest in small replication changes and minimal ingestion. This implies durability can be affordable, and it also implies that the ability to consume a **living document** can scale infinitely. The key limits at play are the update-rate and size. 

The big missing piece as of this writing is the inability for documents to connect together, so there is no glue or index between documents. This, however, is a solvable problem.

Mental Model: The Tiny-House for User Data
------------------------------------------

As a builder, both relational databases and NoSQL stores will shard user data across various machines. As a somewhat privacy concerned user, this terrifies me as all my data is potentially on hundreds of machines. With Adama, all the logic and data can exist within a singular conceptually atomic unit of a document. It's like a tiny house of all your data, and it moves with you. This container model of all your data makes it simpler to comply with emerging regulations since both developers and users can trust where data actually is.
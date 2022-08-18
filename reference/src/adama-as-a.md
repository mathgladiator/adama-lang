# Adama as a ?
*by [Jeffrey M. Barber](http://jeffrey.io)*

When [I](http://jeffrey.io) worked at Amazon S3, there was a meme on many office walls of the form "S3 as a _____" with a long list of answers.
S3 is heavily abused because it is simple, and this is a fantastic thing.
Well, it's also frustrating as that abuse could often lead to late night pages.
I am aspiring to keep Adama simple, but I've had a tough time answering "What is Adama".
Instead, I'll provide a tsunami of answers in the form of "Adama as a ____".

## Adama as board game infrastructure
<img style="float:right" src="/i/aaa-game-infra.png" width="250" />

Board games is where Adama was spawned from which is why the most myopic use-case for Adama is first.
However, it's worth thinking about as it sets context for all the other scenarios.
The motivating question was how to get control of all the state and logic of building a fantastic board game: [Battlestar Galactica (BSG)](https://boardgamegeek.com/boardgame/37111/battlestar-galactica-board-game).

This started by taking control of all the game state with a domain specific language.
By code-generating the state structures, the state could be easily transactionable such that snapshots and undo become possible features.
With that as a foundation, a collaborative framework could be built such that document state could easily replicate to players.
However, many games are competitive which requires privacy. The moment privacy became a concern for the domain specific language was a feature cascade as the language became mostly turing complete.

As the language to transform the document emerged, so to moved all the board game logic.
Sitting in a private place is a complete back-end for BSG which I can't release (sad face).

## Adama as a head-less Excel
The interesting thing is that privacy of state also begs the question of how to have private computations for individual viewers, and this injected reactivity into the language which was yet another productivity boon which made board games exceptionally easier to build.

The moment you have reactivity, you have the foundation that makes Excel powerful.
Suddenly, computing is more accessible and Adama greatly simplifies the burden of building multiplayer experiences.

All you have to do is accept messages, change the document, and computational changes flow to users in a differentiable form.

## Adama as serverless multiplayer game hosting
There is a spectrum of multiplayer experiences from board games to MMOs or FPS, and Adama can provide infrastructure for the metaverse where all games share common infrastructure.
Game developers can focus on their game logic within Adama and build exceptional experiences using any game engine.

## Adama as a low-code collaboration storage and networking engine
A trivial consequence of bringing people together around board games is that normal applications benefit from the investments.
Whether an application requires conflict-free replicated data types or operational transforms, Adama provides a medium for applications to share state via a document abstraction.
All the burdens of networking, synchronization, and reliable communication are simplified as Adama steps in as a message broker and document store.

Beyond sharing state, computations allow that state to become much more than dead bytes.

## Adama as a real-time data store
Adama can be used to build publisher/subscriber systems, presence, live-anything, games.
Adama leverages a socket-first approach because reality is real-time, and goal of having games on Adama means gamer demands will drive the platform towards excellence.

## Adama as a SMB application provider
With Adama providing both state and compute along with a fundamentally simpler networking idiom, entire applications can be built with just Adama as the back-end.
The beauty of these apps is that they don't require complex setups nor configuration.
Simply point a UI at a document, and boom you have an app which you can host on the Adama Platform or on-site with your own machines.

It's worth noting that a document within Adama can be massive (as large as the Java heap) because state is persisted via change deltas, and this can scale up to many businesses for their entire lifetime.

## Adama as a multi-tenant SMB platform
Since a single document can be used to power an entire application, Adama allows businesses to spawn off clients within their own documents.
As most tenants fit within a single machine, they get fantastic properties around data and privacy regulations since the model is easily isolated from other businesses across different geopolitical boundaries.

## Adama as a garbage collecting storage proxy
Blobs of data can be attached to an Adama document, and these blobs are called assets (which can be thought of as attachments).
These blobs are stored within Amazon S3.
As documents change over time with assets coming and going, Adama presents a unique opportunity to precisely control storage by garbage collecting assets against what is stored in the Adama document's history.

## Adama as a web hosting provider
Since Adama has a web-server built-into it (for websockets and assets), it was low-hanging fruit to connect HTTP to an Adama document.
This is a work in progress, but the vertical integration of the Adama platform will include many of key features required to host both static sites and then provide dynamic services.

## Adama as a web hook listener
Since Adama supports HTTP verbs routed to documents, various services can talk to Adama like twilio, discord, or slack.
Any webhook provider can feed data into an Adama document.

## Adama as a massively scalable "real-time" data-base
<img style="float:right" src="/i/aaa-infinite-scale.png" width="250" />

As a future possibility, Adama can support a large number of writers (~1K) with infinite readers having personalized views.
This is possible due to the fact that Adama documents are tiny databases which emit change logs, and change logs can be shipped across regions naturally.
The moment you have a replication topology built from change logs, you can achieve infinite read scale.

## Adama as a cron-service
Each Adama document has a state machine loop which can be leveraged to sleep for periods of time, do stuff, and then go back to sleep with a timer to wake up again.

## Adama as a workflow coordinator
Adama can reach out to people and await a result.
This essential feature for board games allows coordination between customers and staff for something like order fulfillment.
A small restaurant could be backed by a single Adama document such that customers place orders, staff are then alerted about the order, and document can block until the staff acknowledge the order.

## Adama as a personalized queue
The state machine loop is fundamentally a queue which can be leveraged to do stuff over time such that a mainline path is unblocked.
Instead of a massive queue for an entire enterprise, the queue is per document such that precision and insight is available.

## Adama as a durable application gateway
Adama can call other services in a reliable manner and durable manner.
Beyond allowing Adama to broker a request to another back-end, it can make multiple requests to multiple back-ends without worry of partial failure.
This means that Adama prevents torn-writes as Adama has a state machine and a queue to broker the idempotent requests.

## Adama as an edge compute serverless function
Adama provides arbitrary almost-turing-complete capabilities, and as such can be used to host both stateful and stateless functions which do things.
With massive scale potential, documents can be replicated close to users such that the associated behaviors can be executed very quickly.

## Adama as a privacy-aware CDN
Adama providing HTTP, assets as arbitrary blobs of data, and massive scalable potential enables Adama to act as a privacy aware CDN such that resources are not blindly cached at the edge for any passerby lucky enough to get a URL.
Instead, the CDN can execute privacy logic close to users such that assets are vended securely.

## Adama as a "bit much"
At this point, Adama is looking up from a capabilities perspective. Clearly, there has been a tremendous amount of scope creep, but many of these aspects are consequential of having a turing complete language and sane data model.
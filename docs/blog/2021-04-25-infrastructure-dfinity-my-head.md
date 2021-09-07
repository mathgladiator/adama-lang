---
slug: wrapping-head-dfinity-internet-computer
title: Wrapping my head around DFINITY's Internet Computer
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, infrastructure, dfinity]
---

I hope to ship a single simple game this year, so I will need some infrastructure to run the game. Alas, I'm at a crossroads.

One enjoyable path is that I could build the remaining infrastructure myself. While this would require starting a bunch of fun projects and then trying to collapse them into a single offering, this potentially creates an operational burden that I'm not willing to live with. Wisdom would require me to put my excitement aside of combining gossip failure detection with raft and handling replication myself.

Another path is to simply not worry about scale nor care about durability. The simple game to launch only lasts for five minutes, so my core failure scenario of concern is deployment. The only thing that I should do here is to use a database like MySQL on the host instead of my ad-hoc file log. Ideally, the stateful-yet-stateless process should be able to quickly recover the state on deployment. Scale is easy enough to achieve if you are willing to have a static mapping of games to hosts, and this currently is a reasonable assumption for a single game instance. This is not ideal for the future serverless cloud offering empire, but the key is to get a tactical product off the ground and evolve. As durability demands and scale demands go up, the key is to balance the operational cost with engineering cost.

So, it's clear that I should start the model as simple as possible. Starting simple is never perfect and takes too long, but it's how to navigate uncertain waters. This is where [DFINITY's Internet Computer (IC)](https://dfinity.org/) enters the picture as it is almost the perfect infrastructure. Today, I want to give a brief [overview of what the Internet Computer (IC) is](https://dfinity.org/evolution-of-the-internet), and I want to compare and contrast it with Adama. For context, I'm basing my opinions on their website as I'm a buyer looking at brochures; some of this might be inaccurate.

### Canister / Document
At the core, the Internet Computer is a series of stateful compute containers called canisters. A canister is a stateful application running WebAssembly, so it is a foundational building block. The state within the canister is made durable via replication of a log of messages (i.e., the blockchain), and the state is the deterministic result of ingesting those messages. Canisters can be assembled together into a graph to build anything at a massive scale. However, the techniques to do so are not commonly understood since the canister relies on actor-centric programming.

This is very similar to an Adama document where each document is a stateful document fed via a message queue from users, but Adama doesn't address the graph element. What does it mean for two documents to talk to each other? There are three reasons that Adama doesn't do this (yet):

* Allowing any kind of asynchronous behavior requires error handling as bumps in the night will increase dramatically. The moment you send a message, that message may succeed or fail outright, timeout, and may have side effects; a design goal of Adama is to eliminate as much failure handling as possible (no disk, no network).
* I don't believe direct message exchange is [compatible with reactivity](https://principles.reactive.foundation/). Instead of two documents sending messages as commands to each other, I'd rather have one document export a reactive view to another. This collapses the error handling to the data has or hasn't arrived. Adama's design for document linking is on the back burner since I currently lack a usecase within board games.
* While humans are somewhat limited in their ability to generate messages, machines are not. It is entirely appropriate to have a queue from a device driven by a human and then leverage flow control to limit the human. Many operational issues can be traced to an unbound queue somewhere, and handling failures is non-trivial to reason about. This further informs the need to have a reactive shared view between documents since reactivity enables flow control.

From my perspective, the IC's canister concept is a foundational building block. Time and experience will build a lingo around the types of canisters. For instance, Adama fits within a "data-only canister" which only accepts messages and yields data. I'll talk more about possible types of canisters later on in the document.

### Access Control / Privacy
The beauty of the IC canister is that it can be a self-enclosed security container as well via the use of principals and strong crypto. There is no need for an external access control list. Each canister has the responsibility of authorizing what principals can see and do, and this is made possible since each canister is a combination of state and logic. The moment you separate state and logic, a metagame is required to protect that state with common access control logic or access rules.

This is what Adama does via the combination of [@connected](/docs/reference-connection-events) and [privacy controls (without strong crypto)](/docs/reference-privacy-and-bubbles). The Adama constructor allows the document to initialize state around a single principal (the creator) and anything within the constructor argument. The @connected event will enable principals to be authorized to interact with the document or be rejected. Each message sent requires the handler to validate the sender can send that message or not.

Since the IC canister is service orientated, there is nothing that [different for updating state within the container](/docs/what-actors-are-actings). The big difference happens at the read side where Adama has all the state as readable to clients up to privacy rules, and privacy rules are attached to data. This means that Adama clients get only the data the document opens up to them, and they don't need to ask for it explicitly.

Since Adama retroactively forwards updates to connected clients, this is the most significant technical hurdle to cross with using IC. However, this is solvable in a variety of ways. It would be great to connect a socket up to a canister, and I don't see this as an impossible feat.

### Cost
The canister also has an elegant cost model around “cycles” that Adama has somewhat except Adama can bill for partial compute. The Adama language solved the halting problem preventing infinite cost! A finite budget is a key aspect for a language to consider to guarantee to halt. When you run out of budget, the program stops, and you are broke. The critical technical decision to ensure randomly stopping a program isn't a horrific idea is that state must be transactional, and you can go back in time. Given the canister's all-or-nothing billing model, it seems that it also has transactional state.

The IC canister [talks abstractly about cycles](https://sdk.dfinity.org/docs/developers-guide/concepts/tokens-cycles.html), but I'm not sure how cycles relate to storage and memory costs. Perhaps, cycles are associated with paging memory in and off disk? This is where I'm not clear about how IC canister communicates itself to cost-conscious people. Furthermore, it's not clear how cost-competitive it is with centralized infrastructure.

### Usage and Scale

The IC canister is far more generic than Adama, but this is where we have to think about the roles of canisters as they evolve with people building products. With both Adama and IC's canister deeply inspired by actors, this is a shared problem about relating the ideas to people. There will be off-the-shelf canisters that can be configured since scale is part of every business's journey.

From a read side, the good news is that both Adama and canisters that only store data and respond to queries can scale to infinity and beyond. Since replication is a part of both stories, one primary replica cluster can handle the write and read replicas that can tail a stream of updates. The emergence of the "data primary actor" and "data replica" roles are born.

From a write side, data at scale must be sharded across a variety of "data primary actors" in a variety of ways, so even more roles are born.

First, you need "stateless router actor" which will route requests to the appropriate "data primary actor" either with stickiness or randomness (a random router could also be called a "stateless load balancer actor").

Second, with writes splayed across different machines, you will at some point require an "aggregator actor" which will aggregate/walk the shards building something like a report, index, or what-not.

Third, replication gives you the ability to reactively inform customers of changes, an "ephemeral data replica actor" or "fan out actor" would be a decent way for clients to listen to data changes to know when to poll or provide a delta. This is where Adama would put the privacy logic and reactivity layer. Given the infinite read scale of replicas, this also offers an infinite reactive scale.

Fourth, perhaps sharding doesn't take the heat off a "primary data actor", then a "fan in actor reducer" roll would be able to buffer writes and aggregate them into the main "data primary actor." The ability to fan in would enable view counters to accurately bump up with many viewers.

Fifth, beyond data, there is the problem of static assets and user-generated content. A "web server actor" makes sense for front-ends which they already have in the platform. I imagine IPFS would be the place for user-generated content, so long as the IC canister and IPFS got along.

There are more actors for sure, but the key thing to note is that building a new internet requires rethinking how to transform the old world ideas into the new world. Some ideas will die, but some may be foundational.

### Operations

As I haven't started to play with the DFINITY sdk yet, so I'm not sure about a few things. Adama was built around easy deployment, monitoring, and everything I know about operating services. This is where the IC container feels murky to me. For instance, how do I deploy and validate a new actor? How does the state up rev? Can the state down rev during a rollback? This is why Adama has all of its state represented by a giant JSON document because it is easy to understand how data can change both forward and backward.

Deploying with Adama should be a cakewalk, and I want Adama to replicate state changes from a running Adama document into a new shadow document. I can validate the lack of crashing, behavior changes, data changes, and manually auditing things without impacting users.

I'm interested to see how DFINITY addresses the engineering process side of their offering.

### Concluding Thoughts & Interesting Next Steps

Overall, I'm excited about the technology. I'm wary of the cost and the lack of communal know-how, but these are addressable over time. I also feel like there is a synergy between how Adama and the canister think about state. For instance, I choose JSON purely as a way to move faster. However, I hope to design a binary format [with similar properties](https://tools.ietf.org/html/rfc7396). Perhaps DFINITY will release a compatible memory model that is vastly more efficient?

Ultimately, the future in this space will require first adopters willing to slog through the issues and build the vocabulary about how to create products with this offering.

A clear next step for me in 2022/2023 is to figure out a WebAssembly strategy that would enable me to offload my infrastructure to this new cloud. It makes sense that I keep my infrastructure investments low and focus on killer products to make tactical progress towards a product that could fund deeper engineering. This translates towards a more casual approach towards durability and scale. For durability, I'll just use block storage and hope that my blocks do not go poof. As protection against catastrophe, I'll integrate object storage into the mix and move cold state off block storage into Amazon S3 or compatible storage tier. For availability, I'll avoid treating my servers like cattle and use a proper hand-off. For now, I just have to accept that machine failures will result in an availability loss.

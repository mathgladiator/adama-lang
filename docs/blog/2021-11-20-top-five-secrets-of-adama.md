---
slug: five-secrets
title: Five Secrets of the Adama Programming Language
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, infrastructure, secrets, insights]
---

Now, that I am committed to my path with this language; let's talk about secrets. These are the things that I know which I believe could lead to a niche empire. I'll share them directly along with insights about why I'm continuing with a language-centric approach. As a moment of clarity, this isn't my best writing since I'm fumbling around with ideas. Perhaps, secrets today are just messy facts of tomorrow?.

## DIY database within a document

We start with the first experience of writing code. It's an amazing experience where you can take inputs in and do things producing a happy output. Making fractals and other graphical things with [mode 13h](https://en.wikipedia.org/wiki/Mode_13h) was pure joy. I remember making calculator programs for my [TI-80](https://en.wikipedia.org/wiki/TI-80) which helped me in my mathematical journey. I remember building a simple game for a BBS using QBasic, and then I started the journey beyond compute: persistence.

Persisting data from your program to a something like a disk, a database, or a network service is problematic for many reasons. We start with the sheer tedium of taking state from memory and then marshalling it out into a block of data. Assuming you got everything, the other side may fail for a variety of reasons. Worse yet, you may have a partial failure and be in a funky state. Having spent a decade within infrastructure: there are many things that can go wrong.

This problem is so hard that many developers bias towards some degree of stateless programming letting the emergent cloud (i.e., the availability of many specialized services) handle those pesky hard problems. This has been a fruitful decoupling for moving faster, but this is not without its own pain points. The first secret rests in a return to the basics of just writing code, and let the language persist everything. We throw away the notion that we must load data, do our work, then save data. 

We can even model this using existing ideas. Take for instance your computers ability to suspend, power off, then power on back to where you were. When it works, it is amazing, so let’s draw it out:

![mental model of vm flow](/img/20211120-vm-mental-model.png)

Suppose the VM holds the state of your game (or app/experience). When the first player joins the game, the VM wakes up (1) by loading a snapshot from the disk (2). As more players join and they interact (3.a), changes to the VM’s memory needs to be persisted (3.b). Once all the players leave or the VM needs to be run on a different host (cloud shenanigans), the VM needs to be shutdown and put to sleep (4). This mental model is precisely the model that Adama uses for back-ends written in the language.

With this model, we can start asking questions like: how many VMs can a host hold? How do we upgrade the VM’s logic and data structures? What is the memory layout? What is the throughput to persist changes from the VM? How expensive is it to load state? How quickly can a deployment cycle from sleep to load? These questions provide insights into why databases are designed the way they have been.

The key property that I’ve come to appreciate is differentiability. When you use a database, your updates and deletes are effectively data differentials which the database can simply ingest and proxy out to the disk with a log structure. Databases are beautifully simple once you see through the complex layers on top of the transactional log powering them.

Unfortunately, application developers tend to want different ways of persisting data beyond emitting data differentials. So, we get things like ORM or GraphQL. The [object-relational impedance mismatch](https://en.wikipedia.org/wiki/Object%E2%80%93relational_impedance_mismatch) is a very precocious problem requiring either your app or API to submit to the relational model or your relational model to submit to your app (i.e. the emergence of NoSQL).

Making persistence easier is both a siren song and essential research. Personally, using a database doesn’t spark joy with me which is why I’ve biased towards the chaos of NoSQL. However, that chaos is not without its pain and suffering. Perhaps the ultimate truth is that persistence, like life, is pain. I have hope that there is a better way, and here I am to push this rock up the hill once again. However, doing better requires a pilgrimage of understanding why existing solutions the way are they are.

Adama takes the view that developers should work within the previously mentioned VM model. However, instead of mirroring the memory as a giant block of bytes, the memory should have a layout that is compatible with logic upgrades and be cheaply differentiable. This layout is precisely why the key container within Adama is a table because tables have a lot of good things.

As your code runs, Adama’s runtime is building up a data differential which can be emitted to a log around a transactional boundary. With Adama, the transactional boundary is a single message. A single message from a person enters and a single data differential is emitted. This has the beautiful consequence that you think in terms of your data which is declared directly within your code. This is then compounded by the sheer lack of failures for you to handle. The message and data differential are tied together such that your back-end code need not think about failures. You just write code, change state, and magic of persistence is handled for you.

The secret then is that you just focus on the experience directly as if you are a beginner again.

## UNDO!

As previously stated, Adama is translating messages into data differentials by monitoring the game/app state. This monitoring enables recording the inverse differential as well, and this imbues products with an automatic Undo feature.

This comes in two forms.

The first form is to simply rewind the state of the document. This alone is worth the complexity of building the language because the biggest complaint my social group has when playing any online board game is the lack of undo. As I get older with my peers, the game is less about cut-throat competition and more about socially connecting in a casual fun way. Mistakes happen, and that’s ok.

This first form is also global to the document which is problematic when using undo in a collaborative setting; the second form is unsend which speaks towards how the inverse differential can be isolated and pulled forward such that it works to preserve the work of multiple people. This second form is much more theoretical and has potential problems, so I’ll probably need to invent a way for some messages to describe an inverse message for undo rather than using an algebraic manipulation.

Fortunately, I can focus on the first form since the second form is much harder to think about. However, the nerd-snipe is to focus on the academic problem of collaborative undo which leads into various CRDTs.

## Await/async and the dungeon master role

Await and async are language mechanisms which greatly simplify much of the burden that asynchronous code places on developers. For anyone that has had to deal with callback hell with lots of janky code running in various threads, await/async are a tall glass of iced tea.

Since this is (or is becoming) a common idiom, the secret rests with breathing life into the Adama. Each Adama game/document instance has a labeled state machine which will execute code either on transition or after some time. For a board game, this state machine behaves like a dungeon master who controls the flow of the game.

This dungeon master is then able to ask people questions like “which card would you like to play?” or “would you like to roll dice or pass?” and this ability to reach out to people is the key that greatly simplifies writing a board game on the server. Alas, this creates problems.

The first problem relates to the transactional boundary. While the dungeon master incorporates messages from different people, the boundary must happen at the end of the state machine transition. This means that a state machine transition becomes a multi-message transaction boundary where all the messages commit together.

The second problem arises from how some languages implement async-await by converting the logic into a state machine. This complicates the implementation of the transaction boundaries as it is possible for messages be handled (i.e. chat) which must commit a transaction while the dungeon master is waiting on asking a player a question. Furthermore, the code running the dungeon master should be able to restart on a different machine for operations, and this is problematic with how current languages implement async-await.

The third problem arises in a few ways. What happens if a player goes away forever, can the players cancel the current state machine to reduce players and try again? The ability to cancel the multi-message transaction feels important.

These problems are fixable by (1) forcing a single message asked by the dungeon master into a queue within a data differential, (2) an await will throw an exception when data is unavailable, (3) a document will drain the queue and emit a data differential only on successful completion of the state machine logic.

This is possible due to the ability to monitor state grants the ability to revert changes, but it also requires non-determinism for functions like rolling dice.

Many of these problems were discovered in trying to build a game using node.js, and the operational problems of restarting node.js were painfully apparent. Being able to restart a program and not notice if it restarted is a hard problem which manifests as a secret of Adama.

## Reactivity and privacy

A key challenge with board games is privacy between players. It’s a goldilocks problem of sharing too much or not sharing enough as it is tedious to copy, transform, and serialize the state for each user. A key secret of Adama is to define state with privacy rules of who can see what.

Laying out your state with privacy in mind with a language automates the copying, transform, and serialization; this is a great productivity boon mirroring an interface description language like protobuf and thrift which greatly improve productivity with serialization.

The automation of this transformation from global to personalized private state then reveals the tedium of pushing out changes on each state change. Furthermore, the failure mode of reconnecting requires clients to be able to pull state as well. We leverage reactivity to combine the pulling and pushing into a signal idiom.

Reactivity becomes a perfect pairing with privacy such that developers can dial into the perfect balance of what to share or not whilst also minimizing costs. The networking cost is minimized by the server sending only updates to the clients. The client cost is minimized by only ingesting the change. The server cost is minimized by recomputing privacy on data changes (which is courteously given from the server’s ability to monitor change for persistence).

Reactivity also enables sharing of cached results internally like how Excel enables reuse of computation. It is entirely possible to use Adama in ways that are compatible with Excel, and an interesting future project would be to convert an Excel spreadsheet into an Adama script + data blob.

## Massive scale + client-side prediction for low latency

As a final secret on something that I believe is a new mode of building low latency applications.

A key criticism of Adama currently is that it requires consistency mirroring how ACID databases work. This means that if players within the US want to play a game, then a server must be elected near the centroid of the users to keep latency balanced. For board games, latency isn’t a huge deal. However, for collaborative applications or real-time games, then this becomes a challenge.

We can exploit the ability to log data changes out first to achieve massive scale such that millions of people can observe a game with a reasonable number of participants writing state. This mirrors how read replicas of databases function, and this then begs a question of what a write against a read replica could become.

A write against a read replica is, in some ways, the beginnings of client-side prediction where if that write doesn’t conflict then absorbing it is not a big deal. The name of the game is how to deal with conflicts. The reason to investigate this because a read replica could be geographically much closer to the user providing exceptional latency for the users which are close to each other.

Furthermore, this begs a question if client-side predictions can be estimated on the client side. If the state of the game is mostly all public, then the answer is yes. Privacy is a confounding issue, but given we have a language it begs a question if a specialized client-side predictor could be generated to estimate how local messages manifest local data changes.

The future game in this to figure out many things. For instance, inserting items into a table requires id generation to federate by writers. Well, we could set a maximum number of writers and then provision an id space per client such that new items can’t conflict on ids. Similarly, we could have each writer with a randomization seed such that randomness is deterministic between multiple writers. These estimates would only work on independent messages rather than messages requested by the dungeon master.

This area is fascination to explore, and I feel at the beginning of the low latency journey here. Unfortunately, I must prioritize away from low latency. However, the possibilities feel endless!

## Summary
Well, I'm exploring an area at the intersection of many things. The platform I imagine is server-less in nature powered by a language enabling developers to build an application specific database. 

I may be making a mistake by focusing too much on the language, and perhaps I should rebrand this as a new kind of data store. Regardless, I need to shift my thinking about how I execute and find a balanced strategy between my long term research interests and short term results to drive interest. Expect a future post on a strategy shift. And, if you got this far in my rambling, then I thank you.
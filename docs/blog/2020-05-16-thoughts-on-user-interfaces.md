---
id: it-started-with-a-new-browser
title: How this all started from building a custom browser
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, ui]
---

Conceptually, a user interface is a simple thing. It is a pretty and delightful picture which makes it easy to understand and interact with a product. That's it.

Since Adama is a programming language for board games, it stands to reason that Adama does not exist in a vacuum and it must be workable with existing UI technologies. That is, it must sanely integrate with a variety of frameworks to achieve some measure of success and be usable beyond my myopic view of reality.

> When it comes to modern day application building, I am an old man screaming from my porch for you damn kids to get off my lawn.

An interesting point of history is that my recent work on board games actually started using [SDL](https://www.libsdl.org/) and then eventually [Skia](https://skia.org/) with C#. I was trying to build a new style of browser where (1) a single socket was the only way to get data and send messages, (2) there is exceptionally limited client side logic to eliminate abuse, (3) [anti-entropy protocols](https://en.wikipedia.org/wiki/Gossip_protocol) would reconcile data between client and server, (4) the browser was 100% reactive to data changes, and (5) the server was in complete control because ["the network is the computer"](https://en.wikipedia.org/wiki/John_Gage). Now, there is a lot to unpack here, but the point is that building a new style of browser is a monumental task within itself.

The reason for starting a new browser is that I feel the web is a complete shit-show, and I hate building web products. I hate that your stupid website runs code on my machine. I also hate how a lack of privacy is a given in today's world. [Remember, I'm full of hate.](/blog/it-begins)

Putting aside the bucket of rage I feel when using the internet, I wanted to bring board games to mobile devices. That was the mission. My hope was to ship a simple binary which would utilize a single secure socket to do some magic and let the game play happen like an efficient remote desktop protocol.

Unfortunately, when you start building new UI frameworks with new idioms and low-level technologies, you kick off a massive empire building project which will require support and tools. Once again, I was shaving a Yak and building a new empire in praise of the glorious understanding that I have acquired about these stupid machines. I wish I had realized this before getting a working UI editor sort of done. **sigh**

The socket was a key thing to focus on. I prefer raw sockets because they are stateful and conversational. For board games, they are essential because of the inherent complexity of communication between players. The socket simplifies this because you can use the socket to mirror the server state, and all the complexity can be held within the single server.

![Key reason for socket](/img/20200512_diagram_key_moment.png)

The moment you have all state within a single server, the challenge of shipping a complex product is several orders of magnitude less due to practically zero failure modes. There is just one tiny problem... The reliability of a single server in today's cloud with crazy orchestration and almost constantly induced failures is not great.

Ignoring that tiny problem, I persisted since I had built a prototype tiny browser and if I control the hardware then I could probably be ok (right?). There I was building [my favorite game](https://boardgamegeek.com/boardgame/37111/battlestar-galactica-board-game) with [node.js](https://nodejs.org/en/) as my back-end, and I had finally made some decent progress on the game. The usage of a socket and some of the new UI idioms were proving fruitful. However, the server-side complexity became exceptionally overwhelming. Board games are non-trivial endeavors.

This is where the impetus for Adama was born. There were three key mission questions to drive. First, can that pesky single server limitation be overcome where machine failures are handled gracefully without user impact. Second, are there useful primitives which reduce the total complexity. Third, what essential truths were learned during from the simplified UI idioms.

These first two questions will be addressed at length in the future, but the last question however gave pause. Systems do not live in an isolated vacuum, and it is the role of the system to make itself useful beyond its immediate peer all the way to the user. Afterall, [Distributed systems are a UX problem](https://bravenewgeek.com/distributed-systems-are-a-ux-problem/), so we must operate on primitives which are useful to the UI and the UI developers.

First, we study the UI idioms which the toy browser used. They were straightforward, have been incorporated in the prototype of Adama:

* An Adama server can only receive messages from clients of two core types.
  * The first type is a free form message like "say hello" which has no rules associated to it. It's only for the pesky humans.
  * The second type is a response in request-response where the server asks the client a question (which piece do you want to move, where do you want to move it). This is the magic for implementing the board game logic in a sane way, and also the secret for enabling AI. This will be written about at length in the future.
  * Note: there is a possible third type where the client may send a request to Adama, and Adama will respond, but that is open to debate. It feels natural (and may be useful outside of the current domain), but it introduces dealing with the failures of the RPC. Instead, messages are stored in a queue on the client side and must replicate to the Adama server with exactly once semantics (i.e. at least once with deduping)
  Adama must keep the client state up to date and be consistent
* The entire application state is a giant object represented by JSON
  * An Adama server can differentiate the state and keep the client up to date with [json merge (rfc7386) for the win.](https://tools.ietf.org/html/rfc7386)
  * The UI must then process a stream of state differentials which are congruent to the initial payload (i.e. they have the same shape and form, but differentials have vastly less information)

![Ideal User Interface](/img/20200512_diagram_basic_ui_flow.png)

The entire application state being a giant JSON is reminiscent of [Redux](https://redux.js.org/) and the notion of an application state container. Now, this has the property that the UI simply needs to react to changes of a single object. In the toy browser, the role was to simply render the scene and then index the scene in a way to convert interactions into messages. With the browser, this requires work to expose new idioms, and this is where I am at. Since the JSON is predictable, there is a maybe new (or not) concept of an "object sieve subscribe".

```js
var sieve = GOAT.CreateSieve()
GOAT.SieveSubscribe(sieve, wrappedCallback);
```

The implementation of the above is basically to implement [json merge (rfc7386)](https://tools.ietf.org/html/rfc7386), and then publish out changes as they happen by walking a parallel object callback structure (ie. the sieve). We can gleam a basic idea of how this works with single example.

```js
GOAT.SieveSubscribe(sieve, {'turn': function(change) {
  GameLog.write(
    [  "The turn has changed from ",
       change.before,
       " to ",
       change.after
    ].join(""));
  document.getElementById('turn').innerHTML = change.after; 
}});

GOAT.SieveMerge(state, diff, sieve); // <- powers the entire engine
````

This example shows an interesting idiom where updates on the UI are ONLY derived from the update stream. There is no global re-computation, and no giant reconstruction for small changes. This property was important for the toy browser because I was aiming for a battery efficient engine where only updates would refresh the screen rather than periodically polling the scene.

However, there is a small hitch. [JSON Merge (rfc7386)](https://tools.ietf.org/html/rfc7386) does not handle arrays well, but this can be overcome by constructing a new merge operation which enables array differences. This requires the server to craft and embed meta signals in the delta, so that's what I am working on at this very moment until I got distracted by my thoughts. This will be described in more detail at a later date.

The core observation that I have had through-out this journey is that when back-end and front-work together, amazing properties are to be had. In this case, a small change from client to back-end results in a small change from back-end to other clients using a small amount of network. When clients learn of changes, they update proportional to the change at hand. No excess. No fuss. Just niceness.
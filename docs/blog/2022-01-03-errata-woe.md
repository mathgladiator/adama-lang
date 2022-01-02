---
slug: more-websocket-woe
title: Errata on the WebSocket Woe
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [websocket, rambling, errors]
---
**Woe (noun, literary): great sorrow or distress.**

There’s great fun when you post on HN and get those precious internet points in bulk, but the real value comes from more data. There were things that I failed to mention along with confusion.

## My bad: big miss on communication
One thing that felt like was a miss was that I was the nature of what I was writing. I wasn't saying "You shouldn't use WebSockets", but rather "There will be pain for using WebSockets and here is a list of pain points which you should think about as requirements"

Now, you may argue that's true for all technology, so this 

## Problem: the internet is a wild place
One problem to contend with which adds to the pain and suffering of using a WebSocket is that it isn’t [uniformly available with 98.4% availability](https://caniuse.com/websockets). Worse still is that some networks tank the connections with a corporate MITM which screws with your precious socket. The good news is that both of these problems can be solved with long polling or even [Server-Side Events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events). While long polling starts simple, it does have challenges to scale and you will basically learn why the features of TCP are the way they are (the hard way).

Fortunately, you can use a well-designed single server (scale using DNS partitioning) as a fallback for the rare population that can't use WebSocket. I think an architectural mistake is trying to have the WebSocket server doing all protocols when it probably makes sense to have a separate and isolated fleet doing transcoding. This will be the approach that Adama takes for those rare users.

As an aside, the fallback influences the protocol design which is why Adama biases towards streaming from server to client with request response towards server. This makes it very similar to using SSE for reads with regular HTTP for writes.

## Problem: never trust
Generally speaking, you shouldn't trust the browser, the server, or TCP for your product. You will need proof of life in both directions, and a great way to do that is with bi-directional heartbeats.

These heartbeats can be combined with some useful state to assess if anything has gone off the rails such that a reconnect needs to happen because of a bug. This is especially true if the WebSocket is just a proxy between the client and another streaming service, and those heartbeats should be end to end.

My bias is to use gossip/anti-entropy techniques to inspire how to quickly check if things are expected between client and server.

## Problem: the herd will thunder
Most catastrophic failure modes require an additional level of protocol that is not provided out of the box. You will need to leverage exponential back-off to detect if you are causing a problem. Unfortunately, an aggressive retry policy at massive scale is a cannon on every service.

While you can fix this and have your client be a polite citizen, you also have to contend with this internally as the world can be rather impolite. Ideally, create the connections as cheap as possible, but as they gain weight you will need to apply flow control internally along with some exponential back-off. Extra capacity can be exceptionally useful for handling self-induced DDoS attacks. The ability to scale on demand becomes important as you need the head-room to absorb traffic.

## Problem: this is an old space
In a way, using a TCP socket or a WebSocket is a well paved road. However, we forget that learning how to navigate the road is rougher than the train provided by well-designed abstractions like request response. There are plenty of HTTP clients that you can for your language which are amazing in terms of operability.

Unfortunately, with WebSocket being so open, you have to implement and design everything. This requires knowing a great deal of best practices such as:
* Pairing of callbacks to responses for sent requests
* Time-outs
* Flow control
* Standard error codes and error handling
* Retry and backoff
* Request metadata
* Multiplexing

The challenge of even picking WebSockets is that you have so much freedom. That additional freedom is not without additional responsibility and burden. Ultimately, this responsibility will add to the engineering cost because you must build a team which understands this as well.

## Problem: there are many protocols
Having used MQTT and RSocket, I can tell you that these protocols have leaks. Let's take MQTT an example.

You get your MQTT broker stood up, and then you get a client that has all the nice retry built into. Great! You issue your SUB and get a SUBACK back, amazing! This will work for many use-cases, but there is a transition when you start to care about reliability. Disconnects can cause implicit message loss because it works with ideal flow:
* client X subscribes to topic X
* client Y publishes to topic X a payload of "A"
* client X gets payload "A"

Seems reasonable? Well, what happens when client X loses connectivity
* client X subscribes to topic X
* time passes, and client X's network changes
* client Y publishes to topic X a payload of "A"
* client X reconnects and resubscribes
* client X never gets payload "A""

The only fix for this is that the publish stream needs to be made durable, but the MQTT protocol lacks a notion of sequencer when then you have to build into the protocol at a higher level. WELL, guess what, now you have a non-trivial game of pulling updates from another place. Should the protocol handle this? Maybe, but the point is there is no free lunch in terms of expectations.

It could be said that there are expectations of consuming some streams which require special application logic. This however means the protocol was lacking, and you don't capitalize on the wide breadth of the protocol. If you have to sprinkle logic across all languages, then you will need a client team to maintain all that shit.

You can take a look at [the request stream patent which was filled](https://patents.google.com/patent/EP3790253A1/en) for details of what a great protocol could look like if you care about reliability. This protocol summarizes all my learnings and bootstraps off of the language of [RSocket. RSocket may be the most winnable game in terms of a future protocol to build on top of.](https://rsocket.io/).

## The importance of abstractions
There was agreement on the need for abstractions, and that's what I was building up to as I am seeking to balance my trade-offs for Adama. With Adama, I have a specific engineering game that I am playing since I can't solve all the problems I want to.

Where I have landed is that the client will have the ability:
* multiplex multiple document connections on a single socket
* enable request-response for talking to a single document
* enable request-response for talking to the service
* reconnects will download a new copy of the document to get all updates; this document will be locally differentiated to produce events

## Disappointments on lack of feedback around the relativistic nature of stream
I'll be honest that I'm a bit disappointed that no one commented on the difficulty of using a traditional load balancer with stickiness for streams. Time is an exceptionally hard element to contend with a stream.

## Miss: The game should be winnable with abstractions
There are a lot of protocols, and I've used a few of them (like RSocket and MQTT). At core, when we sit down to build something, we are playing a game of sorts.

The question is whether or not that game is winnable. Currently, request response is a very winnable game because you can time out. You either get a response back or an error. The same isn't true for a stream. I love the language of request stream. You send a single request, and then you get a stream of updates back. In terms out that telling an inactive stream from a dead stream is very hard.

For Adama, I can make the game winnable such that a reconnect will generate a payload immediately. This allows me to timeout and measure first-payload latency. Periodically, I will ask what is the sequencer of the document and this will confirm two things. (1) am I up to date, (2) am I still connected. This predictable chatter will also be useful for measuring network conditions. Because the stream has a known schema, the game is winnable and investigations can be had as to why things are not working.

This defines the engineering game of even knowing if the stream is working at all. A hard question for anyone to answer when using WebSockets is how to measure reliability, and if you can't measure it well or get fuzzy then that is a strong signal that something is foul.

## Personal problem: counting small numbers
My career has been focused on massive scale. I have tilted towards viewing machines like cattle. When you have the massive scale and cattle ranching, you experience a tremendous number of errors which means the woes of WebSockets become requirements to contend with.

This taints my view of things, and wow do things become simpler if I just optimize a few machines to handle WebSocket. I recently posted about [the path to scale for Adama](/blog/the-path) as I think through the problems I want to solve for the SaaS side of the equation.

I'll be continuing this series to talk about how I intend to build a scalable and reliable service, and the key for me is the focus on reliability. I believe, very strongly, that reliability is the essential element for great sleep.

Sleep is going to be the #1 value for this future endeavor.


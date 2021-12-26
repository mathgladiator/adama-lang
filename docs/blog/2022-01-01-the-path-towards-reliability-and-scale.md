---
slug: the-path
title: The path towards reliability and scale
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, scale, saas]
---

As I have burned the bridge and committed towards an open source SaaS, I am designing how to handle the failure modes. Today, I'm looking at the path towards launch and asking if there is an opportunity to provide trade-offs to the future consumer as this unfolds along with the related business model.

For clarity, each diagram here represents a single cluster within a single region under a single DNS name.

## Ultra YOLO
We start with the simplest and most problematic. Take the code and then run the WebSocket frontend with Adama directly coupled within the process.

![a single service](/img/20220101_topology_1.png)

**Pros**
* Easy for me, a single command
* Simple to reason about
* Cheapest offering
* Lowest possible latency

**Cons**
* Not durable
* Unable to survive updates to both Web and Adama
* Reliability depends entirely on the lifetime of the host
* Scale is exceptionally limited to that single host
* Yearly data loss chance: 100%
* Easy to denial of service

**Does this have legs?**
This could be used as an ephemeral real-time solution since it is just a state machine routable via DNS. The business here is to resell hosts on the edge, and this could work exceptionally well with something like [fly.io](https://fly.io/).

## Mega YOLO
We take the Ultra YOLO and split the process with a front-end web tier connecting to an Adama host via gRPC.

![a single service](/img/20220101_topology_2.png)

**Pros**
* Simple to reason about
* Almost as cheap as Ultra, but the communication overhead reduces total capacity
* Still pretty quick
* Provides a natural pivot to double the scale of Ultra Yolo

**Cons**
* Not durable
* Unable to survive updates to Adama
* Reliability depends entirely on the lifetime of the host
* Scale is exceptionally limited to that single host
* Yearly data loss chance: 100%
* Easy to denial of service

**Does this have legs?**
See Ultra Yolo as this is similar with minor extra costs and additional latency where the key gain is the ability to update Web instance.

Since the key failure mode would be Adama deployments, this begs the question if ephemeral real-time solution could benefit from a state hand-off protocol where a new service is stood up and then takes over the state. This would bring the data loss chance from 100% to [5%](https://www.statista.com/statistics/430769/annual-failure-rates-of-servers/) unless there is operator error.

## YOLO
We take the Mega YOLO and introduce a database into the mix.

![a single service](/img/20220101_topology_3.png)

**Pros**
* Simple to reason about
* Best combination of latency and pretend-responsibility
* Able to survive software updates

**Cons**
* Not long term durable
* Reliability depends entirely on the lifetime of the host
* Scale is exceptionally limited to that single host
* Yearly data loss chance: 5%
* Easy to denial of service

**Does this have legs?**
It all depends on latency. The key problem is that this fakes quality with a submarine lack of durability. This will also depend on the domain, so this could be a legit option to offer people. This would be great as a small developer offering for testing.

## Durable
Replace managing a database with using an existing DB as a Service (i.e. RDS) which handle everything for you.

![a single service](/img/20220101_topology_4.png)

**Pros**
* Simple to reason about
* Durable
* Durability is handled by someone else
* Able to survive software updates

**Cons**
* Reliability depends entirely on the lifetime of the host
* Scale is exceptionally limited to that single host
* Easy to denial of service
* Vendor lock-in to DBaaS

**Does this have legs?**
At this point, this can look like a "buy an Adama host" and let customers do sharding within their app (i.e. pick a DNS). I feel this is a cheat, but this is the first offering that at least feels responsible.

## Defense against the interwebs
We can mitigate the denial of service by introducing multiple web proxies to front-load Adama traffic

![a single service](/img/20220101_topology_5.png)

**Pros**
* Simple to reason about
* Durable
* Durability is handled by someone else
* Able to survive software updates

**Cons**
* Reliability depends entirely on the lifetime of the host
* Scale is exceptionally limited to that single host
* Vendor lock-in to DBaaS

**Does this have legs?**
This extends the business of front-loading Adama's traffic, and to some degree the business competes with cloudflare for preventing denial of service attacks. This begs the key design question of whether customers should manage their own sharding logic.

## Expensive Load Balancer
Or, we can build an expensive high-throughput load balancer which can route traffic to Adama's shard. 

![a single service](/img/20220101_topology_6.png)

**Pros**
* Simple to reason about
* Durable
* Durability is handled by someone else
* Able to survive software updates
* Scales Adama up dramatically due to sharding Adama
* Reliability of Adama is managed by that expensive load balancer.

**Cons**
* Scale and reliability all depends on the expensive load balancer which is a single point of failure and choke point.
* Easy to denial of service
* Vendor lock-in to DBaaS

**Does this have legs?**
This will dramatically increase scale, but the single point of failure becomes an over-promise. Fortunately, it is stateless, so recovery should be quicky.

## Condenser
We can combine the distributed load balancer with the expensive load balancer. We rename that single load balancer as a condenser and then optimize the protocol as it has one job: routing streams.

![a single service](/img/20220101_topology_7.png)

**Pros**
* Durable
* Durability is handled by someone else
* Able to survive software updates
* Scales Adama up dramatically due to sharding Adama
* Reliability of Adama is managed by that expensive load balancer.
* Compute offload to the WebProxy reduces condenser and Adama burden

**Cons**
* Stack is getting longer with many pieces
* Latency is suffering multiple hops
* Scale and reliability all depends on the condenser which is a single point of failure and choke point.
* Vendor lock-in to DBaaS

**Does this have legs?**
That single point of failure may be a deal-breaker, but the initial web proxy fleet could be amortized across all customers and a customer could have a dedicated

## Distributed Load Balancer
Fixing the reliability of the choke point is as of the same of having a distributed load balancer between the web proxy and Adama tier. 

![a single service](/img/20220101_topology_8.png)

**Pros**
* Durable
* Durability is handled by someone else
* Able to survive software updates
* Scales Adama up dramatically due to sharding Adama
* Reliability of Adama is managed by consensus and fail-over
* Compute offload to the WebProxy reduces Adama burden

**Cons**
* Requires reliable failure detection
* Requires consistent routing
* Requires DBaaS to detect when routing is split
* Stack requires many new things to do all this stuff
* Latency will have more variability due to the failures induced
* Vendor lock-in to DBaaS

**Does this have legs?**
This is potentially the best case if failure detection can be brought in cheap enough. The key gap that requires to be crossed this time is handling the failure modes of the DBaaS failing a write due to a conflict emerging from a split brain. There are a variety of fixes for this which will be discussed in a later post.

## Distributed Load Balancer plus Sharded Database
The latency of the system will depend on the DBaaS, and we can take over the DB role. Assuming we build out the DB with replicas

![a single service](/img/20220101_topology_9.png)

**Pros**
* Durable
* Able to survive software updates
* Scales Adama up dramatically due to sharding Adama
* Reliability of Adama is managed by consensus and fail-over
* Compute offload to the WebProxy reduces Adama burden
* No longer vendor locked

**Cons**
* Requires reliable failure detection
* Requires consistent routing
* Requires DBaaS to detect when routing is split
* Stack requires many new things to do all this stuff
* Operational burden for managing databases

## Distributed Load Balancer with Chain Replication
If the DB schema remains simple, then I could remove all the overhead and complexity of the DB and manage it by using the local file system and build my own replication schema.

I'd start with chain replication, but there are [trade-offs when compared to raft/paxos](http://nil.lcs.mit.edu/6.824/2020/papers/craq-faq.txt). The reason I'd bias towards chain replication is that I would invent a bi-directional logger and leverage prediction.

![a single service](/img/20220101_topology_9.png)

**Pros**
* Durable
* Able to survive software updates
* Scales Adama up dramatically due to sharding Adama
* Reliability of Adama is managed by consensus and fail-over
* Compute offload to the WebProxy reduces Adama burden
* No longer vendor locked

**c**
* Complex
* Requires reliable failure detection
* Requires a service to manage the chains


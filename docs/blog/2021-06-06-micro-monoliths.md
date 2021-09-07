---
slug: micro-monoliths
title: Micro-monoliths and the future of infrastructure...
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, infrastructure]
---

I’ve got my silly opinions on this site, but generally my silly thoughts have a way of manifesting over time. In this post, I’m thinking about possible alignments of my thoughts with the broader industry. The key question is which of my ideas are worth going to production with the [limited innovation tokens](http://boringtechnology.club/) I would have if leveraged in a business.

First, the Adama language itself will take a tremendous time to finish both in terms of quality, features, tooling, documents, idioms, and what-not. I believe strongly in the language, but this would not make a great foundation for an enterprise business. Programming languages tend to gain a religious zeal to them, and it tends to be best to focus at making either a great library or a service with robust API.

Aside: speaking of religious zeal, I am now a fan of Rust. It’s great, and I am playing with WebAssembly. A personal goal this year is to be somewhat competent at both Rust and WebAssembly because the artifacts produced make me happy. Rust gives me confidence that we can have good software, and WebAssembly is the modern JVM that will be ubiquitous.

Focusing on the language could be a sketchy lifestyle business, and maybe that is ok? But... If the goal is to align and lead industry, then I could find success by building an infrastructure business around WebAssembly such that people could bring their own language.  This would allow that business to simply focus on supporting a black-box of logic, and then orchestration and state management would be the business.

This language could power a "state-machine/actor as a service" business, and it would be very similar to [Durable Objects from Cloudflare](https://developers.cloudflare.com/workers/learning/using-durable-objects). The key difference is that I’d hook up a WebSocket to it and invent [yet another robust streaming protocol (sigh)](https://patents.google.com/patent/EP3790253A1/en).

It would feel a bit like Firebase, but it would be much more expressive as you would build your own database operations within the document. Is there an open-source Firebase?

Beyond WebAssembly’s current popularity and tremendous investments, I believe that WebAssembly has a huge potential for disruption in how we think about building software. Generally speaking, the hot shit these days is Docker/k8s/micro-services. All the cool kids want their software to be super scalable, and they cargo cult practices that only make sense with hundreds of engineers. That’s fine and par for the course, but as the guy that generally cleans up messes and make services reliable; I shudder as I have a bipolar relationship with micro-services.

On one hand, they let engineers scope and focus their world view. Micro service architectures tend to solve very real **people** problems, but this comes at great cost. That cost manifests in extra machines, but also requires everyone to understand that networks are not perfectly reliable. Having a bunch of stateless services sound great until failures mount and your reliability sucks.

The other perspective relates to monoliths which enable engineers to build (more) reliable software (due to less moving pieces), but slow build times and hard release schedules make them undesirable because people. Distributed systems are hard asynchronous systems that are exceptionally expensive in terms of hardware, but people can move fast. Monoliths have all the nice properties, but scaling is people expensive and requires vertical growth.

This is where WebAssembly can enter the picture as you can re-create a monolith with [WebAssembly containers](https://github.com/deislabs/krustlet/) which can be changed during normal operation. This is a “micro-monolith” which fits as many conceptual services within a single machine as possible such that you get the people benefits of micro-services with a monolithic runtime. This thinking mirrors mainframes where [hardware can be replaced while the machine is running](https://www.youtube.com/watch?v=ipe-WywAnA0).

Developers already contend with asynchronous interfaces for services, so it is a productivity wash with respect to hands on keyboard coding when compared to microservice frameworks. The upsides comes from reduced operational cost and better responsiveness due to locality as compute can chase data and caches, and this has the potential to nullify the advantages of a traditional monolith.

The potential for removing the need to serialize requests, queue a write, let the network do its magic, read from network queue, and deserialize the request is exceptionally interesting. This reduces cpu work, decreases latency, increases reliability, and reduces heap and GC pressure. It's full of win especially when you think that a diverse fleet of stateless services feels exceptionally pointless and wasteful of network resources.

Paradoxically, it would seem these ideas are not new. We can check out the [actor model](https://en.wikipedia.org/wiki/Actor_model) or [Erlang](https://www.erlang.org/)/[Elixer](https://elixir-lang.org/)/[BEAM VM](https://en.wikipedia.org/wiki/BEAM_(Erlang_virtual_machine)) for spiritual alignment. It's always good when ideas are not new as it represents harmonization, and I feel my appreciation and education deepening within this field. I've come to believe that this mode of programming is superior (as many of the Erlang zealots would promote), but it has been held back by languages. WebAssembly feels like the way to escape that dogma, and the key is to produce the platform.

[Having written about DFINITY](/blog/wrapping-head-dfinity-internet-computer) with a technical lens, I'm realizing it may be a bigger deal in the broader sense. I now realize that decentralized compute will fundamentally transform the landscape for comnputing and manifest [infrastructure as a public utility](https://www.youtube.com/watch?v=vMKNUylmanQ) without corporate goverance. What will computing look like if both compute and storage are public utilities? What happens when racks can be installed on-premise and become assets that serve the broader community?

This is an exciting time to be alive, and the question then is what do I do? Stay tuned.
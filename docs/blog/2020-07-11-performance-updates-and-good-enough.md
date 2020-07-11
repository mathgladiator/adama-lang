---
id: performance-updates-and-good-enough
title: Performance Updates & Good-Enough?!?
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, ui]
---

This update spans events over four days of joyful suffering.

Day #1
------

I’m dumb. My benchmark code would randomly inject 1 ms delays due to a stupid [spin-lock type concept](https://en.wikipedia.org/wiki/Spinlock). This was introduced quickly because there is a scheduler which scheduled future state transitions. I fixed the state transition for 0 ms delays to be... well... instant, and the world is much better. Fixing just this took our previously reported 550 to 350 ms which is a massive reduction, but it also changes the perception of the impact of the prior work.

| ms | billing cost |
| --- | --- |
| 350 | 2328882 |

We can retrospectively re-evaluate the impact and adjust expectations. For instance, if 200 ms is pure testing overhead, then we can factor that in. That is, instead of comparing 350 to 740 (where performance began), we can compare 350 to 540 to measure the actual impact of the optimization work on production scenarios. While the testing environment saw a whooping 53% drop in time, the production environment only would see a 35% drop. While this is sad, I'm excited to see more testing happen faster.

**Moral of the day: measuring is hard.**

Day #2
------

The day started with a focus on improving testing, finding issues, and resolving some long-standing bugs and swamp of TODOs. I was very happy with the 90%+ unit test coverage on the runtime, but the coverage improved to the point where I had to deal with the cruft and tech debt because I didn't want to write unit tests which would become bunk. It was clear that I needed to invest in sorting out the persistence model and making it rigorous, and it was time to go all in on the "delta-model". Here, I want to spend a bit of time talking about the "delta-model" and why it is so important.

At core, we must use a distributed system for durability, and a key primitive to leverage is ["compare and set"](https://en.wikipedia.org/wiki/Compare-and-swap) which enables multiple parties to atomically agree on a consistent value. Adama was designed to exploit this, and we talk about the entire game that Adama plays by looking at how messages get integrated into a document. The below code lays out the game.

```js
  function integrate_message(msg, key) {
    // download document from store
    let [seq, doc] = get_document(key);
    // compute new document
    let new_doc = do_compute(doc, msg);
    // leverage compare and set to share the new document
    if (!put_doc_if_seq_matches(key, seq, new_doc)) {

      // it failed, try again
      integrate_message(msg);
    }
  }
```

Half of Adama is designed to make ```do_compute``` really easy to build with some special sauce between multiple users. An absolute key requirement for ```do_compute``` is that it must absolutely be a side-effect-free-honest-to-goodness-mathematical function otherwise the system becomes unpredictable. Now, Adama has been at this stage for a while via a series of shadow documents. The objects that Adama's code would interact were backed by a reactive object, and reactive objects were backed by JSON objects. 

![pure delta mode](/img/20200710-pure-delta-mode.png)

The way this worked is that changes all flow to the JSON, and the entire role of the reactive objects was to provide a cached copy to provide the ability to revert changes. That is, the entire document is [transactional](https://en.wikipedia.org/wiki/ACID). For instance, if a message handler manipulates the document then aborts a message for some reason, then those manipulations are rolled back. This ability to roll-back is exceptionally powerful, and we will see how in a moment. The work at hand was to simply throw away the shadow copies and just one giant reactive tree which could produce a delta on a transactional commit. Since JSON is the core format, we will emit JSON deltas using [rfc7386](https://tools.ietf.org/html/rfc7386). This required more code-generation and a lot of work, but it was producing deltas. But, we return to *why deltas*? The core reason to leverage deltas is because of physics, and physics is a harsh mistress.

Namely, what happens as document size increases with a compare and set system? 
* The time for both ```get_document``` and ```put_doc_if_seq_matches``` increase due to network cost.
* Whoever is executing ```get_document``` must deserialize, execute ```do_compute```, then serialize for ```put_doc_if_seq_matches```; these all cost resources which grow with document size.
* As time increases, the probability of conflicts emerges for ```put_doc_if_seq_matches``` which adds more time which starts to cascade with more time and more cost.
* Oh, and people are constantly downloading the document, so the resources on what-ever shard is providing ```get_document``` is undergoing more contention to simply share updates to other people. (And, how they know when to update is an entirely different system usually using [pub/sub](https://en.wikipedia.org/wiki/Publish%E2%80%93subscribe_pattern)

There be dragons in all services, but the short answer is these compare and set services work well for small fixed objects. These systems tend to have caps on what their document size is. For instance, [Amazon DynamoDB has a low 400KB limit](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Limits.html), and I almost guarantee that there is a frustrated principal at Amazon who thinks that value is too high. Now, there are a variety of ways of working within these limits, but they tend to shift the cost to more resources especially on the network. Instead, Adama proposes a core shift towards a more database-inspired design of using a logger.

Databases have the advantages that their updates can be replicated rather than direct data changes, and this enables Databases to become massive! This is the property we want to exploit, except we don't want to describe changes to the data. Adama's role is to deal with user messages, integrate them into the document in a natural way, then emit a data change seamless to the user. This is game changing, and the key is to understand what runs on which machine. Abstractly, I see a mathematical foundation to be exploited.

![pure delta mode](/img/20200710-equations.png)

This is the foundation for an entirely new service, but it requires clients to maintain state. That is, clients use must leverage stateful practices like using a WebSocket. Typically, stateful approaches have short-comings as application services tend to have gnarly problems, but Adama overcomes them. Let's explore what ```integrate_message``` becomes in this new world.

```js
  function integrate_message2(doc_reactive_cache, msg, key) {
    // pull any updates into our local cache
    sync_document(doc_reactive_cache, key);

    // compute a delta (half the purpose of Adama).
    // -- 1: do compute with side-effects
    // -- 2: roll-back side-effects
    delta = compute_prepare_delta(doc_reactive_cache, cache, key);

    if (!append_delta(key, delta)) {
      // well, we already failed, so let's maybe check back with the caller?
      // maybe the caller has more messages to send?
      // maybe the need for the message got cancelled based on new data?
      // maybe just convert msg to [msg]? maybe and get some batching love?
      // lot of opportunity in the retry here! we can even exploit flow control!
      integrate_message(doc_reactive_cache, msg, key);
    } else {
      // pull the commit down along with anything
      sync_document(doc_reactive_cache, key);
    }
  }
```

This has much nicer physics. As the document size increases:
* time is bounded by changes inflight
* the network cost is proportional to changes.
* the CPU cost is proportional to changes.
* the consensus is proportional to data changes.
* as conflicts emerge, messages can be batched locally which reduce pressure to minimize conflict
* batching locally enables us to exploit stickiness to *optimistically* eliminate conflict and further drive down cost.

And this is just what happens when using a reactive cache which can be blown away at any moment.

With the physics sorting out as way better, we must return to the decision to leverage a stateful transport like WebSocket because it may be a horrific idea as stateful services are exceptionally hard. The moment you have a socket, you have a different game to play on the server. This new socket game is mucher hard to win. Now, it is very easy to get started and achieve impact. However, the moment you consider reliability you must think about what happens when your stateful service dies. This is the path for understanding why databases exist. It's so hard that there is a reason that databases are basically empires!

In this context, using a socket is appropriate because it has one job: leverage the prior state that the connection has in a predictable way. For devices to the server, the socket is used simply as a way of minimizing data churn on the client. For instance, the "stateful server code" is simply:

```js
   function on_socket(connection) {
      var doc = get_document(connection.key);
      connection.write(doc); // the first element on the connection is the entire document

      while(sleep_for_update()) { // somehow learn of an update to the document
        var new_doc = get_document(connection.key); // fetch the entire document
        connection.write(json.diff(new_doc, doc)); // emit updates as merges
        doc = new_doc;
      }
   }
```

Now, there is room for improvement in the above with Adama as the language which generates document updates, but the core reliability is understandable and not very complex. This is the key to leveraging something like WebSocket without a great deal of pain or self-abuse.

Day #3
------
Holy crap, I’m dumb. I am just not smart enough to be doing what I am doing building a reactive differential programming language database thing. Found two big issue which invalidate all of my work and life... in a good way.

First up, and this is really bad... I wasn’t actually deleting items from the tables. I was just hiding them and not removing them from the internal tables which means most of the loops were filtering dead stuff. No wonder things were going slow. Fixing this alone dropped the time to less than 120. This was unexpected, but it is worth noting that I wasn't investigating performance at the time. Instead, I was investigating the correctness of the "delta-model", and there was something rotten in the mix. Testing the delta model requires accepting a message, producing a delta, then persisting that delta, then throwing away all memory (effectivelly turn the tiny server off), then rehydrate the state from disk. The goal is to produce a model where servers can come and go, but users don't notice.

![do users notice](/img/20200710-do-users-notice.png)

Well, as a user, I was noticing. It turns out there was a break in reactive chain, and this was the second issue. I was up until four am in the next day...

Day #4
------

Woke up, fixed the issue. It's amazing how sleep fixes things for you. An interesting observation was that testing the delta-model was showing a bug in the prior assumption validity of the test; that is, I found a deeper bug outside of my immediate changes. Unfortunately, that means my test case is no longer congruent with the previous test runs (the number of decisions dropped from 798 to 603). I've checked the delta log, and the new version appears correct at the point where the fault happened. Because it is doing the right thing now, I had to find a new one. The new test case has 802 decisions, and it comes in at 

| ms | billing cost |
| --- | --- |
| 119 | 1938080 |

Based on decisions, this feels close enough to call it a good enough test case given the results from day two were in the same ballpark. This means that the production environment would experience a 540 --> 120 time drop (78%) while the testing environment would have experienced a 84% drop, and user satisifcation would be up due to less bugs.

While I'm still not done dealing with all the fall-out of the delta model, I do have to wonder if this is good enough. Well, I know I can do better because in this world if I turn off client view computation, then the time drops to 49 ms which gives me hope that I can optimize the client view computation. However, I think for the day and probably the month, this will be good enough... or is it? Honestly, I'll probably explore creating client views with streaming json.
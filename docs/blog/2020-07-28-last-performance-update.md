---
id: last-performance-update-for-july
title: Last Performance Update (for July?)
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, ui]
---

From [last time](/blog/performance-updates-and-good-enough), the performance and user cost was sitting at:

| ms | billing cost |
| --- | --- |
| 119 | 1938080 |

This seems good enough...

Unfortunately, this number does not account for the end to end story where clients get changes rather than entire copies of their personalized view. Ideally, clients will connect and get an entire snapshot of the document, then subsequent updates will require a [JSON merge](https://tools.ietf.org/html/rfc7386) to integrate updates. Alas, the above number represents the cost to construct a copy of their complete personalized view for every update. Outside of that measure, we then produce a delta. So, let's account for taking the difference between two versions of the view.

| ms | billing cost |
| --- | --- |
| 350 | 1938080 |

Ouch! That's a punch to the gut. This makes sense since we are now computing the entire view, then comparing it to the previous view and emitting a delta. It is a bunch of work! Instead, what if we compute the delta as we go. That is, store a resident copy of the private view and then update it in a way which produces a delta as a side-effect. This means we avoid construction the JSON tree along with a bunch of memory allocations. This will then also avoid the costly JSON to JSON comparison.

| ms | billing cost |
| --- | --- |
| 137 | 1143089 |

That is not half bad. We are providing more value for almost the same cost. However, this work also reverted the benefits of caching views between people which is reasonable as people may be at different points of synchronization. However, it also revealed the costs of working with JSON trees, so let's remove them and use streaming readers and writers everywhere!

| ms | billing cost |
| --- | --- |
| 95 | 1143089 |

Yay, more work done at a lower cost is the way to go. Now, this is the last update for July, but it is also the last update on performance for a while. 95 ms is fairly good for 802 user actions over 4 users. That means we take 0.03 ms/user-action which is fast. I think this is good enough, but something else that is interesting emerged.

As part of testing, I validated that snapshots work as expected. A core value of this system is that you can stop the computation (i.e. deployment or crash) and move it to another machine without people noticing beyond a touch of latency. 

The test that I did was simple. After each decision, I'd snapshot the state, then throw away everything in memory, and then reconstruct the memory and compute the next decision. This naturally slows it down, but it also illustrates the opportunity of this concept. The measurements are sadness inducing because they are bad:

| ms | billing cost |
| --- | --- |
| 856 | n/a |


So, the inability to preserve state between actions is 9x more expensive on the CPU. This aligns with a view about [remote caches and fast key value stores](http://pages.cs.wisc.edu/~rgrandl/papers/link.pdf) which I believe need to go. The document's size is between 75 to 80K, so this begged a question of how bandwidth changes between versions. Now, here is where is easy to get confused, so let's outline the two versions with some pseudo code.

The first version is "cmp-set", and it is something that I could see be implemented via [AWS Lambda](https://aws.amazon.com/lambda/), so let's look at the client side code.

#### **cmp-set-client.js**
```js
while (true) {
  // somehow learn that it is the client's turn
  var decisions = await somehowLearnOfMyTurnAndGetDecision()

  // ask the user (somehow) to make a decision
  makeDecision(decision[k]);
}
```

#### **cmp-set-server.js**
```js
// server routed a decision based on the person
function on_decision(player, decision) {
  // download the entire state from the store/db
  var state = download_from_db();

  // teardown/parse the state
  var vm = new VM(state);

  // send the VM the message and let its state update
  vm.send(player, decision.channel, decision);

  // pack up that state
  var next_state = vm.pack();

  // upload the state with a cmp-set (failure will throw)
  upload_to_db_if(vm.seq, next_state);

  // somehow tell the people that are blocking the progress of the game
  vm.getPeopleBlocking().map(
    function(person, decisions) {
      person.sendDecisions(decisions);
    });

  // give the state to the current player
  return next_state;
}
```

Now, this example has all sorts of problems, but it shows how a stateless server can almost be used to power an Adama experience. You can refer to [first case study](/blog/first-case-study-and-thoughts) to contrast the above code to how this would work with Adama (without any hacks or holes). We can leverage the above mental model to outline two useful metrics. First, there is the "client bandwidth" which measures the bytes going from the server to all the clients (in aggregate). Second, there us "storage bandwidth" which measures all the bytes from the stateless server to some database or storage service. We can use the tooling to estimate these metrics for our example game. Here "cmp-set" refers to the above code, and we compare this to the adama version.

| dimension | cmp-set| adama | adama/cmp-set % |
| --- | --- | --- | --- |
| ms | 856 | 95 | 11.1% |
| client bandwidth | 24 MB | 1.17MB | 5% |
| storage bandwidth | 32 MB | 644KB | 2% |
| % client updates that are less than 1KB | 0% | 94.8% | &#8734; |

As a bonus metric, I also counted how many responses were less than 1024 bytes which can safely fit within an [ethernet frame (1500 MTU)](https://en.wikipedia.org/wiki/Maximum_transmission_unit). That is, close to 95% of the responses from the server can travel to the client within a single packet. This data is very promising, and it demonstrates the potential of Adama as a unified platform for building interactive products which are exceptionally cheap. I intend to dig into the 5% of responses which are larger than 1500 as another source of optimization, but my gut is telling me that I need to move away from JSON and lean up the wire/storage format. This should be low on my list of priorities... We shall see.
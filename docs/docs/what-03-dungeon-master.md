---
id: what-workflow-dungeon-master-as-a-service
title: Dungeon Master as a Service
---

How to control the flow of a game?
----------------------------------

With [time](what-the-living-document) and [messages](what-actors-are-actings) being able to drive changes to the **living document**, an important question is begging to be answered: How do messages from multiple clients get organized? This is where we combine the state machine model with messaging to turn Adama code into a ["dungeon master"](https://en.wikipedia.org/wiki/Dungeon_Master) (or, [a workflow coordinator](https://en.wikipedia.org/wiki/Workflow_management_system)).

This is accomplished by creating ["incomplete" channels which yield futures](https://en.wikipedia.org/wiki/Futures_and_promises). First, you define a message.

```adama
message PickANumber {
	int number;
}
```

Then, you define the incomplete channel.

```adama
channel decide_number : PickANumber;
```

Here, we see an incomplete channel which has no code associated to it, so what good is it? Well, the idea is to delay message delivery until something else asks for it. That is, other code is able to fetch a value from this incomplete channel _when_ it makes sense.

Let's imagine a simple (and stupid) game of "who can pick a bigger number?". Two people must contribute a number, those numbers are compared, and the winner is given a score point. Play continues forever as this game is a stupid game with no end.

In just this trivial game, there is a need to build a state machine formed by product questions:

* When do player 1 and player 2 learn they need to provide a number?
* How do we ask players to contribute?
* Do we ask players to contribute in sequence, or in parallel?
* If parallel, then how do we handle the ordering of contributions from players?
* How do we deal with duplicates from players?
* How do players deal with failure of sending a message?
* What happens if a connection to a player is lost?

This all manifests from failures and the difficulty of network programming. Network programming is exceptionally hard, but this is where Adama comes in to save the day. This game can be implemented with following logic:

```adama
// somehow, the document learned of the two people playing the game
client player1;
client player2;

// scores for the players
int score_player1;
int score_player2;

// somehow, the document got into this state, but when this runs
#play {
  // both players are asked for a number
  future<PickANumber> a = decide_number.fetch(player1);
  future<PickANumber> b = decide_number.fetch(player2);

  // we then await the numbers to be able to compare and score them
  if (a.await().number > b.await().number) {
  	score_player1++;
  } else {
  	score_player2++;
  }

  // let's play again for all time.
  transition #play;
}
```

The Adama code does a bunch, so there are comments to explain the more mundane elements. Critically important for this discussion, there are two key elements to focus on. First, this code is responsible for asking players for their numbers:

```adama
  future<PickANumber> a = decide_number.fetch(player1);
  future<PickANumber> b = decide_number.fetch(player2);
```

The **fetch** method on a channel will reach out to the client and ask for a specific type of a message for delivery on that channel. This fetch returns a future which can be awaited to return the message from the person. Notice, concurrency is built into this model and both players can contribute their number independently at the same time. A future represents a value which will arrive... in the future.

The second key element is found in the following code to get the contributions from the players:
```adama
  if (a.await().number > b.await().number) {
```

The **await** invocation here will block execution until the value arrives from the associated clients. [Now, this is nothing new in terms of programming language](https://en.wikipedia.org/wiki/Async/await). However, this is a fundamental game changer for data storage, and this is the key element which simplifies board game back-ends.

This allows the state machine of interaction between users to be constructed with the flow of code rather than modelling an *explicit* state machine. With Adama, this *implicit-code-flow-based* state machine is also durable such that the server running the code can change without people noticing.

The server is in control of who is responsible for producing data and when, and failures don't manifest in the experience (beyond elevated latency).

Mental Model: Restaurant Ordering System
----------------------------------------
The mental model for the document is to behave as a broker between parties.

For instance, if you order food online for delivery, then you really hope the chef gets the order. This requires the technology to monitor a transaction across human-scale timeframes. It may take minutes for the hostess or chef to commit to the execution of the order, or to provide feedback about the viability of the order.

This time frame elevates the challenge as the reliability and latency of the signaling between these two parties is critical. Adama greatly simplifies this challenge with low cognitive load.
---
id: what-workflow-dungeon-master-as-a-service
title: Dungeon Master as a Service
---

The introduction of [messaging](what-actors-are-actings) begs an important question: How do messages from multiple clients get organized? This is where we combine the state machine model with messaging to turn Adama code into a "dungeon master" (or, a workflow coordinator). This is accomplished by creating "incomplete" channels. First, you define a message.

```adama
message PickANumber {
	int number;
}
```

Then, you define the incomplete channel.

```adama
channel decide_number : PickANumber;
```

Here, we see an incomplete channel which has no code associated to it, so what good is it? Well, the idea is to delay message delivery until something else asks for it. That is, other code is able to fetch a value from this incomplete channel when it makes sense.

Let's imagine a simple (and stupid) game of "who can pick a bigger number?". Two people must contribute a number, those numbers are compared, and the winner is given a score point. Play continues forever as this game is a stupid game with no end. This game can be implemented with following logic.

```adama
client player1;
client player2;

int score_player1;
int score_player2;

#play {
  future<PickANumber> a = decide_number.fetch(player1);
  future<PickANumber> b = decide_number.fetch(player2);
  if (a.await().number > b.await().number) {
  	score_player1++;
  } else {
  	score_player2++;
  }
  transition #play;
}
```

The **fetch** method on a channel will reach out to the client and ask for a specific type of a message for delivery on that channel. This fetch returns a future which can be awaited to return the message from the particular person.

This is a fundamental game changer because this allows the state machine of interaction between users to be constructed with the flow of code rather than modelling an *explicit* state machine. In effect, the server is in control of who is responsible for producing data and when.
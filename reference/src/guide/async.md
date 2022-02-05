# Async with channels, futures, and handlers

## Handling messages directly

When [users connect](static-policies-document-events.md), the only way they can interact with the document is to send messages to the document. This requires a message like:

```adama
public string output = "Hello World";

message ChangeOutput {
  string new_output;
}
```

This establishes the shape of the communication, and we leverage a channel to open a pathway for messages to execute code. One option is to add a message handler:

```adama
channel change_output(client sender, ChangeOutput change) {
  output = change.new_output;
}
```

This enables users to send messages via the change_output channel which will execute the associated code. In this example, 'change_output' is the name of the channel which clients will annotate their message with to execute the associated code. Nothing stops you from introducing multiple channels with the same type.

```adama
channel change_output(client sender, ChangeOutput change) {
  output = change.new_output;
}
channel set_output(client sender, ChangeOutput change) {
  output = change.new_output;
}
```

## Waiting for users via futures

While accepting messages indiscriminately is great for applications such as chat, you need a way to combine multiple messages in an orderly fashion. Remember, Adama was designed for board games, and a good pattern to use is [async/await pattern within code](https://en.wikipedia.org/wiki/Async/await). Here, we define a message and an incomplete channel.

```adama
message SomeDecision {
  string text;
}

channel<SomeDecision> decision;
```

And any state machine transition block can then leverage the ```channel``` to pull data from a user.

```adama
private client player1;
private client player2;

#getsome {
  future<SomeDecision> sd1 = decision.fetch(player1);
  future<SomeDecision> sd2 = decision.fetch(player2);
  string text1 = sd1.await().text;
  string text2 = sd2.await().text;
  // do something with text1 and text2 to decide a winner, I guess?
}
```

Note, these two users can come up with the message independently and this decoupling of asking a person (i.e. the ```fetch```) and getting the data in code (i.e. the ```await```) enables concurrency.

### channel&lt;T&gt;.fetch(who)

channels have a fetch method that will result in a single message but has the semantics that the document can block forever. There is a mechanism called ```preempt``` which can unblock the state machine, but this is an experimental feature.

### channel&lt;T&gt;.decide(who, T[])

Similar to fetch, the decide function enables something very cool and is the recommended approach for board games. Decide will ask the user to pick exactly one item from the given array of options. This is exciting as this enables AI to autoplay games, but this is outside the topic of the documentation... for now.

# Async with channels, futures, and handlers

## Handling messages directly

When a [user connects](static-policies-document-events.md) to a document, the main way they can interact with it is by sending messages to the document.
A message for this purpose typically includes a number of fields, such as the message type, the user's ID, and other message content.
The document can then process the message and update accordingly, potentially changing its state or sending messages to other services.

The following example has a single document field along with a message.
```adama
public string output = "Hello World";

message ChangeOutput {
  string new_output;
}
```

In order to establish communication between the user and the document, a channel is used to open a pathway for messages to execute code.
Adding a message handler is one way to achieve this.
A message handler is a function that gets called when a specific message is received on the channel.
This function takes the message as its input, and it can execute any necessary code in response to the message.

```adama
channel change_output(principal sender, ChangeOutput change) {
  output = change.new_output;
}
```

In the example provided, the channel named 'change_output' is the pathway that clients will use to send their messages to trigger the associated code.
It is important to note that multiple channels with the same message type can be introduced to the system.

```adama
channel change_output(principal sender, ChangeOutput change) {
  output = change.new_output;
}
channel set_output(principal sender, ChangeOutput change) {
  output = change.new_output;
}
```

## Waiting for users via futures

The [async/await pattern within code](https://en.wikipedia.org/wiki/Async/await) is a useful way to combine multiple messages in an orderly fashion, and Adama was designed with board games in mind.
To use this pattern, a message type and an incomplete channel need to be defined.
This pattern is useful for games that involve multiple players taking turns or for applications that require the processing of a series of events in a specific order.

In asynchronous programming, message handlers or state machine transitions can be blocked using the ```await``` keyword.
This is particularly useful when it's necessary to ensure that messages arrive in a specific order, even when writing synchronous code.
By blocking the message handler or state machine transition using ```await```, the document can wait for a specific event to occur before proceeding to the next step.
This can help prevent errors or unexpected behavior that might occur if the events were not processed in the correct order.

This is the secret sauce that enables board games and workflow processing.

In this example, we define an incomplete channel.
```adama
message SomeDecision {
  string text;
}

channel<SomeDecision> decision;
```

An incomplete channel can be created and then used in either a message handler or a state transition.
The use of a state transition can be particularly useful when soliciting decisions from multiple users.
For example, in a scenario where two users need to make a decision, a state transition can be used to prompt each user for their input in turn.
By leveraging an incomplete channel in this way, the program can ensure that it receives the necessary input from both users before proceeding to the next step in the process.

```adama
private principal player1;
private principal player2;

#getsome {
  future<SomeDecision> sd1 = decision.fetch(player1);
  future<SomeDecision> sd2 = decision.fetch(player2);
  string text1 = sd1.await().text;
  string text2 = sd2.await().text;
  // do something with text1 and text2 to decide a winner, I guess?
}
```

This decoupling of asking a person (i.e. the ```fetch```) and getting the data in code (i.e. the ```await```) enables concurrency, allowing two users to come up with the message independently.
Note that this approach can improve the efficiency of the program and enable multiple tasks to be performed simultaneously.

### methods on channel&lt;T&gt;

| method signature                   | return type  | behavior                                                                   |
|------------------------------------|--------------|----------------------------------------------------------------------------|
| fetch(principal who)               | T            | Block until the principal returns a message                                |
| decide(principal who, T[] options) | maybe<T>     | Block until the principal return a message from the given array of options |

### methods on channel&lt;T[]&gt;

| method signature                              | return type | behavior                                                                 |
|-----------------------------------------------|-------------|--------------------------------------------------------------------------|
| fetch(principal who)                          | T[]         | Block until the principal returns an array of message                    |
| choose(principal who, T[] options, int limit) | maybe<T[]>  | Block until the principal returns a subset of the given array of options | 

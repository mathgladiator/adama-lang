---
id: reference-channels-handlers-futures
title: Channels, Channel Handling, and Futures
---

<h1><font color="red">Under Construction: Super Rough, Not Hardly Done</font></h1>

## Fast Intro: Any time messages

When [people connect](/docs/reference-connection-events), the only way they can interact with the document is to send messages to the document. This requires a message like:

```adama
public string output = "Hello World";

message ChangeOutput {
  string new_output;
}
```

this establishes the shape of the communcation, and we leverage a channel to open a pathway for messages to execute code. One option is to add a message handler:

```adama
channel change_output(ChangeOutput change) {
  output = change.new_output;
}
```

This enables people to send messages via the change_output channel which will execute the associated code. In this example, 'change_output' is the name of the channel which clients will annotate their messager with to execute the associated code. Nothing stops from introducing multiple channels with the same type.

```adama
channel change_output(ChangeOutput change) {
  output = change.new_output;
}
channel set_output(ChangeOutput change) {
  output = change.new_output;
}
```

## Fast Intro: Futures and letting people make decisions

While accepting messages is great and all for chat, the hard task is how to combine multiple messages in an orderly fashion. Remember, Adama was design for board games, and a good pattern to use

## Diving Into Details

### fetch

### decide

### choose

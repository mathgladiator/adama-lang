---
id: what-actors-are-actings
title: Actors are for Acting!
---

The Actor Model
---------------

The [actor model](https://en.wikipedia.org/wiki/Actor_model) is worth considering as it expresses many ideas core to computing. The **living** document which Adama defines is a limited actor of sorts. Namely, it can only receive messages from people (or devices) and make local decisions and updates based on those messages. At this time, it is unable to create more actors or send messages to other actors. However, this basic form enables the **living** document to be useful.

Adama allows developers to define messages:

```adama
message MyName {
	string name;
}
```

and then ingest them via a handler on a channel:

```adama
public string name;

channel my_channel(client who, MyName msg) {
  name = msg.name;
}
```

Here, **who** is the person sending the the message, and **msg** is the message in the given type. This is the primary way for the **living** document to learn about the outside world. **my_channel** is the name of the channel which is exclusively tied to the associated code. Internally, the JSON

```js
{
	"@channel": "my_channel",	
	"name": "Cake Ninja"
}
```

when sent to the living document will run the handler code and change the top level field name to "Cake Ninja".

The most important property of messages is that they are *atomically* integrated. This means that if a message handler aborts (via the **@abort** statement), then all changes up to the abort are reverted. For instance:

```adama
public string name;

channel my_channel(client who, MyName msg) {
  name = msg.name;
  if (name == "n00b") {
  	@abort;
  }
}
```

The above code will temporarily set the top level name field to "n00b" which is visible only to the running code, but the @abort will roll-back all changes encountered and it will be as if the message never happened. This is super important for ensuring the document is never inconsistent or torn.
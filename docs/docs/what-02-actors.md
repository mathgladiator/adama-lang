---
id: what-actors-are-actings
title: Actors are for Acting!
---

The Actor Model
---------------

The [actor model](https://en.wikipedia.org/wiki/Actor_model) is worth considering as it expresses many ideas core to computing. The **living** document that Adama defines is a limited actor of sorts. Namely, it can only receive messages from users (or devices) and make local decisions and updates based on those messages. Currently, it is unable to create more actors or send messages to other actors. However, this basic and reduced actor model enables the **living** document to be useful.

Adama allows developers to define messages:

```adama
message MyName {
	string name;
}
```

This Adama code outlines a structure with just a name field. Messages are used exclusively for messaging between the users and the living document. The next step is then to ingest a single message on a named channel:

```adama
public string name;

channel my_channel(client who, MyName msg) {
  name = msg.name;
}
```

This Adama code defines:
* a publicly visible string field called **name** (a singleton value scoped to the document).
* a channel named **my_channel** that outlines a function
* a variable within the function called **who** which represents a connection with a user (via the **client** type)
* a variable containing the message (**msg**) sent by the user
* a block of code to execute when the message arrives, and this code will associate the singleton name field **name** with the value coming from the human **msg.name**

This is the primary way for the **living** document to learn about the outside world. Internally, the following JSON represents the message sent on a persistent connection from a device:

```json
{
	"@channel": "my_channel",	
	"name": "Cake Ninja"
}
```

This document, when sent to the living document, will run the handler code and change the top level field name to "Cake Ninja".

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

The above code will temporarily set the top level name field to "n00b" which is [visible only to the running code](https://en.wikipedia.org/wiki/ACID), but the @abort will roll back all changes encountered, and it will be as [if the message never happened](https://en.wikipedia.org/wiki/Database_transaction). This is super important for ensuring the document is never inconsistent or torn.

Mental Model: Old School Chat Room
----------------------------------
The mental model for the document is a receiver of messages from clients connected via a persistent connection. This persistent connection has signals based on client connectivity, and these meta signals are exposed via **@connected** and **@disconnected** events which are exceptionally useful. 

```adama
@connected (who) {
	// keep track of who
}

@disconnected (who) {
	// remove who
}
```

These signals enable the document to identify who is connected and provide the features found in classical old school chat rooms: "Jeff has entered the conversation" and "Jeff has left the conversation".

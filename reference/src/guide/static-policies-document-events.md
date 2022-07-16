# Static policies and document events

A document will contain state, and it is vital to protect that state from unauthorized access or malicous actors. In this section, we will go through the details of access control:
* Who can create documents?
* Who can invent documents? And what does document invention mean?
* Who can connect to documents?
* Who can attach resources (i.e. files) to documents?

This is the first step in building anything with Adama because access control and privacy are important.

## Static policies

Static policies are evaluated without any state precisely because that state is not available. For instance, the ability to create a document requires a polices, so we introduce the ```@static {}``` construct which denotes a block of policies. Within the ```@static``` block are policies like ```create``` and ```invent```.

```adama
@static {
  create {
    return true;
  }
}
```

The above policy allows anyone to create a document within your space (#rude), so you will want to lock it down. For instance, we can lock it down to just adama developers.

```adama
@static {
  create {
    return @who.isAdamaDeveloper();
  }
}
```

We can also validate the user is one of your people using [a public key you provided via authorities](../reference/auth.md).

```adama
@static {
  create {
    return @who.fromAuthority("Z2YISR3YMJN29XZ2");
  }
}
```

The static policy also has a context which provides location about where the user is connecting from using ```@context``` constant.

```adama
@static {
  create {
    return @who.isAdamaDeveloper() &&
           @context.origin == "https://www.adama-platform.com";
  }
}
```

From a security perspective, origin can't be trusted from malicious actors writing bots as they can forge the Origin header.



> This clearly needs a lot of work! The key limits being the lack of data besides who the person is.
> If we add the ability to know the origin or key, then the possibilities open up.
> See [github issue #97](https://github.com/mathgladiator/adama-lang/issues/97) for more details.

Similar to the ```create``` policy function, there is the ```invent``` policy which works similar to ```create``` except works only when a connection fails to establish due to a lack of a document.
That is, if a document is not found, then we internally create it if the invention policy allows it and there is no message required for construction.

```adama
@static {
  create {
    return false;
  }
  invent {
    return who.isAdamaDeveloper();
  }
}
```

## Static properties

With the ```@static``` block, we can also configure fixed properties that balance cost versus features. For instance, the ```maximum_history``` property will inform the system how much history to preserve.

```adama
@static {
  maximum_history = 500
}
```

Here, at least 500 changes to the document will be kept active.

> There are some interesting opportunities to tune the system.
> For example, latency could be traded off for durability.
> For enterprise applications, this is a bad idea; however, for a game this may be great for low-latency experience.
> See [github issue #98](https://github.com/mathgladiator/adama-lang/issues/98)

## Document events

Document events are evaluated against a specific instance of a document. These events happen when a document is created, when a client connects or disconnects to a document, and when an attachment is added to a document.

### Construction

Fields within a document can be initialized with a default value or via code within the ```@construct``` event.

```adama
// y has a default value of 42
public int y = 42;
// x has a default value = 0
public int x;

@construct {
  // overwrite the default value of x with 42
  x = 42;
}
```

The ```@construct``` event will fire exactly once when the document is constructed.
Constructors can also accept a ```client``` and a single message argument.
For instance, the following is a more representative constructor.

```adama
message Arg {
  int init;
}

public int x;
public client owner;

@construct (client who, Arg arg) {
  owner = who;
  x = arg.init;
}
```

As documents can only be constructed once, this enables games and documents to have an authoritative owner and initial state which can be used within [privacy policies](privacy-and-bubbles.md).

### Connected and Disconnected

The primary mechanism for users to get access to the document is via a persistent connection (vis-à-vis [WebSocket](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)) and then [send messages to the document](async.md).
Before messages can be sent, the connection must be authorized by the document and this is done via the ```@connected``` event.

```adama
@connected(who) {
  return true;
}
```

This code is run when a person connects to the document, and if the return value is true then the connection is established.
Not only can they connect, but they also naturally disconnect (either on purpose or due to technical difficulties).
The disconnect signal is an informational event only, and is available via the ```@disconnected``` event.

```adama
@disconnected (who) {	
}
```

In both of these events, the **who** variable has a type of ```client``` which is the user. As a rule, only connected clients can:
* send messages
* read the document

This makes ```@connected``` the access control mechanism for authorizing people to see the document. For private documents, this places a great deal of burden on [the constructor](/docs/reference-constructor) to initialize the state with an owning ```client```.

### Attachments
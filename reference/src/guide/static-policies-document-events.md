# Static policies and document events

A document will contain state, and it is vital to protect that state from unauthorized access or malicous actors. In this section, we will go through the details of access control and answer these questions:
* Who can create documents?
* Who can invent documents? And what does document invention mean?
* Who can connect to documents?
* Who can attach resources (i.e. files) to documents?
* What resource got attached?

Unlike other document stores, access control is done within the platform at the lowest level.
This is the first step in building anything with Adama because access control and privacy are important.

## Static policies

Static policies are evaluated without any state precisely because that state is not available. For instance, the ability to create a document requires a policy, so we introduce the ```@static {}``` construct which denotes a block of policies. Within the ```@static``` block are policies like ```create``` and ```invent```.

### Answer: Who can create documents within your space?

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

The static policy also has context which provides location about where the user is connecting from using ```@context``` constant.

```adama
@static {
  create {
    return @who.isAdamaDeveloper() &&
           @context.origin == "https://www.adama-platform.com";
  }
}
```

From a security perspective, origin can't be trusted from malicious actors writing bots as they can forge the Origin header.
However, the origin header is sufficient for preventing XSS attacks and limiting accidental usage across the web.

### Answer: What is invention and who can invent documents within your space?

Document invention happens when a user attempts to connect to a document that doesn't exist.
If the document requires no [constructor](#construction) then the ```invent``` policy is evaluated.
If then ```invent``` policy returns true, then the document is created and the connect request is tried again.
This is useful for introducing real-time scenarios to an already mature user-base as construction can happen on-demand.

The ```invent``` policy works similar to ```create```.

```adama
@static {
  invent {
    return who.isAdamaDeveloper();
  }
}
```

The logic within ```invent``` can mirror that of ```create```.

## Static properties

With the ```@static``` block, we can also configure fixed properties that balance cost versus features or enable new modes.
For instance, the ```maximum_history``` property will inform the system how much history to preserve.

```adama
@static {
  maximum_history = 500
}
```

Here, at least 500 changes to the document will be kept active.
We also have ```delete_on_close``` which will automatically delete the document once the document is closed.
This is useful for ephemeral experiences like typing indicators.

```adama
@static {
  delete_on_close = true;
}
```

## Document events

Document events are evaluated against a specific instance of a document. These events happen when a document is created, when a client connects or disconnects to a document, and when an attachment is added to a document.

> Note: at this time, document events don't support @context. See [issue #118](https://github.com/mathgladiator/adama-lang/issues/118)

### Construction: How to initialize a document?

Fields within a document can be initialized with a default value or via code within the ```@construct``` event.

```adama
// y has a default value of 42
public int y = 42;
// x has a default value = 0
public int x;
// who owns the document
public client owner;

@construct {
  owner = @who;
  // overwrite the default value of x with 42
  x = 42;
}
```

From a security perspective, it is exceptionally important to capture the creator (via the ```@who``` constant) for access control towards establishing an owner of the document as this enables more stringent access control policies.

The ```@construct``` event will fire exactly once when the document is constructed.
As documents can only be constructed once, this enables documents to have an authoritative owner which can be used within [privacy policies](privacy-and-bubbles.md) and [access control](#connected-and-disconnected).

### Connected and Disconnected

The primary mechanism for users to get access to the document is via a persistent connection (vis-à-vis [WebSocket](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)) and then [send messages to the document](async.md).
Before messages can be sent, the connection must be authorized by the document and this is done via the ```@connected``` event.

```adama
@connected {
  return true;
}
```

The ```@connected``` event is the primary place to enforce document-level access control. For example, we can combine the constructor with the ```@connected``` event.

```adama
public client owner;
public bool open_to_public;

@construct {
  owner = @who;
  open_to_public = false;
}

@connected {
  if (owner == @who) {
    return true;
  }
  return open_to_public;
}
```

If the return value is true then the connection is established.
Since the ```@connected``` event runs within the document scope, it can both mutate the document and lookup data within the document.
A more stringent policy would have [a table](tables-linq.md) with who can access the document.
Above, we always allow the owner to connect and a boolean controls whether a random person can connect.

Not only can they connect, but they also naturally disconnect (either on purpose or due to technical difficulties).
The disconnect signal is an informational event only, and is available via the ```@disconnected``` event.

```adama
@disconnected {	
}
```

This allows us to mutate the document on a person disconnecting.

### Asset Attachments

Adama allows you to attach assets to documents.
Assets are essentially files or binary blobs that are associated with the document.
Since the storage and association of assets is non-trivial, there are two document events for assets.

### Answer: Who can attach a file to your document?

The ```@can_attach``` document event works much like ```@connnected``` in that the code is ran and the person attempting to upload is validated.
If the event returns true, then the user is allowed to attach the file.

```@can_attach {
  return @who.isAdamaDeveloper();
}
```

This event primarily exists to protect user devices from erroneously uploading attachments. After this event returns true, the user will begin the upload which will durably store the file as an asset.

### Answer: What resource was attached
Once the asset is durable stored, the ```@attached``` event is run with the asset as a parameter.

```adama
public asset most_recent_file;
@attached (what) {
  most_recent_file = what;
}
```

The type of the ```what``` variable is ```asset``` which has the following methods.

| method | return type |what it is |
| --- | --- | --- |
| name() | string | The name provided by the uploader (i.e. file name) |
| id() | string | A unique id to denote the asset |
| size() | long | The number of bytes of the asset |
| type() | string | The [Content type](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Type) of the asset ( |
| valid() | bool | The asset is real or not. The default value for an asset is ```@nothing``` |

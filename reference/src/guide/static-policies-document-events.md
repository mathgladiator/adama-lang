# Static policies and document events

In Adama, protecting the state of a document from unauthorized access or malicious actors is a crucial aspect of designing a secure and reliable application. In this section, we will delve into the details of access control and explore the key questions that arise in this context.
Specifically, we will answer these questions:

* Who can create documents?
* Who can invent documents? And what does document invention mean?
* How do I tell when a document is loaded? How to upgrade document state?
* Who can connect to a document?
* Who can delete a document?
* Who can attach resources (i.e. files) to documents?
* What resource got attached? And when do assets get deleted?

One of the key features that sets Adama apart from other document stores is its approach to access control. In Adama, access control is built directly into the platform at the lowest level, rather than being implemented as yet another configuration on top of the system. This design choice ensures that security and privacy are core features of Adama, rather than afterthoughts or add-ons.

As a result, access control is an essential first step in building anything with Adama. By carefully designing your access control policies and privacy rules, you can ensure that your users' data is protected from unauthorized access or misuse, while still providing them with the functionality and features they need to get the most out of your application.

## Static policies

Adama uses static policies to enforce access control rules that are evaluated without any state information, precisely because that state is not available at the time the policy is evaluated.
For example, the ability to create a new document in Adama requires a policy to be in place.
To define static policies, Adama provides the ```@static {}``` construct, which denotes a block of policies that are evaluated at the time the policy is enforced.

Within the @static block, developers can define a variety of policies, such as ```create``` and ```invent```, that restrict or allow users' access to create documents.
These policies are evaluated based on the user's identity and other relevant metadata, such as their IP address or location, to determine whether they have permission to perform a particular action.

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
public principal owner;

@construct {
  owner = @who;
  // overwrite the default value of x with 42
  x = 42;
}
```

From a security perspective, it is exceptionally important to capture the creator (via the ```@who``` constant) for access control towards establishing an owner of the document as this enables more stringent access control policies.

The ```@construct``` event will fire exactly once when the document is constructed.
As documents can only be constructed once, this enables documents to have an authoritative owner which can be used within [privacy policies](privacy-and-bubbles.md) and [access control](#connected-and-disconnected).

### Load: how to run code when a document is loaded?
Since documents may need to experience radical change, the ```@load``` events provides an opportunity to upgrade data when loaded.

```adama
@load {
  if (version < 2) {
    // transfer data to new world
  }
  if (version < 3) {
    // transfer data to super new world
  }
  version = 3;
}
```

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
public principal owner;
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

### Delete
When document deletion is requested from via the API, the ```@delete``` event runs to validate the principal can delete the document.

```adama
@delete {
  return @who == owner; 
}
```

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

| method | type |what it is |
| --- | --- | --- |
| name() | string | The name provided by the uploader (i.e. file name) |
| id() | string | A unique id to denote the asset |
| size() | long | The number of bytes of the asset |
| type() | string | The [Content type](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Type) of the asset ( |
| valid() | bool | The asset is real or not. The default value for an asset is ```@nothing``` |

> Note: at this time, using assets is a pain. See [issue #120](https://github.com/mathgladiator/adama-lang/issues/120)

### Answer: How do assets leave
Since there is a maximum history for the lifetime of a document, assets are periodically collected and then garbage collected. Once an asset object leaves the document and the stored history, the associated bytes are cleaned up.

> Note: this is not true at this time. See [issue #121](https://github.com/mathgladiator/adama-lang/issues/121)

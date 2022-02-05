# Static policies and document events



The primary mechanism for users to get access to the document is via a persistent connection (vis-à-vis [WebSocket](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)) and then [send messages to the document](/docs/reference-channels-handlers-futures). Before messages can be sent, the connection must be authorized by the document and this is done via the @connected keyword.

```adama
@connected(who) {
  return true;
}
```

This code is run when a person connects to the document, and if the return value is true then the connection is established. Not only can they connect, but they also naturally disconnect (either on purpose or due to technical difficulties). The disconnect signal is an informational event only, and is available via the @disconnected keyword.

```adama
@disconnnected (who) {	
}
```

In both of these events, the **who** variable has a type of ```client``` which is the user. As a rule, only connected clients can:
* send messages
* read the document

This makes ```@connected``` the access control mechanism for authorizing people to see the document. For private documents, this places a great deal of burden on [the constructor](/docs/reference-constructor) to initialize the state with an owning ```client```.

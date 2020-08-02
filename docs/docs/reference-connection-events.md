---
id: reference-connection-events
title: Handling Connection Events
---

The primary mechanism for people to get access to the document is via a persistent connection (vis-à-vis [WebSocket](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)) and then [send messages to the document](/docs/reference-channels-handlers-futures). Before messages can be sent, the connection must be authorized by the document and this is done via the @connected keyword.

```adama
@connected(who) {
  return true;
}
```

This code is run when a person connects to the document, and if the return value is true then the connection is established. Not only can they connect, but they also naturally disconnect (either on purpose or due to technical difficulties). The disconnect signal is an informational event only, and is available via the @disconnected keyword

```adama
@disconnnected (who) {	
}
```

In both of these events, the who has a type of ```client``` which is the person.

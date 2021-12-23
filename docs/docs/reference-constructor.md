---
id: reference-constructor
title: The Constructor (@construct)
---

## Fast Intro

Documents have a lifecycle, and we can run a block of code when the document is created.

```adama
public int x;

@construct {
  x = 42;
}
```

will construct the document:

```js
{"x":42}
```

## Diving Into Details

Constructors can also accept a ```client``` and message argument. For instance, the following is a more representative constructor.

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

As documents can only be constructed once, this enables games and documents to have an authoritative owner and initial state which can be used within [privacy policies](/docs/reference-privacy-and-bubbles).
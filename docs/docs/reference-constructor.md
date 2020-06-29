---
id: reference-constructor
title: The Constructor (@construct)
---

<h1><font color="red">Under Construction: Super Rough, Not Hardly Done</font></h1>

## Fast Intro

Documents have a lifecycle, and we can run a block of code when the document is created.
```adama
public int x = 123;
@construct {
  x = 42;
}
```
will construct the document:
```js
{"x":42}
```

## Diving Into Details

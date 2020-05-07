---
id: what-the-living-document
title: Living Document & Tiny Servers
---

What is a Living Document?
--------------------------

Let's start by defining a **living** document as the opposite of a **dead** document. Take a look at the following example JSON document:

```js
{
	"name": "Jeff",	
	"state": "Washington",
	"title": "Dark Lord",
}
```

This example JSON is a representation of a person living in the great state of Washington with an awkward title (it's me, tee hee), but this JSON is **dead**; it requires external stimulus to change. If you put this document inside a typical document store without any additional updates, then it will remain that way for as long as that document store exists.

A **living** document is the opposite of a dead document. That is, a living document can be put within a _living document store_, and the document will update and change on its own based on several factors. One such factor could be time. Take for instance the following Adama code:


```adama
public int ticks;

@construct {
  ticks = 0;
  transition #tick in 1;
}

#tick {
  ticks++;
  transition #tick in 1;
}
```

This Adama code defines
* a publicly visible integer field called *ticks*.
* a constructor which runs when the document is created to initialize ticks and kick off the state machine
* a state machine transition which runs every second and increments the *ticks* field

In effect, a living document is just a state machine on top of a JSON document that can transition in three ways: time, messages, or shared data changes. The above example illustrates time. See [Actors](/docs/what-actors-are-actings) for more details about how messages come into the picture, and see shared data changes is still being designed (TODO: update link).

Alternatively, a living document is a tiny **persistent** server.

Mental Model: Tiny Persistent Servers
-------------------------------------

An exceptionally tiny _persistent_ server is an alternative view of this concept. This is what the merging of state and compute looks like with Adama. With Adama, you outline the shape of your state and then open up mechanisms for how that state changes. This is comparable to building a server in what-ever language you want except the discipline to correctly persist state outside of the server is handled for you by the run-time.

For the target domain of board games, this is exceptionally useful because representing the state of board games is a difficult task within itself. With Adama, a single document represents the entirety of a single game's state via a singular definition.

Now, representing the state inside a single document provides an exceptional array of features: versioning, debugging via time travel, an imagination for androids, atomic and consistent boundaries, locality homing, and more. These features will be talked about in future 

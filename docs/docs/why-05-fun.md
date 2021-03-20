---
id: why-keeping-it-fun
title: Building Should Be Fun
---

Writing code can be fun, but there are many obstacles to achieving the orgasmic dopamine hits possible when things are working well. When progress is being made, good times are ahead. More often than not, coding can be exceptionally frustrating. Write code, then compile, wait, wait some more, oh crap, and things are still broken. Maybe write this code here, refresh the page, it isn't working. Why not?

Adama takes the position that tooling should be mostly in your face and out of your way. Adama is being designed where productivity and diagnostics are not just add-ons, but central features. Fun comes from speed and no waiting.

* Adama's validator and code compiler must run in single digit milliseconds for moderately large game back-ends.
* Adama's deployment must run in a sub-second timeframe.
* Tests written within Adama run in a sub-millisecond timeframe.

With such speed, each key-stroke can be validated, and if things are not working then real-time diagnostics will tell you why (with your text editor of choice via [LSP](https://microsoft.github.io/language-server-protocol/)). If validation worked, then a deployment will happen, tests are run and reported (via LSP), and results are visible within the product. Keep in mind, Adama is aiming to be competitive with Excel. Typing in Excel produces results!

The same must be true in Adama. However, going fast sometimes causes a new set of problems. For instance, after a deployment, human interaction in a game may be needed to test it. Well, this interaction can change the state in a bad way. Thus, Adama must enable time travel!

Since Adama takes care of all state management, it also provides coherent ways of rewinding state and snapshotting state. Tests can be written against snapshotted state, and the test results can be reported in real-time.

Going fast, in a fun way, enables products to achieve results that are beyond "good enough". So much so, Adama unlocks new ways of thinking about automation.

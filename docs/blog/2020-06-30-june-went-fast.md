---
id: june-went-fast
title: June went by fast! Woah!
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, ui]
---

I’ll be honest, I didn’t get as much as I wanted done beyond a bunch of clean-up on the language and develop a few new language features. I am however much happier with the state of the world now, but I’m avoiding a few things like deprecating some dumb ideas.

I finished going ham on the parser. Rewriting the entire parser from scratch by hand required rebuilding my trust in it, so I set out to get test coverage on the parser and the type checker. The core language has 100% test coverage, and this process uncovered many bugs. It further required resolving many TODOs within the code. The parser swap was a deep change, and while I’m very happy with it and the correctness of the language with my over 1,500 tests; the time has come to polish the error messages and align the character positions with the errors to be meaningful. This is the way for July.

Before I went ham on the parser, I made progress on simulation. I simulated 1.2M games over night. I let all my cores go, and I discovered that complete games could be played in under a half a second.  The purpose of these simulations were to randomly play games looking for dead-ends, crashes, or game-ending problems. The time per game-decision was about 2.1 ms which is exceptionally high. Performance being a non-goal meant I’d move on.

Something amazing happened  after going ham on the parser, fixing issues, eliminating some hacks, and improving the type system. The simulation performance tripled and now the time is about 0.7 ms per game-decision. w00t. I hope to spend a touch of time in July optimizing performance a bit because it is fun, and I hope to double the performance again.

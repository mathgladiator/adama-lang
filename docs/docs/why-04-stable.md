---
id: why-keeping-things-stable
title: A Commitment Towards Stable
---

For a variety of reasons, code requires maintenance. Furthermore, software people are expensive (really expensive), so maintenance is expensive. This is a problem for crafting software because opportunity cost has to come in balance with keeping the lights on.

In theory, some things can be done *right*, but it is so hard that we can have scope the conditions of the game being played. Now, Adama, the language and runtime, has the goal to provide a stable platform. The back-end for a board game written in 2021 with Adama must still function in the year 3025. Why is this a goal? Well, it sounds fun, that's why.

Adama aims for long term stability, and solving this requirement requires understanding why code requires maintenance in the first place.

First, it is hard to get code right, so we have to fix issues. The good news is that this isn't the language's problem; it's your problem. If the code is wrong, then stability would mean it will stay wrong... forever. Keep in mind, COBOL still exists. Code broken in specific ways should remain broken.

Second, code does not exist in a vacuum. The code must run on a machine, and the code must leverage a run-time. Achieving stability requires defining how to leverage a machine, and then being exceptionally careful with the run-time.

Machines are, more or less, stable enough. Fortunately, machines can be emulated with virtual machines. Adama is initially targetting a virtual machine which _*should*_ last a 1000 years: the JVM. The JVM can run code from the 90s, but there can be breakage. For instance, that breakage generally happens at the edge which are explored due to performance. For Adama, we will therefore drop extreme performance as a requirement. Performance is a nice to have, but it is more important to focus on conceptual simplicitly and keeping the math at the elementary level.

Run-times require great care to build to remain stable. This means when something is added to the run-time, then it should remain functional for a very long time. With Performance a non-goal, the goal is for the run-time to only expose deterministic and simple features.

Achieving a stable run-time is a serious burden. Every API in Adama gets a serious amount of formal and rigorous scrutiny. As an example, there are no plans to add any "get the current time" function. The core reason is that function doesn't exist in a mathematical sense, and it is not deterministic and stable. For productivity, Adama will import some libraries, but only a minimal number of them are high quality and can adapt standards into the minimal interfaces that Adama supports (i.e. Netty for the server). Adama's code base will mostly be a monolith.

Third, stability requires a closed ecosystem. Adama will not support direct networking or disk access. This is why many games from the 90s can be emulated with great success, and this is why some MMOs are gone forever.

Fourth, stability requires a lack of growth. Adama and the run-time is limited such that no more than 1,000 people can play a single game at a time. There, growth solved!

The reality of this desire to achieve stabiliy is a disciplined approach. This project is not going to be done in a weekend, but a decade.

> Slow is smooth smooth is fast.


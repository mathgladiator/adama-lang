---
id: going-ham-on-parser
title: Going HAM on the parser with next level testing
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, ui]
---

Finding the balance between what to work on is a challenge when there are so many interesting problems ahead. Testing the back-end for the current game has proved to be a challenge due to the entropy involved, so I’m thinking about ways of building tooling to corral that complexity and chaos.

However, the game works, but I have yet to reach the reality of the first milestone of playing with friends (UI sucks, afraid of a game-stopping bug). I am going to focus on improving the core in two ways. First, I want to improve the error messages. Second, I want to bring real-time code coverage into the picture as part of the test tooling.

I started with [ANTLR](https://www.antlr.org/) to do the parsing and help me build the trees backing the language primitives. However, I started to get frustrated with what ANTLR does with white space and the error messages it produces. While that is relatively minor, it also requires me to rethink all my trees to accomplish some goals. For instance, I want to make it easy to do refactoring and code completion. I also want to support LSP to bring comments on field via the hover mechanism. In using ANTLR, I have found truth in some wisdom from [why others recommend not using parser tools.](https://www.digitalmars.com/articles/b90.html)

The objectives of the new parser are thusly:
* Have a unified way of thinking about comments which can annotate any and everything
* Have the parser become an ally in how to format the code
* Have great error messages
* Make it easy to associate environments to the token space such that code completion queries are fast and relevant.
* Be fast (it is already 20% faster)
* Fix some keyword issues such that things which identifiers are forbidden versus not.
* Achieve 100% code coverage on the parser
* Fail on the first error, it is simply better that way

With the new parser, I will have plenty of information to think about code coverage at the character level. With code coverage and the code indexed with comments, I can build a tool where the test cases are generated for me and the transitions are generated to achieve code coverage. The aim is to use AI techniques to minimize the total number of automated tests to simplify the validation.

Now, this requires more thought and more words to elucidate, but imagine having a UI which shows you the raw state, the code with comments that is being tested, and the related state change of running that code. This can be certified as a good and right state change (by potentially multiple people), and this test can be saved for posterity and re-ran later. However, if changes happened, then those changes can be validated as either new data changes, loss of a data change, a different data change and require new certification.

Existing tests are fed in to establish a base line code coverage, and new tests can be generated such that code coverage is maximized with minimal test certification. For instance, the introduction of a new card could require several test cases to be randomly generated such that the shuffling of the card happens early. Only the test cases which extend code coverage need to be stored and certified.

It feels exciting that I’ll never need to mock or write test code ever again since the goal of testing is primarily about reducing entropy and maximizing determinism.

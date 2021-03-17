---
id: why-redefine-ai
title: AI Wars and Get Testing for Free
---

Since this all started with <a href="https://boardgamegeek.com/boardgame/37111/battlestar-galactica-board-game">Battlestar Galactica</a>. The question has been raised about how to leverage artificial intelligence, and it turns out that the discipline that Adama enforces with automatic state management and time travel enables artificial intelligence to have an imagination at hand.

In a game with perfect information game, Adama gives developers the option to specify which decisions are possible within the game at a particular time. The AI can fork the state and start evaluating the tree of options against the code. This begs many questions, but the state space of the game is available. Now, this creates a very interesting challenge, and this is active research. However, the important element is the ability to fork state.

Unfortunately, forking state for an imperfect information game reveals too much (either about other players or the future). This is where Adama exposes a primitive to assist with the "hey, this is forking, let's add some entropy", and this primitives allows sampling of the real state such that games are able to leverage techniques that work with perfect information game.

That is, take a game that is in play, then keep everything that has been revealed the same and then generate hypotheses for what could be (for instance, by swapping the ownership of two game play elements that are unrevealed). This is an imagined instance of what could be reality and allows the AI to derive conclusions over samples. These samples provide a wealth of data for making a reasonable decision.

This does not magically solve AI, but it provides a platform for the ultimate meta game: Pick a game, write AI, who wrote the better AI?

For practically minded folks, this has an interesting productivity consequence: [Fuzzing](https://en.wikipedia.org/wiki/Fuzzing)! Answering the question "Does this product have bugs or crash?" is of huge importance for products.

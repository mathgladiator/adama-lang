# Adama Programming Language
A language, run-time, and service platform for building board games online. Now, this begs the question: why on earth do board games need their own programming language. Well, many modern board games are exceptionally hard to model because they rely on the human mind to evaluate the complex interactions which emerge from a loose set of rules.

Adama aims to simplify the process of building a board game by providing a curated set of idioms which allow expressing the board games rules with low cognitive effort. For more information on why, please refer to [the origin story](http://www.adama-lang.org/docs/why-the-origin-story) over at http://www.adama-lang.org/

Now, the what is in a process of definition, and there are some hints over at http://www.adama-lang.org/

# Open Source? OK, where is the code.
I am planning a big release for the code since it is a mess right now. I'll tell you where I am at. I have a:

* working server
* working parser (with useful errors), and a new parser written by hand for better error messages
* working type system (with useful errors)
* working translator to Java
* working integration with the Java compiler to expose code to the server
* a "good enough" runtime
* a slim standard libary
* a language server protocol implementation for diagnostics (working on the new parser to help me index for code complete and refactoring)
* a complete back-end for a game that I love (yes, it is [BSG](https://boardgamegeek.com/boardgame/37111/battlestar-galactica-board-game))

That's a lot, so why hold back... Well, there are oddities which I want to smooth over. The focus on shipping the game forced me out of shaving the Yak mode, but I need to return and polish the things I care about.

I also want to sort out the right license, and blah-blah-blah... more bullshit excuses for not shipping. I know, I'm working on it in my weekends.
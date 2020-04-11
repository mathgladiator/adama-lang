---
id: why-the-origin-story
title: Origin Story
---

This programming language was born from a desire to bring a great game board-game online: <a href="https://boardgamegeek.com/boardgame/37111/battlestar-galactica-board-game">Battlestar Galactica</a>. [I](/docs/who-jeff) absolute love board games. Unfortunately, every time I try to implement one online, I find myself broken hating everything about the technology I use.

At core, [I](/docs/who-jeff) believe board games represent a limit point of both technical and product complexity where traditional web techniques break down. However, old-school gaming techniques work better, but these old-school techniques have their own issues. The first motivation of this project is to bridge how web and old-school gaming techniques can work together in a cohesive way.

The complexity of a board games manifest when describing the implicit state machine required for people to communicate and execute complex rules. We take day to day conversations for granted, and the best way to overcome this technically is to eschew classical HTTP/1.1 and use [WebSockets](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API) (or just a socket). However, the moment you embrace WebSockets, you have a whole new world of hurt because the internet is not reliable enough for a six-hour board game.

Furthermore, [I](/docs/who-jeff) believe good things happen when compute and storage come together. I've worked and tinkered in this space for a while, and Adama is the culmination of 20 years of problems and experience into a single language, runtime, and platform.

Now, I admit, things will not be perfect. This language is intended to be niche and limited, and I want to be exceptionally upfront about this. I don't want to over-promise that this language will cure cancer or any fanciful claims of grandeur, but I do believe we are living in a dark ages of sort with these machines. Ultimately, I strongly believe we can do better, but doing better requires putting a stake in the ground as to what better looks like.

This project is a stake in the ground.

About the name
--------------
Adama was a special Lamancha goat that my wife and I raised until he passed due calcium stones that blocked his urethra. He was an adorable goat that would love to cuddle, and I named this project after him.
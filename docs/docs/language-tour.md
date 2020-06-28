---
id: language-tour
title: A Language Tour
---

This document is a light-weight tour of the core features and ideas which make Adama a some-what novel [data-centric programming language](https://en.wikipedia.org/wiki/Data-centric_programming_language). It is important to remember that Adama is not a general purpose language, it’s just for board games (and maybe more... much more).

## Defining State Layout

We start by outlining some global fields with default values using minimal ceremony. Laying out and structuring data is arguably the most important activity in building software, so it must be simple. Here, we will define a document with just a name and score field:

```adama
public string name = "You";
private int score = 100;
```

This fairly minimal Adama is all one needs to define a document. The data backing this script is a document represented via the following JSON:

```json
{"name":"You", "score":100}
```

However, a human viewer (such as yourself or myself) of the document will only see:

```json
{"name":"You"}
```

This is because the **score** field is marked private. Only the code within the document can operate on the **score** field. Now, a key theme within board games is the need for secrets (i.e. the contents of your hand) or unrevealed state (such as the ordering of cards within a deck).

This intuitively defines the environment with which Adama code operates, so we will show the architecture diagram here because this language requires an environment.

![Architecture diagram showing you, the data you see, how it connects to a store which runs the Adama code](/img/20200613_diagram_entire_thing_story.png)

We start this diagram with you. You will (via a client) connect to the Adama Document Store with a persisent connection. The store will then vend to you a private version of the document. This private version is tailored just for you as dictated from the rules within the Adama code provided to the store. Adama is not only a data-centric programming language, but a privacy focused language such that secrets between players (i.e. individual hands) and the universe (i.e. decks) are not leaked. This is important for games to have a level playing field such that no one has an advantage based on their hacking ability.

## Organizing the Chaos Induced by Globals

Having a giant pool of global fields will lead to chaos and copypasta, so we introduce records as a way combining fields around an entity.

```adama
record Card {
  public int suit;
  public int rank;
}

public Card a;
public Card b;
```

A record is a structure which collects named typed fields under a single type name. In the above example, the type **Card** is the combination of **suit** and **rank** integer fields. These types can then be used to create instances within the document of that type. The above document would then be backed by the following JSON:

```json
{
  "a":{"suit":0, "rank":0},
  "b":{"suit":0, "rank":0}
}
```

This is great for cleaning up patterns within the global document, but this is insufficient for non-trivial games. The next step is to introduce a collection of records. Adama provides the notion of a table, and the above record can be used to create a table named **deck**.

```adama
table<Card> deck;
```

But this begs a question: how do records flow into the deck? Adama has a notion of events which can be associated with imperative code which is evaluated on events occurring. This code can then manipulate the document.

One unique event within the lifetime of a document is the creation of the document, and this is done via a constructor (using the **@construct** keyword). This constructor can be used with an "ingestion" operator (**<-**) and some C style **for** loops.

```adama
@construct {
  for (int s = 0; s < 4; s++) {
  	for (int r = 0; r < 13; r++) {
  	  deck <- {suit:s, rank:r};
  	}
  }
}
```

The above code will construct the state of the document which represents a typical 52 playing card deck. It is important to note that viewers of the document will not see anything as tables are permanently private. Instead, queries against the table must open up the data to people (such as you or me). As an example. the following code will let everyone know the size of the deck.

```adama
public formula deck_size = deck.size();
```

This notion of a **formula** is where the influence of Excel comes into play as Adama is also a [reactive programming language](https://en.wikipedia.org/wiki/Reactive_programming). As the deck undergoes changes, the formulas which depend on that deck will be recomputed and updates are sent to viewers such as you as me.

This is why the connection from your device to the Adama Document Store uses a socket. The socket provides a way for the server to know the state of the client, and then minimize the compute overhead on the server. This enables small data changes to manifest in small compute changes which translate to less network usage. Less network usages translates to less client device compute to integrate, and this manifests less battery consumption. Board games can last for hours, so being mindful of client batteries is important.

A table is an exceptionally powerful tool, and having the table be a first class citizen within the language enables [language integrated query](https://en.wikipedia.org/wiki/Language_Integrated_Query). We can extend the **Card** **record** with a **client** type to indicate possession of the card.

```adama
record Card {
  public int suit;
  public int rank;
  public client owner;
}
```

This allows us to share how many cards are unassigned in the deck via a **formula**.

```adama
public formula deck_remaining = (iterate deck where owner == @no_one).size()
```

Here **@no_one** is a special value for the **client** type which indicates unassigned and is the default value. We can leverage a **bubble** to share a viewer's hand (if they are a player and not a random observer).

```adama
bubble<who> hand = iterate deck where owner == who;
```

The bubble is special type of **formula** which allows data to be computed based on who is viewing the document. This allows people to have a personalized view of the document such as being able to see their hand. As the **deck** and rows within the **deck** experience change, the formulas update automatically based on precise static analysis. These changes propagate to all viewers in a predictable way.

This all begs the question: how do changes manifest?

## Messages from you to document

Adama acts as a limited form of actor which can receive messages from clients. We can model a **message** similar to a **record**. For instance, we want to model a message to mean "I wish to draw $count cards" which we can model thusly:

```adama
message Draw {
  int count;
}
```

This **message** encodes the product intent, and we can associate code to that message via a **channel**.

```adama
channel draw_cards(client who, Draw d) {
  (iterate deck where owner == @no_one shuffle limit d.count).owner = who;
}
```

This channel will allow messages of type **Draw** to flow from the client to the code outlined above. In this case, the code will simply leverage an integrated query to find at most **$d.count** available random cards to associate to the sender.

Messages alone create a nice theoretical framework, but they may not be practical for games. This messaging works great for things like chat, but it offloads a great deal of burden to both the message handler and the client. For instance, in a game, when can someone draw cards? Can they draw cards at any time? Or during a specific game phase?

## Let the server take control!

This is where an incomplete **channel** comes into play. An incomplete channel is like a [promise](https://en.wikipedia.org/wiki/Futures_and_promises) which indicates that clients may provide a message of a specific type, but only when the document asks for them.

This is where we need to take a detour because we need a third party to broker the play between these players. That is, who is going to be asking the players for messages? This is where the document's [finite state machine](https://en.wikipedia.org/wiki/Finite-state_machine) comes into play. The document can be in exactly one state at any time, and states are represented via hashtags. For instance, ```#mylabel``` is a state machine label used to denote a potential state of the document.

We can associate code to a state machine label directly and set the document into that state via the **transition** keyword.

```adama
@construct { // this could also be a message sent after all players are ready
  transition #round;
}

#round {
  // code to run
}
```

In this example, the associated code attached to ```#round``` will run after the constructor has run and the document has been persisted. An important property of the state machines is that it defines an atomic boundary for both persisting to a durable store and when to share changes to the document.

Only the **transition** keyword can set the document's next state label to run. For instance, the following is an infinite state machine.

```adama
public int turn;

#round {
  turn++;
  transition #round;
}
```

The reason we took this detour is to have a third party be able to use the incomplete channel. For instance, the document somehow learns of two players within a game; these player's associated clients are stored within the document via:

```adama
private client player1;
private client player2;
```

Now, we can define an incomplete for the document to ask these players for cards.

```adama
channel how_many_cards : Draw;
```

This incomplete channel will accept message only from code via a **fetch** method on the channel. We can leverage the state machine code to ask players for the number of cards they wish to draw thusly:

```adama
#round {
  future<Draw> f1 = how_many_cards.fetch(player1);
  Draw d1 = f1.await();

  future<Draw> f2 = how_many_cards.fetch(player2);
  Draw d2 = f2.await();
}
```

This is a productivity win with respect to board games because it inverts the control model away from the client towards the server as synchronous code. This is the key to enforce rules in a coherent way and keep control of the implicit state machine formed as rules compound in complexity.

## Time to Reflect

This document took you on a tour of a few of the core ideas found within the Adama programming, and while this is not a comprehensive review it does address some of the novel aspects. The key is that you focus on the data at hand for a single game, and then outline all the ways the game state may change. The rules of the game can be written in a synchronous manner which mirrors how they are executed live with people.

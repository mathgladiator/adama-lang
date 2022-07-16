# Language Tour

Since the central way of interacting with documents held within a space is via the Adama language, let's take a tour.

This document is a lightweight tour of the core features and ideas that make Adama a somewhat novel [data-centric programming language](https://en.wikipedia.org/wiki/Data-centric_programming_language). It is important to remember that Adama is not a general purpose language. It’s for board games (and maybe more... much more).

## Defining State Layout

We start by defining global fields with default values. Laying out and structuring data is arguably the most important activity in building software, so it must be simple and convenient for developers to define their data. Here, we will define a document with just a name and score field:

```adama
public string name = "You";
private int score = 100;
```

This fairly minimal Adama code defines a document. The backend data for this script is a document represented via the following JSON:

```json
{"name":"You", "score":100}
```

However, a human viewer (such as yourself or myself) of the document will only see:

```json
{"name":"You"}
```

This is because the **score** field is defined with the **private** modifier. By modifying a field with **private**, only the code within the document can operate on the **score** field. This is useful, for example, to define secrets in a game. A key function in board games is the need for secrets (i.e. the contents of your hand) or an unrevealed state of objects such as the ordering of cards within a deck.

The following diagram visualizes the Adama environment and architecture:

![Architecture diagram showing you, the data you see, how it connects to a store which runs the Adama code](https://www.adama-platform.com/i/20200613-diagram-entire-thing-story.png)

Here is a brief overview of the Adama working environment:

- People connect (via a client) to the Adama Platform with a persistent connection.
- Adama will then send to you a private and personalized version of the document.
- People send messages to the document, and Adama will run code on the message to validate and change the document.
- Adama will send updates while respecting the privacy based on directives (e.g., the **private** modifier sets the **score** variable as private in the above example). 

Adama is not only a data-centric programming language, but a privacy-focused language such that secrets between players (i.e. individual hands) and the universe (i.e. decks) are not disclosed. This environment is essential for games requiring secrets so that other gamers do not gain an unfair advantage from "hacking" environment variables. 

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

A record is a structure that defines one or more named typed fields under a single type name. In the above example, the structure **Card** is the combination of **suit** and **rank** integer fields. These structures can then be used to create instances within the document of that type. The above code backend would have the following JSON:

```json
{
  "a":{"suit":0, "rank":0},
  "b":{"suit":0, "rank":0}
}
```

The above example is great for cleaning up patterns within the global document, but this is insufficient for non-trivial games. The next step is to introduce a collection of records. Adama provides the notion of a table, and the above record can be used to create a table named **deck**.

```adama
table<Card> deck;
```

But this begs the question: how do records flow into the deck? Adama uses events that can be associated with developer code which is evaluated when events trigger. This code can then manipulate the document.

One example of an event is the creation of the document. The event is created via a constructor (using the **@construct** identifier). This constructor can be used with an "ingestion" operator (**<-**) and some C style **for** loops. The following Adama code builds a table of Card records based on the JSON document structure:

```adama
@construct {
  for (int s = 0; s < 4; s++) {
    for (int r = 0; r < 13; r++) {
      deck <- {suit:s, rank:r};
    }
  }
}
```

The above code will construct the state of the document representing a typical deck of cards containing 52 cards, 4 suits and 13 cards per suit. Tables are always private in Adama, so viewers of the document will not see table structures. However, the data contained within the table will be viewable. Queries against the table expose selected data to people such as players. As an example, the following code will let everyone know the size of the deck:

```adama
public formula deck_size = deck.size();
```

The above **formula** variable represents Adama's [reactive programming language](https://en.wikipedia.org/wiki/Reactive_programming). As the deck undergoes changes during gameplay, the formula variables depending on that deck will be recomputed and updates will be sent to viewers such as players in the game. For efficiency, this is done once message processing stops.

Because Adama continually updates the state of document, the connection from your device to the Adama Document Store uses a socket. The socket provides a way for the server to know the state of the client, and then minimize the compute overhead on the server. This enables small data changes to manifest in small compute changes that translate to less network usage. Less network usage translates to less client device compute overhead, and this manifests into less battery consumption for the end-user. Board games can last for hours when they leverage Adama's reduction in battery power consumption.

A table is an exceptionally powerful tool, and Adama uses [language integrated query](https://en.wikipedia.org/wiki/Language_Integrated_Query) (LINQ) to query data. Using the Card structure, the following example adds a **client** type to the Card record to indicate possession of the card:

```adama
record Card {
  public int suit;
  public int rank;
  public client owner;
}
```

The above Card record allows us to share how many cards are unassigned in the deck via a **formula**. The code to do this is below:

```adama
public formula deck_remaining = (iterate deck where owner == @no_one).size()
```

Here **@no_one** is a special default value for the **client** type which indicates that cards are unassigned. We can leverage a **bubble** to share a viewer's hand (if they are a player and not a random observer).

```adama
bubble hand = iterate deck where owner == @who;
```

The bubble is special type of **formula** which allows data to be computed based on who is viewing the document. This allows people to have a personalized view of the document such as being able to see their hand. As the **deck** and rows within the **deck** experience change, the formulas update automatically based on precise static analysis. These changes propagate to all viewers in a predictable way.

## Messages from Devices to the Document

Changing the document is done via people sending messages to the document.

Adama acts as a message receiver of messages sent by the client. We can model a **message** similar to a **record**. For instance, we can design a message that says "I wish to draw $count cards" demonstrated below:

```adama
message Draw {
  int count;
}
```

This **message** encodes the product intent, and we can associate code to that message via a **channel**.

```adama
channel draw_cards(Draw d) {
  (iterate deck where owner == @no_one shuffle limit d.count).owner = @who;
}
```

This channel will allow messages of type **Draw** to flow from the client to the code outlined above. In this case, the code uses a LINQ query to find at most **d.count** available random cards to associate to the Draw message sender.

Messages alone create a nice theoretical framework, but they may not be practical for games. This messaging works great for things like chat, but it offloads a great deal of burden to both the message handler and the client. For instance, in a game, when can someone draw cards? Can they draw cards at any time? Or during a specific game phase?

## Let the Server Take Control!

To control message flow, Adama uses an incomplete **channel** identifier. An incomplete channel is like a [promise](https://en.wikipedia.org/wiki/Futures_and_promises) that indicates clients may provide a message of a specific type, but only when the document asks for it.

Adama uses a third party to broker the communication between players. That is, it determines who is asking players for messages. This is where the document's [finite state machine](https://en.wikipedia.org/wiki/Finite-state_machine) comes into play. The document can be in exactly one state at any time, and states are represented via hashtags. For instance, ```#mylabel``` is a state machine label used to denote a potential state of the document.

We can associate code to a state machine label directly and set the document to that state via the **transition** keyword.

```adama
@construct { // this could also be a message sent after all players are ready
  transition #round;
}

#round {
  // code to run
}
```

In this example, the associated code attached to ```#round``` will run after the constructor has run and the document has been persisted. An important property of the state machine is that it defines an atomic boundary for both persisting to a durable store and when to share changes to the document.

Only the **transition** keyword can set the document's next state label to run. For instance, the following is an infinite state machine:

```adama
public int turn;

#round {
  turn++;
  transition #round;
}
```

The reason we took this detour is to have a third party be able to use the incomplete channel. For instance, the document somehow learns of two players within a game; these players' associated clients are stored within the document via:

```adama
private client player1;
private client player2;
```

Now, we can define an incomplete channel for the document to ask players for cards.

```adama
channel<Draw> how_many_cards;
```

This incomplete channel will accept messages only from code via a **fetch** method on the channel. We can leverage the state machine code to ask players for the number of cards they wish to draw using the following Adama code:

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

# Deeper Tour

## Language basics

Adama is yet another language with braces in the tradition of C

```adama
/** comments are for friends */
procedure code() -> int {
  int x = 42;
  for (int k = 0; k < 10; k++) { x++; }
  while (x > 0) { x--; }
  var y = (x + 1) * x;
  if (x == y) { y++; } else { y--; }
  return y;
} 
```

## Strange things
Unlike other static languages with legacy behavior like integer division, Adama applies the principal of least surprise yet maximal correctness.

In math classes, we were taught to call out division of zero; Adama forces your hand by having division escape out the expected types.

```adama
procedure portion(int x, int y) -> double {
  maybe<double> m_norm = x / (x + y);
  if (m_norm as norm) {
    return norm;
  } else {
    return 0.0;
  }
}  
```

## Complex numbers
Similarly, Adama embraces complex numbers because all languages should.
Complex numbers are awesome!

```adama
function len(double x, double y) -> double {
  complex sqr = Math.sqrt(x * x + y * y);
  return sqr.re();
}
```

## Document structure
As a developer, you will create an Adama specification file which is just a text file that outlines the state of a document.

At the root level you have fields which can be single values or records.

```adama
public string name;
public int x;
public int y;
private int bank_balance;

record Pv { int x; int y; }
public Pv pvalue1;
public Pv pvalue2;
```

## Relational data and table queries

Records can also be kept in a table.

An interesting aspect of this is that this is the only way to create "new" data.
The language does not expose a memory model that developers have to think about as everything is acccessible from the root document.

Adama embraces the existing legacy model with table queries as this not only makes code more expressive, but it also provides the compiler the ability to optimize and have static query planning.
```adama
record Card {
  public int id;
  public int value;
  public principal owner;
  public int ordering;
}

table<Card> deck;

procedure shuffle() {
  int new_order = 0;
  (iterate deck shuffle).ordering = new_order++;
}

procedure deal(principal player, int count) {
  (iterate deck
   where owner == @no_one
   order by ordering asc
   limit count).owner = player;  
}
```

### Constructor

Once you have structured your data, you can populate a document at the time of construction.

```adama
@construct {
  for (int k = 0; k < 52; k++) {
    deck <- {value:k, owner: @no_one};
  }
  shuffle();
}
```

## Message Handling

Once constructed, message handling is one mechanism for documentation mutation.

Here, we provide a way for people to ask the document for some cards to be vended to them.

```adama
message DrawCard {
  int count;
}

channel draw_card(DrawCard dc) {
  (iterate deck 
   where owner == @no_one
   order by ordering asc
   limit dc.count).owner = @who;
}
```

## Reactivity

As data fills the document, you want to expose that to people; this is done reactively via formulas.

So, here, we express the count for the number of cards and a boolean indicating if there are any cards available.

These fields update when the deck update.

```adama
public formula cards_left =
  (iterate deck
   where owner == @no_one).size();

public formula cards_available = cards_left > 0;
```

## Privacy - policies

As you expose data to players, it's important to consider the privacy of that information.
This is vital if you want to maintain secrets.
Here, we have a custom use_policy on the super secret data field.
The policy is evaluated when that data is being vended to people.

Protecting fields is not enough as we also want to limit side channels, so we require the policy to even know that the object exists.

In terms of privacy, this is a robust system for ensuring that people can only see what they are allowed.

```adama
record R {
  public int id;
  private principal owner;
  
  // guard the field
  use_policy<see> int super_secret_data;

  // guard the existence of the entire record
  require see;

  policy see {
    return @who == owner;
  }
}

table<R> recs;
public formula all = iterate recs;
```

## Privacy - bubbles

The privacy policies provide a security model to eliminate information disclosure; however, it is not the most efficient way to handle many scenarios.

This is where the privacy bubble comes into play where fields can be reactively computed with the viewer.
These viewer dependent queries allow for efficiency in vending data.

```adama
bubble yours = iterate recs where owner == @who;

bubble hand = iterate deck where owner == @who;
```

## State machine &amp; time;

Another way to change the document is by using time via the state machine.
Each document has one state machine label to run at any given time.

Here, the document starts with the countdown variable set to 10 and every minute that passes will decrement the countdown variable.

```adama
int countdown;
@construct {
  countdown = 10;
  transition #bump in 60;
}
#bump {
  countdown--;
  if (countdown > 0) {
    transition #bump in 60;
  }
}
```

## Asynchronous dungeon master

We can invert the typical control from of message handing to message asking.
Here, imagine the document is a dungeon master that is asking players to answer questions.

In the below example, the document is asking the current player to make a move.

```adama
message Move { int piece; int x; int y; }
channel<Move> ask_move;
public principal current_player;

#play {
  let move = ask_move.fetch(current_player).await();
  // apply the move to the state...
  next_player();
  transition #play;
}
```

## Access control and presence

```adama
private principal owner;
public int active;
@construct {
  owner = @who;
}
@connected {
  if (@who == owner || @who.isAnonymous()) {
    active++;
    return true;
  }
  return false;
}
@disconnected {
  active--;
}
```
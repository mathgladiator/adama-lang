# Enumerations

Enumerations in Adama are simply ways of associating integers to names.

```adama
enum Suit { Hearts:1, Spades:2, Clubs:3, Diamonds:4 }
```

We can refer to a single value via **::** by

```adama
Suit x = Suit::Hearts;
```

## Enumeration collections

We can build an array of all the values within an enumeration using the * symbol. For example, we can build an array of all the suit types via:

```adama
Suit[] all = Suit::*;
```

which is a handy. We can also build an array of all enumeration values which share a prefix. For instance, given the enum

```adama
enum Role { CoreLeft, CoreRight, Normal };
```

then we can refer to the collection of all values that start with Core via

```adama
Role[] core = Role::Core*;
```

This is a handy way to build a strict taxonomy within an enumeration.

## Dispatch


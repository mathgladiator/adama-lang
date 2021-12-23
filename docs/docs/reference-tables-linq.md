---
id: reference-tables-linq
title: Tables & Language Integrated Query
---

## Intro

Given a record such as:

```adama
record Rec {
  public int id;
  public string name;
  public int age;
  public int score;
}
```

This record can be used with the table:

```adama
table<Rec> _records;
```

This table is a way of organizing information per given record type. In general, the table is a useful construct which enables many common operations found in data structures. The above record would create a table like:

| id | name | age | score |
| --- | --- | --- | --- |
| 1 | Joe | 45 | 1012 |
| 2 | Bryan | 49 | 423 |
| 3 | Jamie | 42 | 892 |
| 4 | Jordan | 52 | 7231 |

## Diving Into Details

A table in and of itself requires a toolkit to handle it, and we introduce a variant of SQL in the form a language integrated query (LINQ). It is a variant in many ways, and we will introduce the mechanics.

### Reactive lists
Lists of records can be filtered, ordered, sequenced, and limited via language integrated query (LINQ).

### iterate

First, the ```iterate``` keyword will convert the table&lt;Rec&gt; into a list&lt;Rec&gt;.

```adama
public formula all_records = iterate _records;
```

Now, by itself, it will list the records in their canonical ordering (by id). It is important to note that the list is lazily constructed up until the time that it is materialized by a consumer, and this enables some query optimizations to happen on the fly.

### where

We can suffix a LINQ expression with **where** to filter items.
```adama
public formula young_records = iterate _records where age < 18;
```

### indexing!

Yes, we can make things faster by indexing our tables. The ```index``` keyword within a record will indicate how tables should index the record.

```adama
public formula lucky_people = iterate _records where age == 42;
```

This will accelerate the performance of ```where``` expressions when expressions like ```age == 42``` are detected via analysis.

### shuffle
The canonical ordering by id is not great for card games, so we can randomize the order of the list. Now, this will materialize the list.
```adama
public formula random_people = iterate _records shuffle;
public formula random_young_people = iterate _records where age < 18 shuffle;
```

### order

Since the canonical ordering by id is the insertion/creation ordering, **order** allows you to reorder any list.

```adama
public formula people_by_age = iterate _records order by age asc;
```

### limit

```adama
public formula youngest_person = iterate _records order by age asc limit 1;
```

### Bulk Assignments

A novel aspect of a reactive list is bulk field assignment, and this allows us to do some nice things. Take the following definition of a Card table representing a deck of cards:

```adama
record Card {
  public int id;
  public int value;
  public client owner;
  public int ordering;
}

table<Card> deck;
```

We can shuffle the deck using ```shuffle``` and bulk assignment.
```adama
procedure shuffle() {
  int ordering = 0;
  (iterate deck shuffle).ordering = ordering++;
}
```

This assignment of ordering will memorize the results from shuffling. With a single statement, we can deal cards by assigning ownership.

```adama
procedure deal_cards(client who, int count) {
  (iterate deck             // look at the deck
    where owner == @no_one  // for each card that isn't own
    order by ordering asc   // follow the memoized ordering
    limit count             // deal only $count cards
    ).owner = who;          // for each card, assign an owner to the card
}
```

This ability makes it simple to update a single field, but it also applies to method invocation as well.

### Bulk method execution

### Bulk Deletes



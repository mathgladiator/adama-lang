# Records

A **record** is a collection of privacy-enforced typed data elements grouped under one name. For instance, we can define a Person record with the following code:

```adama
record Person {
  public string name;
  private int age;
  private double balance;
}
```

The data elements mirror how data is spelled out within [the root document](./document.md); however, a **record** can be used in multiple ways. For example, a record can be held within another record.

```adama
record Relationship {
  public Person a;
  public Person b;
}
```

This simple re-use becomes the foundation for building composite types and collections.

See:
* [types](./types.md) for more information on which types can go within records.
* [privacy policies](./privacy-and-bubbles.md) for how privacy per field is specified within a record.
* [bubbles](./privacy-and-bubbles.md) for how data is exposed based on the viewer
* [reactive formulas](./formulas.md) for how to expose reactively compute data to the viewer of record.

Records also have [#methods](#methods), can [declare privacy policies](#policies), and [hint at indexing](#indexing-tables) for [tables](./tables-linq.md).
If a record is used within a table, an implicit field called ```id``` is created with integer type.
Since communication is done with [messages](./messages.md), [conversion to a message](#easily-convert-to-a-message-for-communication) is provided as a free helper.

## Methods

We can associate code to a record via a method. For example, a method may mutate a record which is useful for consolidating how records change.

```adama
record R {
  public int score;
  
  method zero() {
    score = 0;
  }
}
```

Methods can be marked as read-only such that they are not allowed to mutate the document and thus become available for reactive formulas.

```adama
record R {
  public int score;
  
  method double_score() -> int readonly {
    return score * 2;
  }

  public formula ds = double_score();
}
```

## Policies

Records can express policies which are bits of code associated to the record along with ```@who```. 
```adama
record R {
  private client owner;
  
  policy is_owner {
    return owner == @who;
  }
  
}
```

A policy can be used to protect fields within a record.
```adama
record R {
  private client owner;
  
  use_policy<is_owner> int balance;
  
  policy is_owner {
    return owner == @who;
  }
  
}
```

Alternatively, a policy may be used to protect the entire record.

```adama
record R {
  private client owner;
  
  public int balance;

  policy is_owner {
    return owner == @who;
  }
 
  require is_owner;
}
```

## Indexing tables

The best mental model for a record is a row within a table. By default, a row has a primary key index on ```id``` which has a type of int.





## Easily convert to a message for communication

Given a message or record, we can convert it into a message type via the  ```@convert``` keyword.
```adama
record R {
  public int x;
}
R r;

message M {
  int x;
}

#sm {
 M m = @convert<M>(r);
}
```

The usefulness of this conversion will become clear when [channels and futures are outlined](./async.md).

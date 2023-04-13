# Records

To structure information for persistence, Adama uses a **record**, which is a collection of fields that represent a specific type of data.
These fields can be assigned values and then stored in the [document](./document.md), [a table](tables-linq.md), another record, or even [a map](./map-reduce.md).
Records can be customized with privacy rules and visibility modifiers to control access to the data they contain.

```adama
record Person {
  public string name;
  private int age;
  private double balance;
}
```

Adama's data elements reflect the structure of data in the [the root document](./document.md), but the record type offers additional flexibility. Records can be used in multiple ways, such as being nested within another record.

```adama
record Relationship {
  public Person a;
  public Person b;
}
```

By enabling records to be nested within each other, Adama establishes a foundation for constructing composite types and collections.
This straightforward re-use of records is fundamental to building more complex data structures.

See:
* [types](./types.md) for more information on which types can go within records.
* [privacy policies](./privacy-and-bubbles.md) for how privacy per field is specified within a record.
* [bubbles](./privacy-and-bubbles.md) for how data is exposed based on the viewer from the record.
* [reactive formulas](./formulas.md) for how to expose reactively compute data to the viewer of record.

In addition to being nestable and reusable, records in Adama come equipped with several useful features.
They can have [methods](#methods), can [declare privacy policies](#policies), and [hint at indexing](#indexing-tables) for [tables](./tables-linq.md).

If a record is used within a table, an implicit field called ```id``` is created with an integer type.

To facilitate communication via [messages](./messages.md), Adama provides a [free helper that easily converts a record to a message](#easily-convert-to-a-message-for-communication).

## Methods

We can associate code with a record via a method, allowing for increased functionality and flexibility.
For example, a method can be used to mutate a record, which can be helpful for consolidating how records change over time.
By providing a way to associate code with a record, Adama makes it possible to implement complex logic and functionality directly within the data structure itself.

For example, the following record, R, has a method to zero out the score.
```adama
record R {
  public int score;
  
  method zero() {
    score = 0;
  }
}
```

Methods can be marked as read-only.
This means that these methods are not permitted to mutate the document, which makes them available for use in reactive formulas.
By designating certain methods as read-only, Adama provides a way to ensure that these methods do not interfere with other parts of the data structure or cause unintended side effects.

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

Records can express policies that are bits of code associated with the record and identified by the ```@who``` keyword.
These policies specify the access permissions for the record and provide a way to restrict or control how the record is used or modified.
By associating policies with records, Adama enables the implementation of fine-grained security and access controls within the data structure itself.

```adama
record R {
  private principal owner;
  
  policy is_owner {
    return owner == @who;
  }
  
}
```

Policies can also be used to protect individual fields within a record.
By associating a policy with a specific field, access to that field can be restricted or controlled based on the user's permissions or other criteria.
This allows for a more granular level of control over data access and modification, providing enhanced security and flexibility for complex data structures.

```adama
record R {
  private principal owner;
  
  use_policy<is_owner> int balance;
  
  policy is_owner {
    return owner == @who;
  }
  
}
```

Policies can also be used to protect the entire record.
By associating a policy with the record as a whole, access to the record can be restricted or controlled based on the user's permissions or other criteria.
In this case, the visibility and existence of the record is determined by whether the policy returns true or false.

```adama
record R {
  private principal owner;
  
  public int balance;

  policy is_owner {
    return owner == @who;
  }
 
  require is_owner;
}
```

## Indexing tables


The best mental model for a record is that of a row within a table.
By default, each row has a primary key index on the id field, which has a type of int.
This makes it easy to reference and manipulate individual rows within a table, as well as to perform fast lookups and [queries](./tables-linq.md).

In addition to the primary key index, Adama also allows for additional fields within a record to be indexed.
This can be particularly useful for speeding up queries and searches within large data sets.
By indexing specific fields, Adama can quickly locate and retrieve the records that match a given set of criteria, which can be critical for performance in many real-world applications.

In this example, we introduce a secondary key and instruct the record to index it.
```adama
record R {
  private int key;
  
  index key;
}

table<R> _table;
```

In Adama, the ```index``` keyword can be used to inform a table that it can group records by a specific field, known as the ```key```, in order to reduce the number of candidates considered during a ```where``` clause.
By creating an index on the key field, Adama can more efficiently locate the records that match a given query, which can result in significant performance improvements.

## Easily convert to a message for communication

The ```@convert``` keyword can be used to convert a message or record into a message type.
This is a useful feature for communicating with external systems or other parts of an application that expect data to be formatted in a particular way.

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

The ability to convert records into a message using the ```@convert``` keyword can be incredibly useful in a variety of situations.
For example, when working with [channels and futures are outlined](./async.md), it may be necessary to present users with a list of options derived from a table, which can be accomplished by converting the table data to a specific message type.
Similarly, when sending records to [another service](./services.md) using Adama services, it may be necessary to convert the data to a specific format in order to ensure compatibility with the receiving system.

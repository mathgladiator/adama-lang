# Records and Messages

A data structure is a collection of data elements grouped under one name. These data elements, also known as member fields or just fields, can have different types. Adama has two types of data structures: **record** and **message**.

## Record

A **record** starts with the same rules as document variables, but wraps those variables within an additional layer of syntax. For instance, we can define a Person record with the following code:

```adama
record Person {
  public string name;
  private int age;
  private double balance;
}
```
And this record can be leveraged within the document:
```adama
public Person owner;
```
or within another record:
```adama
record Relationship {
  public Person a;
  public Person b;
}
```

Beyond fields, it is worth nothing that records can have [methods](/docs/reference-methods-on-records), [privacy policies](/docs/reference-privacy-and-bubbles), and [reactive formulas](/docs/reference-reactive-formulas)

## Message

A message is similar to a record except without any privacy awareness or privacy concerns. All fields within a message are public, and the expectation is that messages come from users. The following defines a real-world message:

```adama
message JoinGroup {
  string name;
  int age;
  bool ready;
}
```

## Diving Into Details: Records
The types that are allowed in records is limited to:
* ```bool```
* ```enum```
* ```int```
* ```long```
* ```double```
* ```string```
* ```label```
* maybe&lt;t&gt; for any type in this list (except maybe and maybe)
* table&lt;r&gt; where r is a record

The best mental model for a record is a row within a table. As a convention, every record has a hidden field called **id** with type ```int``` which can be revealed by defining it (without changing its type).

Records also have:
* [index definitions to make tables go faster](./tables-linq.md)
* [bubbles for viewer-dependent computation/view and privacy policies](./privacy-and-bubbles.md)

## Diving Into Details: Messages

Most types that can be defined within code can be defined within a message. The exceptions are channels and futures.

Messages can also be constructed anonymously on the fly.
```adama
#yo {
  let msg = {x:1, y:2};  
}
```

It is worth noting that the type of messages undergo static type rectification, so the above is 100% statically typed. It also leverages a simplified form of type inference such that messages of a known type can be constructed.
```adama
message M {
  int x;
  int y;
}

@construct (who) {
  M m = {x:1, y:1};
}
```

## Easy copying and type conversion (@convert)

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

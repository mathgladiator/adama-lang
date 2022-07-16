# Messages

A message is similar to a [record](./records.md) except without any privacy awareness or privacy concerns. Unlike records, messages lack formulas and bubbles and have a limited form of [methods](#methods).

All fields within a message are public, and the expectation is that messages come from users. The following defines a real-world message:

```adama
message JoinGroup {
  string name;
}
```

Most types that can be defined within code can be defined within a message.
Some exceptions are channels, services, and futures; spiritually, all data defined within a message must be complete in a serialized form.
Messages can also be constructed anonymously on the fly.

```adama
#yo {
  let msg = {x:1, y:2};  
}
```

It is worth noting that the type of messages undergo static type rectification, so the above is 100% statically typed.
It also leverages a simplified form of type inference such that messages of a known type can be constructed.

```adama
message M {
  int x;
  int y;
}

@construct {
  M m = {x:1, y:1};
}
```

## Methods

Messages may have methods, but they are more constrained as they can only reference data from within the message as messages.

```adama
message X {
  int val;
  method succ() -> int {
    return val + 1;
  }
}
```

## A collection of messages is a native table

Messages, like records, can be put into a table within code.

```adama
message M {
  int x;
  int y;
}

#turn {
  table<M> tbl;
  tbl <- {x:1, y:1};  
  tbl <- {x:1, y:2};  
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

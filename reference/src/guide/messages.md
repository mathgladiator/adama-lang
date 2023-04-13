# Messages

A message is essentially a simplified version of a record, lacking privacy awareness and privacy concerns, and without formulas or bubbles.
It provides a limited form of methods compared to records.
All fields within a message are public, making it suitable for use by users.
In contrast to records, which are designed to store data within the system, messages are intended to be used for communication with external services or between different parts of the system.

The following defines a real-world message:

```adama
message JoinGroup {
  string name;
}
```

Within a message, it is possible to define almost all types that can be defined within code, with the exception of channels, services, and futures.
It is important to note that all data defined within a message must be complete in a serialized form.
Messages can also be constructed anonymously on the fly, which allows for easy and efficient communication between different parts of the system.

```adama
#yo {
  let msg = {x:1, y:2};  
}
```


Messages undergoes static type rectification, which means that any type errors are detected at compile time rather than runtime.
This makes message handling safer and more reliable.
Additionally, Adama supports a simplified form of type inference, which means that messages of a known type can be constructed without having to explicitly declare their types.

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

In a message, methods can be defined, but they are more limited compared to records.
Methods within messages can only reference data from within the message as messages, and cannot reference data outside of the message, unlike methods in records which can reference data from the entire document.
This is because messages are designed to be used for communication and are often sent over the network, so they should be self-contained and not rely on external data.

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
They can also be used in other ways that records can, such as being used as parameters or return types for functions.
However, since messages lack privacy policies and formulas, they may not be suitable for all use cases where records are used.
When messages are put into a table, they are not indexed as there is no primary key.

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
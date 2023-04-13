# Document layout

At the heart of Adama is a [focus on data](https://en.wikipedia.org/wiki/Data-centric_programming_language), which makes organizing your data for state management a critical component of building applications with this language.
In Adama, state is organized at the document level as a series of fields, which represent different aspects of the state that your application needs to manage.
By carefully laying out these fields, you can create a robust and efficient system for managing your application's state.
For example, the Adama code below outlines three fields that might be used in an application:

```adama
public string output;
private double balance;
int count;
```

These three fields will establish a persistent document in JSON:
```json
{"output":"","balance":0.0,"count":0}
```

The [*public* and *private*](./privacy-and-bubbles.md) modifiers control what users will see, and the omission of either results in *private* by default.
In this case, users will see:
```json
{"output":""}
````
when they [connect to the document](./static-policies-document-events.md#connected-and-disconnected).
Adama's strong privacy isolation guarantees ensure a clear separation between what users can see and what the system sees, creating a gap that protects sensitive data from unauthorized access.

The language has [many types](./types.md) to leverage along with a [more ways to expressive privacy rules](./privacy-and-bubbles.md).

Furthermore, state can be laid out with [records](./records.md), collected into [tables](./tables-linq.md), and computations exposed via [formulas](./formulas.md).

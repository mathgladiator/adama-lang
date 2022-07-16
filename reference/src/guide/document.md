# Document layout

As Adama is [a data-centric programming language](https://en.wikipedia.org/wiki/Data-centric_programming_language), the core game to play is organizing your [data for state management](https://en.wikipedia.org/wiki/State_management). At the document level, state is laid out as a series of fields. For instance, the below Adama code outlines three fields:

```adama
public string output;
private double balance;
int count;
```

These three fields will establish a persistent document in JSON:
```json
{"output":"","balance":0.0,"count":0}
```

The [*public* and *private*](./privacy-and-bubbles.md) modifiers control what users will see, and the omission of either results in *private* by default. In this case, users will see:
```json
{"output":""}
````
when they [connect to the document](./static-policies-document-events.md#connected-and-disconnected).

The language has [many types](./types.md) to leverage along with a [more ways to expressive privacy rules](./privacy-and-bubbles.md).

Furthermore, state can be laid out with [records](./records.md), collected into [tables](./tables-linq.md), and computations exposed via [formulas](./formulas.md).

## Details

The syntax which Adama parses for this is as follows:
```regex
(privacy)? type name (= expression)?;
```

**such that**
* _privacy_ when set may be *private*, *public*, or anything outlined in [the privacy section](./privacy-and-bubbles.md). In this context, *private* means only the system and code within Adama can see the field while *public* means the system, the code, and any human viewer may see the field. [The privacy section](./privacy-and-bubbles.md) will outline other ways for humans to see the field. When _privacy_ is omitted results in a default value of *private* which means no users can see the field.
* _type_ is the type of the data. See [types for more details](./types.md).
* _expression_ when provided will be computed at the [construction](./static-policies-document-events.md) of the document.

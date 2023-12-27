# Types

Adama has many built-in primal types! The following tables outline which types are available.

| type                                       | contents | default          | fun example |
|--------------------------------------------| --- |------------------| --- |
| bool                                       | bool can have one of the two values *true* or *false*. | false            | true |
| int                                        | int is a signed [integer](https://en.wikipedia.org/wiki/Integer) number that uses 32-bits. This results in valid values between −2,147,483,648 and 2,147,483,647. | 0                | 42 |
| long                                       | long is a signed [integer](https://en.wikipedia.org/wiki/Integer) number that uses 64-bits. This results in valid values between -9,223,372,036,854,775,808 and +9,223,372,036,854,775,807.  | 0                | 42 |
| double                                     | double is a floating-point type which uses 64-bit IEEE754. This results in a range of 1.7E +/- 308 (15 digits). | 0.0              | 3.15 |
| [complex](./rich-types.md#complex-numbers) | complex is a tuple of two doubles under the [complex field of numbers](https://en.wikipedia.org/wiki/Complex_number) | 0 + 0 * @i       | @i |
| string                                     | string is a [utf-8](https://en.wikipedia.org/wiki/UTF-8) encoded collection of code-points. | "" (empty string) | "Hello World" |
| label                                      | label is a pointer to a block of code which is used by [the state machine](./state-machine.md), | # (the no-state) | #hello |
| principal                                  | principal is a reference to a connected person, and the backing data establishes who they are. This is used for [acquiring data and decisions from people](./async.md), | @no_one          | @no_one |
| [dynamic](./rich-types.md#dynamic)         | a blob of JSON | @null            | @null | 


## Call-out to other types

The above built-in types are building blocks for richer types, and the below table provides callouts to other type mechanisms. Not all types are valid at the document level.

| Type                            | Quick call out                                                                                                                                     | Applicable to document/record   |
|---------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------|
| [assets](./assets.md)           | An **asset** is an externally stored byte blob                                                                                                     | yes                             |
| [enum](./enumerations.md)       | An **enum**eration is a type that consists of a finite set of named constants.                                                                     | yes                             |
| [messages](./messages.md)       | A **message** is a collection of variables grouped under one name used for [communication via channels.](./async.md)                               | only via a formula              |
| [records](./records.md)         | A **record** is a collection of variables grouped under one name used for persistence.                                                             | yes                             |
| [maybe](./maybe.md)             | Sometimes things didn't or can't happen, and we use **maybe** to express that absence rather than null. Monads for the win!                        | yes (only for applicable types) |
| [table](./tables-linq.md)       | A **table** forms the ultimate collection enabling maps, lists, sets, and more. Tables use **record**s to persist information in a structured way. | yes                             |
| [channel](./async.md)           | Channels enable communication between the document and people via handlers and **future**s.                                                        | only root document              |
| [future](./async.md)            | A future is a result that will arrive in the **future**.                                                                                           | no                              |
| [maps](./map-reduce.md)         | A **map** enables associating keys to values, but they can also be the result of a reduction.                                                      | yes                             |
| [lists](./tables-linq.md)       | A **list** is created by using language integrated query on a table                                                                                | yes                             |
| [arrays](./anonymous.md#arrays) | An array is a read-only finite collection of a adjacent items                                                                                      | only via a formula              |
| [result](./services.md)         | A result is the ongoing progress of a service call made to a remote service                                                                        | only via a formula              |
| [service](./services.md)        | A service is a way to reach beyond the document to a remote resources                                                                              | only root document              |

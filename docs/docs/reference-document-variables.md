---
id: reference-document-variables
title: Document Variables & Types
---

## Fast Intro

As Adama is [a data-centric programming language](https://en.wikipedia.org/wiki/Data-centric_programming_language), the first order of business it organize your [data for state management](https://en.wikipedia.org/wiki/State_management). At the document level, state is laid out as a series of fields. For instance, the below Adama code outlines two fields:
```adama
public string output;
private double balance;
int count;
```

These three fields will establish a persistent document in JSON:
```js
{"output":"","balance":0.0,"count":0}
```

The *public* and *private* control what people will see, and the omission of either results in *private* by default. In this case, people will see
```js
{"output":""}
````
when they [connect to the document](/docs/reference-connection-events).

## Diving Into Details

The syntax which Adama parses for this is as follows:
```regex
(privacy)? type name (= expression)?;
```

**such that**
* _privacy_ when set may be *private*, *public*, or anything outlined in [the privacy section](/docs/reference-privacy-and-bubbles). In this context, *private* means only the system and code within Adama can see the field while *public* means the system, the code, and any human viewer may see the field. [The privacy section](/docs/reference-privacy-and-bubbles) will outline other ways for humans to see the field.
* _privacy_ when omitted results in a default value of *private* which means no humans can see the field.
* _type_ is the type of the data; see [types for more details](#types).
* _expression_ when provided will be computed at the [construction](/docs/reference-constructor) of the document.

The privacy policy of this document is limited and lacks data that people want to and should see. This is unfortunate, but can be corrected by changing the document to.
```adama
public string output = "Hello World"
private double balance = 13.42;
public int count = 42;
```

which results in the document persisted and viewed by all with slightly more meaningful data in JSON:
```js
{"output":"Hello World","count":42}
```

## Built-in Types
Adama has many built-in types, and the following tables outlines which types are available

| type | contents | default | fun example |
|  --- | --- | --- | --- |
| bool | bool can have one of the two values *true* or *false*. | false | true |
| int | int is a signed integral number that uses 32-bits. This results in valid values between −2,147,483,648 to 2,147,483,647 | 0 | 42 |
| double | double is a floating point type which uses 64-bit IEEE754. This results in a range of 1.7E +/- 308 (15 digits) | 0.0 | 3.15 |
| string | string is a [utf-8](https://en.wikipedia.org/wiki/UTF-8) encoded collection of code-points. | "" (empty string) | "Hello World" |
| label | label is a pointer to a block of code which is used by [the state machine](/docs/reference-state-machine) | # (the no-state) | #hello |
| client | client is a reference to a connected person, and the backing data establishes who they are. This is used for [acquiring data and decisions from people](/docs/reference-channels-handlers-futures) | @no_one | @no_one |

## Call-out to other types

The above built-in types are building blocks for richer types, and the below table provides call-outs to other type mechanisms.

| type | quick call out |
|  --- | --- | --- |
| [enum](/docs/reference-enumerations-dynamic-dispatch) | An **enum**eration is a type that consists of a finite set of named constants |
| [messages](/docs/reference-defining-structure-types) | A **message** is a collection of variables grouped under one name used for communication.  |
| [records](/docs/reference-defining-structure-types) | A **record** is a collection of variables grouped under one name used for persistence.  |
| [maybe](/docs/reference-maybe-types) | Sometimes things didn't or can't happen, and we use **maybe** to express that absense rather than null. Monads for the win! |
| [table](/docs/reference-tables) | A **table** form the ultimate collection enabling maps, lists, sets, and more. Tables use **record**s to persist information in a structured way. |
| [channel](/docs/reference-channels-handlers-futures) | Channels enable communication between the document and people via handlers and **future**s. |

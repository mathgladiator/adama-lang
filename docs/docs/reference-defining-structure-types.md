---
id: reference-defining-structure-types
title: Defining Record & Message Types
---

<h1><font color="red">Under Construction: Super Rough, Not Hardly Done</font></h1>

A data structure is a collection of data elements grouped under one name. These data elements, also known as member fields or just fields, can have different types. Adama has two types of data structures: **record**s and **message**s. 

## Fast Intro: Record

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

## Fast Intro: Message

A message is similar to a record except without any privacy awareness. All fields within a message are public, and the privacy option is unavailable. The following defines a reasonable message.
```adama
message SetName {
  string name;
}
```

Unlike records, messages can have only fields without any privacy awareness. The expectation is that messages flow freely from people to the document, or within the document.

## Diving Into Details: Records

## Diving Into Details: Messages

## Easy Structural Copying (@convert)

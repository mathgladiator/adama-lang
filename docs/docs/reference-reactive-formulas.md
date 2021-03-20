---
id: reference-reactive-formulas
title: Reactive Formulas
---

## Fast Intro

Adama is inspired by spreadsheets. Beyond capturing data in tables, spreadsheets have formulas to enable various useful forms of computation. This idea is a key part behind the reactive programming model in Adama. It's easy to define a formula, and here is an example:

```adama

public int x;
public int y;

public formula len = Math.sqrt(x * x + y * y);
```

## Diving Into Details

The ```formula``` identifier is used like a type but enables the right hand side of the '=' to be an expression combining any previously defined state (or other formulas) in a glorious mathematical expression. There are two key rules important to address:

* The right-hand side must be "pure" in a side-effect free kind of way. That is, it is unable to mutate the document. Practically, this means that only math and functions can be used. Procedures are strictly forbidden.
* The right-hand side must only refer to a state defined before the introduction of the formula. This prevents circular logic.

With regards to performance and semantics, formulas are 100% lazy. No computation is done until the data is asked for, and the result of the computation is cached until underlying data is invalidated. When data changes happen, the result will be thrown away until the next time the data is called upon.

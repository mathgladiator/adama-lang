---
id: reference-maybe-types
title: Maybe Types
---
<h1><font color="red">Under Construction: Super Rough, Not Hardly Done</font></h1>

## Fast Intro

Too many times, a value could not be found nor make sense to compute with the data at head. The lack of a value is something to contend with, and [failing to contend with it well has proven to be a billion dollar mistake](https://www.youtube.com/watch?v=ybrQvs4x0Ps). Adama uses [the maybe (or optional) pattern](https://en.wikipedia.org/wiki/Monad_(functional_programming)#An_example:\_Maybe).

```
public maybe<int> age;
```

## Philosophy

Maybe enforces better discipline by leveraging the type system to ask "Did you mean that?"


## As a poor-mans Either; thinking about non-recoverable problems.

@reason_timeout
@reason_zero_inputs


## Built-in Methods

A key goal of Adama was to limit the number of ways that failure can happen, but this is exceptionally hard in some situations.

.has()
.haltGet()
.delete()

## Details

The mechanism




There is a real problem that Optional tries to solve, and this article shows a better way to solve it. Therefore, you are better off using a regular possibly-null Java reference, rather than using Optional. 
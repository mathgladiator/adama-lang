---
id: reference-control-statements
title: Control Statements
---

<h1><font color="red">Under Construction: Super Rough, Not Hardly Done</font></h1>

## Fast Intro

Adama has many control and loop structures found within other C-like languages like:

* [if](#if)
* [while](#while)
* [do-while](#do-while)
* [for](#for)

And we introduce two non-traditional ones:
* [ifas](#ifas)
* [foreach](#foreach)

## Diving Into Details

### if

**if** statements are straightforward ways of controling the flow of execution, and Adama's **if** behave like most other languages.

```adama
public int x;

@construct {
   if (true) {
     x = 123;
   } else {
   	 x = 42;
   }
}
```

### if-as

Unlike most languages, there is a special extension to the if statement which is used for [maybe](/docs/reference-maybe-types). This allows safely extracting values out from the *maybe*.

```adama
int x;
@construct {
   maybe<int> m_value = 123;
   if (m_value as value) {
     x = value;
   }
}
```

### while

**while** statements are a straightforward way to iterate while a condition is true.

### do-while

### for

### foreach

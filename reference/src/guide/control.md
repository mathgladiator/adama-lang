# Standard control

Adama has many control and loop structures similar to C-like languages like:

* [if](#if)
* [while](#while)
* [do-while](#do-while)
* [for](#for)

And we introduce two non-traditional ones:
* [ifas](#if-as)
* [foreach](#foreach)

## Diving Into Details

### if

**if** statements are straightforward ways of controlling the flow of execution, and Adama's **if** behaves like most other languages.

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

Unlike most languages, Adama has a special extension to the if statement which is used for [maybe](/docs/reference-maybe-types). This allows safely extracting values out from the *maybe*.

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

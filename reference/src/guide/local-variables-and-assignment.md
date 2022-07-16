# Local variables and assignment

While [document variables](./document.md) are persisted, variables can be defined locally within code blocks (like [constructors](static-policies-document-events.md) to be used to compute things. These variables are only used to compute.

```adama
private int score;

@construct {  
  int temp = 123;
  temp = 42;
}
```

### Define by type

Native [types](./types.md) can be defined within code without a value as many of them have defaults:

```adama
#transition {
  int local;
  local = 42;
  string str = "hello";
}
```

The default values follow [the principle of least surprise](https://en.wikipedia.org/wiki/Principle_of_least_astonishment).

| type | default value |
| --- | --- |
| bool | false |
| int | 0 |
| long | 0L |
| double | 0.0 |
| string | "" |
| list&lt;T&gt; | empty list |
| table&lt;T&gt; | empty table |
| maybe&lt;T&gt; | unset maybe |
| T[] | empty array |

### readonly keyword

A local variable can be annotated as readonly, meaning it can not be assigned.

```adama
#transition {
  readonly int local = 42;
}
```

This is fairly verbose, so we introduce the **let** keyword.

### Define via the "let" keyword and type inference

Instead of leading with the **readonly** and the type, you can simply say "let" and allow the translator to precisely infer the type and mark the variable as readonly.

```adama
#transition {
  let local = 42;
}
```

This simplifies the code and the aesthetics.

### Math-based assignment, increment, decrement

Numerical types provide the ability to add, subtract, and multiply the value by a right-hand side. Please note: division and modulus are not available as there is the potential for division by zero, and division by zero is bad.

```adama
#transition {
  int x = 3; // 3
  x *= 4; // 12
  x--; // 11
  x += 10; // 21
  x *= 2; // 42, the cosmos are revealed
  x++;  
}
```

### List-based bulk assignment

Lists derived from [tables](./tables-linq.md) within the document provide a bulk assignment via '=' and the various math based assignments (+=, -=, *=, ++, --).

```adama
record R {
  int x;
}
table<R> _records;

procedure reset() {
  (iterate _records).x = 0;
}

procedure bump() {
  (iterate _records).x++;
}
```

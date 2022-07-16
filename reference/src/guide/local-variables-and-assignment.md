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

Each native type can be defined within a code block:

```adama
#transition {
  int local;
  local = 42;
  string str = "hello";
}
```

Many of the [types used by records and messages](./types.md) can used within code, and many of the types have a default value:

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

This is fairly verbose!

### Define via the "let" keyword and type inference

Instead of leading with the **readonly** and the type, you can simply say "let" and allow the translator to precisely infer the type and market the variable as readonly.

```adama
#transition {
  let local = 42;
}
```

### Math-based assignment, increment, decrement

Numerical types provide the ability to add, subtract, and multiply the value by a right hand side.
---
id: reference-local-variables-assignment
title: Local Variables & Assignment
---

## Fast Intro
While [document variables](/docs/reference-document-variables) are persisted, variables can be defined locally within code blocks (like [constructors](/docs/reference-constructor), TODO complete listing of where code is defined).

```adama
private int score;

@construct {  
  int temp = 123;
  temp = 42;
}
```

## Diving Into Details

### Define by type

Each native type can be defined within a code block:
```adama
#transition {
  int local;
  local = 42;
  string str = "hello";
}
```

Note, many types have a default value.

| type | default value |
| --- | --- |
| bool | false |
| int | 0 |
| long | 0L |
| double | 0.0 |
| string | "" |

TODO: what about lists, arrays, ... etc...

### Define via the "let" keyword and type inference

Instead of leading with the type, you can simply say "let" and allow the translator to precisely infer the type.

```adama
#transition {
  let local = 42;
}
```

### readonly keyword

A local variable can be annotated as readonly meaning it can not be assigned.

```adama
#transition {
  readonly int local = 42;
}
```

Note: readonly and let do not mix at this time... TODO

### Math-based Assignment

#### Add To (+=)

#### Subtract From (-=)

#### Multiply By (\*=)

#### Divide By (/=)

#### Mod (%=)


# Functions, procedures, and methods
Adama has three forms of colloquial functions: Functions, procedures, and methods.

Functions in Adama are pure in that they have no side effects and also are context-free. That is, the output of the function is 100% dependent on the inputs as [decreed by Mathematics](https://en.wikipedia.org/wiki/Function_(mathematics)).

```adama
function square(int x) -> int {
  return x * x;
}
```

On the other hand, there are procedures. A procedure can read state from outside of the function's scope. The reason for this distinction is for two reasons. First, the author has a Mathematics degree (and a chip on his shoulders) and feels the history of functions should be respected. Second, in a reactive environment, the important aspect is functions can't write state and thus cause non-determinism of a reactive read causing a write invalidating a reactive read in an infinite spiral of chaos.


```adama
int x;
procedure square_of_x() -> int {
  return x * x;
}
```

We can also mark a procedure as ```readonly``` since the ability to read state from outside a procedure is important for building complex results in [bubbles](./privacy-and-bubbles.md) and [formulas](./formulas.md)

```adama
int x;
procedure square_of_x() -> int readonly {
  return x * x;
}
public formula x2 = square_of_x();
```

The third form is a method which is a procedure attached to a record. Methods also support the ```readonly``` annotation.

```adama
record R {
  int x;
  int y;
  method foo() -> int readonly {
    return x + y;
  }
}
R z;
public formula z_foo = z.foo();
```


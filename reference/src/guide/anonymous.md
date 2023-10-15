# Anonymous messages and arrays

Adama is a programming language that allows messages and arrays to be constructed as literals, similar to how objects and arrays can be written in JavaScript.
This means that developers can write code more efficiently and succinctly, by directly specifying the values of messages and arrays in the code itself.

## Messages
Anonymous messages (or message literals) are a way to construct messages without explicitly defining a type beforehand.
To create an anonymous message in Adama, braces are used to indicate the beginning and end of an object, similar to how this is done in JavaScript/JSON.
For example, a simple message can be created using the following syntax:

```adama
@construct {
  let m = {cost:123, name:"Cake Ninja"};
}
```

Messages can be given a named type, and the type system uses a weak form of inference to convert anonymous messages to explicitly named message types.
This means that, when an anonymous message is created, the type system can automatically infer the type of the message based on its structure and the types of its fields.
By doing so, the message can be converted to an explicitly named type, which can help ensure that the message is properly typed and structured according to its intended use.

```adama
message M {
  int cost;
  string name;
}

@construct {
  M m = {cost:123, name:"Cake Ninja"};
}
```

This weak form of type inference is done via type rectification. 
Type rectification is the process of taking two values of different types and finding (or creating) a type that can hold both values.
This process is used to ensure that messages can be correctly processed, even when they have different structures or types.
For example, the rectified type of an int and a double is double, because double can hold both types of values.
Similarly, when rectifying messages with distinct fields, the resulting rectified type is a message that includes all of the original fields, but with their new types wrapped in maybes.

This approach allow static typed systems to compete with [duck typing](https://en.wikipedia.org/wiki/Duck_typing).

## Arrays

In Adama, anonymous arrays (or array literals) are supported and can be created using the brackets syntax, which is similar to JavaScript.
This approach provides a convenient way to create arrays without explicitly defining their type beforehand. For example, the following code creates an array:

```adama
@construct {
  let a = [1, 2, 3];
}
```

Anonymous arrays are statically typed, which means that the elements within them must have a compatible type under type rectification.
This ensures that the elements can be properly processed and compared within the array.
It's important to note that the process of type rectification is necessary in cases where the elements have different types.

A good example of this is the following code snippet, which creates an array with using different types of elements rectified into one common type.

```adama
@construct {
  let a = [{x:1}, {y:2}, {z:3}];
}
```

The common type generated is
```adama
message GeneratedType123 {
  maybe<int> x;
  maybe<int> y;
  maybe<int> z;
}
```

## Working with arrays

When you have an array like:

```adama
@construct {
  int[] a = [1, 2, 3];
}
```

The primary way of accessing a particular element in an array is through the index lookup operator (```[int]```).
However, the result of the index operator has a particular twist in Adama's type system, which aims to maximize safety.
Specifically, the index operator always returns a maybe type, which means that the result may or may not exist.
This approach helps to prevent errors and ensure that the code is always properly typed and safe.

For example, consider the following code snippet, which attempts to access the second element in an array:

```adama
@construct {
  int[] a = [1, 2, 3];
  maybe<int> second = a[1];
}
```

The result type is `maybe<int>` which requires a second inspection to determine if it exists, and this both forces the checking of range and contends with invalid ranges.
The only way to really know the value of the second index is via an if statement like so:

```adama
@construct {
  int[] a = [1, 2, 3];
  maybe<int> second = a[1];
  if (second as actual_second) {
    // ok, do whatever you want
  } else {
    // range failure
  }
}
```

While this approach may seem cumbersome at first, it ultimately helps to ensure that the code is properly typed and can handle cases where the requested element does not exist or is out of range.
By forcing the programmer to check for the existence of a value using an if statement, Adama helps to avoid unexpected runtime errors and promotes safer, more robust code.
While this approach may require more verbose code, it ultimately helps to prevent issues that could lead to difficult-to-debug errors.

### Iterating through arrays
An array should be interated via a `foreach` loop, see [Standard Control](./control.html#foreach). The size of an array size can be accessed via `.size()` to utilize a for loop.

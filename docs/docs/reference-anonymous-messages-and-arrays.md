---
id: reference-anonymous-messages-and-arrays
title: Anonymous Messages & Arrays
---

# Fast Intro: Anonymous Messages

Anonymous messages (or message literals) are a convenient way to construct messages without explicitly defining a type beforehand. The way to create an anonymous message in Adama is similar to JavaScript/JSON in that braces are used to indicate the beginning and end of an object. For instance, a simple message can be constructed using the following:

```adama
@construct {
  let m = {cost:123, name:"Cake Ninja"};
}
```

The following syntax is the only way to instantiate a message with a named type. For instance,

```adama
message M {
  int cost;
  string name;
}

@construct {
  M m = {cost:123, name:"Cake Ninja"};
}
```

This is done via type rectification.

# Fast Intro: Arrays

Adama supports anonymous arrays as well via the brackets similar in syntax to JavaScript. For instance, the following code produces an array:

```adama
@construct {
  let a = [1, 2, 3];
}
```

It is worth noting that these arrays are statically typed, and the elements within must have a compatible type under "type rectification". An interesting example for the need of type rectification is the following snippet.

```adama
@construct {
  let a = [{x:1}, {y:2}, {z:3}];
}
```

Here, the elements in the array have the same type, and the above is equal to:

```adama
@construct {
  let a = [{x:1,y:0,z:0}, {x:0,y:2,z:0}, {x:0,y:0,z:3}];
}
```

## Working with arrays

When you have an array like:

```adama
@construct {
  int[] a = [1, 2, 3];
}
```

The primary way of getting access to a particular element is via the index lookup operator (**[]**). The result of the index operator has a particular twist on the result type. For instance, the following code:

```adama
@construct {
  int[] a = [1, 2, 3];
  maybe<int> second = a[1];
}
```

The result type is **maybe** of whatever the element type is, and this both forces the checking of range and contends with invalid ranges. The only way to really know the **second** type is via an if statement like so:


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

While the above sample is trivial, this construct enforces the appropriate type and range to disallow bad things to go bump in the night and force an error when things are incorrect.

## What is "Type Rectification" and why should you care?

Type rectification is the process of taking two values of two types, then finding (or creating) a type which allows both of them to fit together. For instance, the rectified type of int and double is double because double can hold both values.


## TODO
* write about type rectification more
* outline the rules for how messages are joined under an array
* detail why this is important
* detail how this helps things become better

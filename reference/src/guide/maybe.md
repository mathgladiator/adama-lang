# Maybe some data, maybe not

Too many times, a value could not be found nor make sense to compute with the data at hand. The lack of a value is something to contend with, and [failing to contend with it well has proven to be a billion dollar mistake](https://www.youtube.com/watch?v=ybrQvs4x0Ps). Adama uses [the maybe (or optional) pattern](https://en.wikipedia.org/wiki/Monad_(functional_programming)#An_example:\_Maybe). For example, the following defines an age which may or may not be available:

```
public maybe<int> age;

// how to write
#sm1 {
 age = 40;
}

// how to read
#sm2 {
 if (age as a) {
  // so something with a if it exists
 } else {
  // age has no value, :(
 }
}
```

## Philosophy

The concept of ```maybe<>``` enforces better coding-discipline by leveraging the type system as a forcing function to prevent bad things from happening. [Segmentation fault](https://en.wikipedia.org/wiki/Segmentation_fault), [NullPointerException](https://en.wikibooks.org/wiki/Java_Programming/Preventing_NullPointerException), and index out of range exceptions are avoided entirely. Within Adama, a failure feels catastrophic as a failure signals the end of the game. This is a core motivation why Adama is a closed ecosystem (i.e. no disk or networking) such that the failures are limited to logic bugs or division by zero (and the jury is out as to whether or not division should result in a ```maybe<double>``` or not)

## Using maybes

Data can always freely enter a maybe using regular assignment.

```adama
maybe<int> key;

#sm {
  key = 123;
}

```

To safely retrieve data, the safe way is using an ```if ... as``` statement.

```adama
maybe<int> key;

#sm {
  if (key as k) {
  	// yay, I have the value
  } else {
  	// nay, I don't have a value
  }
}
```

## Maybe expressions

An instance of a maybe with a given type can be generated on the fly via ```maybe<Type>```. Example:
```adama
#sm {
	let key = @maybe<int>;
}
```

And an instance of a maybe with a given value can be generated via ```maybe(Expr)```. Example:
```adama
#sm {
	let key = @maybe(123);
}
```

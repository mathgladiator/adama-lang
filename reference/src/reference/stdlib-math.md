# Math

The math library adds methods to the primitive data types of int, double, long, and complex.

For example, instead of

```adama
  public int x;
  formula a_x = Math.abs(x);
```

developers can instead use

```adama
  public int x;
  formula a_x = x.abs();
```

Also, many math functions also work on [maybe types](/guide/maybe.md) since some mathematical operators may be undefined. Operating on maybe types, while inefficient, allows for expressive compute.

## Type: int

| Method | Description | Result type |
| --- | --- | --- |
| abs() | Returns the absolute value of the given integer. | int |

## Type: long
| Method | Description | Result type |
| --- | --- | --- |
| abs() | Returns the absolute value of the given long. | long |

## Type: double, maybe&lt;double&gt;

| Method | Description | Result type |
| --- | --- | --- |
| abs() | Returns the absolute value of the given double. | double |
| sqrt() | Returns the square root | complex |
| ceil() | - | double |
| floor() | - | double |
| ceil(double precision) | - | double |
| floor(double precision) | - | double |
| round() | - | double |
| round(double precision) | - | double |
| roundTo(int digits) | - | double |

## Type: complex, maybe&lt;compex&gt;
| Method | Description | Result type |
| --- | --- | --- |
| conj() | Returns the [complex conjugate](https://en.wikipedia.org/wiki/Complex_conjugate). | complex |
| length() | Returns the length of the complex number per the pythagorean theorem.  | double |

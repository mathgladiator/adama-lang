---
id: reference-built-in-math-expressions
title: Built-in Math Expressions
---

Adama lets you do math, and that's awesome! It has the typical operations that you would expect, and then some fun twists. So, let's get into it.

## Operators

### Parentheses: ( *expr* )

You can wrap any expression with parentheses, and parentheses will alter the precedence of evaluation. See [operator precedence section](#operator-precedence) for details about operator precedence.

**Sample Code**
```adama
public int z;
@construct {
  z = (1 + 2) * 3;
}
```

**Result**
```js
  {"z":6}
```

**Typing:** The resulting type is the type of the sub-expression.

### Unary numeric negation: - *expr*

When you want want to turn a positive into a negative, a negative into a positive, a smile into a frown, a frown upside down, or [reflect a value over the y-axis.](https://en.wikipedia.org/wiki/Cartesian_coordinate_system). This is done with the subtract symbol prefixing any expression.

**Sample Code**
```adama
public int z;
public formula neg_z = -z;
@construct {
  z = 42;
}
```

**Result**
```js
{"z":42,"neg_z":-42}
```

**Typing:** The sub-expression type must be an int, long, or double. The resulting type is the type of the sub-expression.


### Unary boolean [negation / not / logical compliment](https://en.wikipedia.org/wiki/Negation): ! *expr*

Turn false into true, and true into false. This is the power of the unary **Not** operator using the exclamation point **!**. Money back if it doesn't invert those boolean values!

**Sample Code**
```adama
public bool a;
public formula not_a = !a;
@construct {
  a = true;
}
```

**Result**
```js
{"a":true,"not_a":false}
```

**Typing:** The sub-expression type must be a bool, and the resulting type is also bool.

### Addition: *expr* + *expr*

You'll need to use addition to count the phat stacks of money you'll earn with a computer science degree.

**Sample Code**
```adama
public int a;
public formula b = a + 10;
public formula c = b + 100;
@construct {
  a = 1;
}
```

**Result**
```js
{"a":1,"b":11,"c":111}
```

**Typing:**  The addition operator commonly is used to add two numbers, but it can also be used with strings and lists. The following table summarizes the typing and behavior.

| left type | right type | result type | behavior |
| --- | --- | --- | --- |
| int | int | int | integer addition |
| double | double | double | floating point addition |
| double | int | double | floating point addition |
| int | double | double | floating point addition |
| long | long | long | integer addition |
| long | int | long | integer addition |
| int | long | long | integer addition |
| string | string | string | concatenation |
| int | string | string | concatenation |
| long | string | string | concatenation |
| double | string | string | concatenation |
| bool | string | string | concatenation |
| string | int | string | concatenation |
| string | long | string | concatenation |
| string | double | string | concatenation |
| string | bool | string | concatenation |
| list&lt;int&gt; | int | list&lt;int&gt; | integer addition on each element |
| list&lt;int&gt; | long | list&lt;long&gt; | integer addition on each element |
| list&lt;int&gt; | double | list&lt;double&gt; | floating point addition on each element |
| list&lt;long&gt; | int | list&lt;int&gt; | integer addition on each element |
| list&lt;long&gt; | long | list&lt;long&gt; | integer addition on each element |
| list&lt;double&gt; | int | list&lt;double&gt; | floating point addition on each element |
| list&lt;double&gt; | double | list&lt;double&gt; | floating point addition on each element |
| list&lt;string&gt; | int | list&lt;string&gt; | concatenation on each element |
| list&lt;string&gt; | long | list&lt;string&gt; | concatenation on each element |
| list&lt;string&gt; | double | list&lt;string&gt; | concatenation on each element |
| list&lt;string&gt; | bool | list&lt;string&gt; | concatenation on each element |

### Subtraction: *expr* - *expr*

You'll need the subtract operator to remove taxes from your phat stack of dolla-bills.

**Sample Code**
```adama
public int a;
public formula b = a - 10;
public formula c = b - 100;
@construct {
  a = 1000;
}
```

**Result**
```js
{"a":1000,"b":990,"c":890}
```

**Typing:**  

The subtraction operator commonly is used to subtract a number from another number, but it can also be used with lists. The following table summarizes the typing and behavior.

| left type | right type | result type | behavior |
| --- | --- | --- | --- |
| int | int | int | integer subtraction |
| int | double | double | floating point subtraction |
| double | double | double | floating point subtraction |
| double | int | double | floating point subtraction |
| long | int | int | integer subtraction |
| long | long | long | integer subtraction |
| int | long | int | integer subtraction |
| list&lt;int&gt; | int | list&lt;int&gt; | integer subtraction on each element |
| list&lt;int&gt; | long | list&lt;long&gt; | integer subtraction on each element |
| list&lt;int&gt; | double | list&lt;double&gt; | floating point subtraction on each element |
| list&lt;long&gt; | int | list&lt;int&gt; | integer subtraction on each element |
| list&lt;long&gt; | long | list&lt;long&gt; | integer subtraction on each element |
| list&lt;double&gt; | int | list&lt;double&gt; | floating point subtraction on each element |
| list&lt;double&gt; | double | list&lt;double&gt; | floating point subtraction on each element |

### Multiplication: *expr* * *expr*

TODO: something pithy about multiplication.

**Sample Code**
```adama
public int a;
public formula b = a * 2;
public formula c = b * 3;
@construct {
  a = 7;
}
```

**Result**
```js
{"a":7,"b":14,"c":42}
```

**Typing:** TODO
| int | int | int | integer subtraction |
| int | double | double | floating point subtraction |
| double | double | double | floating point subtraction |
| double | int | double | floating point subtraction |
| long | int | int | integer subtraction |
| long | long | long | integer subtraction |
| int | long | int | integer subtraction |

### Division: *expr* / *expr*

TODO: something pithy about division.

**Sample Code**
```adama
public double a;
public formula b = a / 2;
public formula c = b / 10;
@construct {
  a = 20;
}
```

**Result**
```js
{"a":20.0,"b":10.0,"c":1.0}
```

**Typing:** TODO


### Modulus: *expr* % *expr*

TODO: something pithy about Modulus and remainders.

**Sample Code**
```adama
public int a;
public formula b = a % 2;
public formula c = a % 4;
@construct {
  a = 7;
}
```

**Result**
```js
{"a":7,"b":1,"c":3}
```

**Typing:** The left and right side must be integral (i.e. int or long), and the result is integral as well. The following table summarizes the logic precisely.

| left type | right type | result type
| --- | --- | --- |
| int | int | int |
| long | int | int |
| int | long | int |
| long | long | long |


### Less than: *expr* < *expr*

TODO: something pithy

**Sample Code**
```adama
public int a;
public int b;
public formula cmp1 = a < b;
public formula cmp2 = b < a;
@construct {
  a = 1;
  b = 2;
}
```

**Result**
```js
{"a":1,"b":2,"cmp1":true,"cmp2":false}
```

**Typing:** The left and right must be comparable, and the resulting type is always bool. The following table outlines comparability

| left type | right type |
| --- | --- |
| int | int |
| int | long |
| int | double |
| long | int |
| long | long |
| long | double |
| double | int |
| double | long |
| double | double |
| string | string |

TODO: do maybes play into this?

### Greater than: *expr* > *expr*

TODO: something pithy

**Sample Code**
```adama
public int a;
public int b;
public formula cmp1 = a > b;
public formula cmp2 = b > a;
@construct {
  a = 1;
  b = 2;
}
```

**Result**
```js
{"a":1,"b":2,"cmp1":false,"cmp2":true}
```

**Typing:** This has the same typing as &lt;


### Less than or equal to: *expr* <= *expr*

TODO: something pithy

**Sample Code**
```adama
public int a;
public int b;
public formula cmp1 = a <= b;
public formula cmp2 = b <= a;
public formula cmp3 = a + 1 <= b;
@construct {
  a = 1;
  b = 2;
}
```

**Result**
```js
{"a":1,"b":2,"cmp1":true,"cmp2":false,"cmp3":true}
```

**Typing:** This has the same typing as &lt;

### Greater than or equal to: *expr* >= *expr*

TODO: something pithy

**Sample Code**
```adama
public int a;
public int b;
public formula cmp1 = a >= b;
public formula cmp2 = b >= a;
public formula cmp3 = a + 1 >= b;
@construct {
  a = 1;
  b = 2;
}
```

**Typing:** This has the same typing as &lt;

**Result**
```js
{"a":1,"b":2,"cmp1":false,"cmp2":true,"cmp3":true}
```

### Equality: *expr* == *expr*
TODO: something pithy

**Sample Code**
```adama
public int a;
public int b;
public formula eq1 = a == b;
public formula eq2 = a + 1 == b;
@construct {
  a = 1;
  b = 2;
}
```

**Result**
```js
{"a":1,"b":2,"eq1":false,"eq2":true}
```

**Typing:** If a left and right type are comparable (see table under  &lt), then the types can be tested for equality. The resulting type is a bool. Beyond comparable left and right types, the following table

### Inequality: *expr* != *expr*
TODO: something pithy

**Sample Code**
```adama
public int a;
public int b;
public formula neq1 = a != b;
public formula neq2 = a + 1 != b;
@construct {
  a = 1;
  b = 2;
}
```

**Result**
```js
{"a":1,"b":2,"neq1":true,"neq2":false}
```

**Typing:** This has the same typing as =;

### Logical and: *expr* && *expr*
TODO: something pithy

**Truth Table**

| left | right | result |
| --- | --- | --- |
| false | false | false |
| true | false | false |
| false | true | false |
| true | true | true |

**Sample Code**
```adama
public bool a;
public bool b;
public formula and = a && b;
@construct {
  a = true;
  b = false;  
}
```

**Result**
```js
{"a":true,"b":false,"and":false}
```

**Typing:** The left and right expressions must have a type of bool, and the resulting type is bool.

### Logical or: *expr* || *expr*
TODO: something pithy

**Truth Table**

| left | right | result |
| --- | --- | --- |
| false | false | false |
| true | false | true |
| false | true | true |
| true | true | true |

**Sample Code**
```adama
public bool a;
public bool b;
public formula or = a || b;
@construct {
  a = true;
  b = false;  
}
```

**Result**
```js
{"a":true,"b":false,"or":true}
```

**Typing:** The left and right expressions must have a type of bool, and the resulting type is bool.

### Conditional / Ternary: *expr* ? *expr* : *expr*

**Sample Code**
```adama
public bool cond;
public formula inline = a ? 5 : 10;
@construct {
  cond = true;
}
```

**Result**
```js
{"cond":true,"inline":5}
```

**Typing:** The first expression must have a type of bool, and the second and third type must be the compatible under type rectification. The result is the rectified type. For more information about type rectification, see [anonymous messages and arrays](/docs/reference-anonymous-messages-and-arrays).

## Operator precedence 

When multiple operators operate on different operands (i.e. things), the order of operators (or operator precedence) is used to break ambiguities such that the final result is deterministic. This avoids confusion, and operators with a higher level must evaluate first.

When there are multiple operators at the same level, then an associativity rule indicates how to evaluate those operations. Typically, this is left to right, but some operators may not be associative or be right to left.

| level | operator(s) | description | associativity |
| --- | --- | --- | --- |
| 11 | _expr_._ident <br /> _expr_[_expr_] <br/> _exprF_\(_expr0_,...,_exprN_\) <br /> \(_expr_\) | field dereference <br/> index lookup <br/> functional application <br/> parentheses | left to right |
| 10 | _expr_++ <br /> _expr_-- | post-increment <br/> post-decrement| not associative |
| 9 | ++_expr_ <br/> --_expr_ | pre-increment <br/> pre-decrement| not associative |
| 8 | -_expr_ <br /> !_expr_ | unary negation | not associative |
| 7 | _expr_\*_expr_ <br /> _expr_/_expr_ <br /> _expr_%_expr_ | multiply <br/> divide <br/> modulo | left to right |
| 6 | _expr_+_expr_ <br/> _expr_-_expr_ | addition <br /> subtraction | left to right |
| 5 | _expr_&lt;_expr_ <br /> _expr_&gt;_expr_ <br /> _expr_&lt;=_expr_ <br /> _expr_&gt;=_expr_ | less than <br /> greater than <br/> less than or equal to <br/> greater than or equal to | not associative |
| 4 | _expr_==_expr_ <br /> _expr_!=_expr_ | equality <br /> inequality | not associative |
| 3 | _expr_&&_expr_ | logical and | left to right |
| 2 | _expr_\|\|_expr_ | logical or | left to right |
| 1 | _expr_?_expr_:_expr | inline conditional / ternary | right to left |

# Reactive formulas

Adama draws inspiration from spreadsheets and their ability to do more than just organize data into tables.
By utilizing formulas, spreadsheets can perform useful computations, a concept that forms the basis of Adama's reactive programming model.

One of Adama's defining features is its simplicity in defining formulas, as demonstrated in the following example:

```adama
public int x;
public int y;

public formula len = Math.sqrt(x * x + y * y);
```

The ```formula``` identifier in Adama serves as a type that allows expressions to be combined into mathematical expressions using any previously defined state or other formulas.
However, there are two key rules to keep in mind.

Firstly, the right-hand side of the formula must be "pure," meaning it cannot mutate the document in any way.
This restriction limits the use of only math and functions, while procedures are strictly prohibited.
Secondly, the right-hand side must only refer to a state defined before the introduction of the formula, which effectively prevents circular logic.

In terms of performance and semantics, formulas in Adama are 100% lazy. 
No computation is executed until the data is requested, and the result is cached until the underlying data is invalidated.
If changes occur in the data, the result is discarded until the next time the data is accessed.
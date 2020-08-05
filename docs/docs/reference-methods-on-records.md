---
id: reference-methods-on-records
title: Methods on Records
---

[Records](/docs/reference-defining-structure-types) can have methods which allow code to run within the context of a record.

```adama
record Point {
  public double x;
  public double y;

  method lensqr() -> double {
  	return x * x + y * y;
  }
}
```


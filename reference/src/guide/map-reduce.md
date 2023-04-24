# Maps and reduce

## Explicit maps
Adama supports maps from integral and string types to other types.

```adama
record Point {
  int x;
  int y;
}

table<Point> pointdb;
map<string, Point> named_points;
map<int, Point> index_points;
```


## Maps via reduction

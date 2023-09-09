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

Many times, we need to group things in a table by a common property.
Here, this is done via the reduce function.

```adama
enum Breed { Lamancha:1, Boer:2, Numbian:3, Pygmy:4, Alpine:5 }
record Goat {
  Breed breed;
}
table<Goat> goats;
public formula grouped_by_breed = iterate goats reduce breed via (@lambda x: x);
```


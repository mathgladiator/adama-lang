---
id: reference-tables-linq
title: Tables & Language Integrated Query
---

## Intro

Given a record like;

```adama
record Rec {
  public int id;
  public string name;
  public int age;
  public int score;
}
```

This record can be used with a table thusly:

```adama
table<Rec> _records;
```

This table is a way of organizing information per given record type. In general, the table is an exceptionally useful construct which enables many common operations found in data structures. The above record would create a table like

| id | name | age | score |
| --- | --- | --- | --- |
| 1 | Joe | 45 | 1012 |
| 2 | Bryan | 49 | 423 |
| 3 | Jamie | 42 | 892 |
| 4 | Jordan | 52 | 7231 |

## Diving Into Details

A table in and of itself requires a toolkit to handle it, and we introduce a variant of SQL in the form a language integrated query. It is a variant in many ways, and we will introduce the mechanics.

### iterate

First, the ```iterate``` keyword will lazily convert the table&lt;Rec&gt; into a list&lt;Rec&gt;.

```adama
public formula all_records = iterate _records;
```

Now, by itself, it will list the records in their canonical ordering (by id). It is important to note that the list is lazily constructed up until the time that it is materialized by a consumer, and this enables some query optimizations to happen on the fly.

### where

We can suffix an expression with **where** to filter items 

### shuffle

### order

### limit

### Bulk Assignments

### Bulk Deletes


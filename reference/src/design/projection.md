# Projection Map

## Problem Statement

For developers, it is very convenient to create tables with many fields.
However, fields have different mutation and read load which creates an imbalance since record changes propigate reactively as an atomic unit.
That is, if you have a profile picture or name intermixed with a presence/last-activity flag, then invalidations will cause all profile picture/names lookups to happen excessively.
The problem here is to provide developers an easy tool to cache and reduce the reactive pressure from fast-moving changes invalidating slow-moving changes.

## Ultimately Solution
The ultimate solution would be to increase the memory demands on subscription and augment with some notion of which fields a thing depends on.
However, this is exceptionally difficult with static analysis reactivity.
The next version of the reactive system should rely entirely on runtime analysis which can exploit [bloom filters](https://brilliant.org/wiki/bloom-filter/) which has a new set of trade-offs.

## Candidate Solution 1

Here, we are going to introduce a "projection map" which allows slow moving data to be replicated out of a table into a map.
We will assume the domain is a integer for the row id, so we propose this syntax for a new reactive type

```adama
record R {
  public int id;
  public string name;
  public datetime last_activity;
}

table<R> tbl;

message Rslow {
  string name;
}

function r_to_rslow (R row) -> Rslow {
  return { name: row.name }
}

projection<tbl via r_to_rslow> tbl_slow;
```

### Rules
* Private by default; since this is a conversion from a record to a message, we will not expose this to users by default to prevent an information leak scenario.
* Readonly by default; since this is the output image of a table and a function, the readonly keyword isn't needed nor welcome.
* The given table (i.e. tbl) must exist and have a record type compatible with the domain of the given function (i.e. r_to_rslow)
* The given function must exist and be pure (i.e. using 'function')
* The resulting type is then a map of integers to the range of the function

### Invalidation Flow

The trick conversation is the invalidation flow as it is tricky. Ultimately, the spirit of the design is nice but we need to see how we actually save compute/reactive-pressure.

#### Hash as a Shield

The first path requires hashing to shield downward changes.
Here, all primary key changes from a table to the projection map be unfiltered (i.e. duplicate signals) such that each table change results in evaluating the result.
This result is then cached and hash, and if the hash changes, then invalidate associated subscriptions for the primary key.
The big cost here is the unfiltered subscription flow combined with re-executions and then a hash (which currently uses sha384).
The hashing alone makes this path not ideal, and the only plus is that compute would be minimal while the cached was used and invalidated.

#### New Settle Phase
Currently, settlement is a single pass which means invalidations generated during a settlement are not captured.
Relying on the settlement phase would be unfortunate as the downchain consumers wouldn't be invalidated during execution (which almost suggests the need for a "soft invalidation" phase).
That is, in the reactive chain of table --> map -> formula, if the invalidation signal stops at map until settlement, then using the formula is going to be a problem

### OK, maybe the design is bad.

The introduction of the function requires a lot of work, so what if we simplify and use the existing reactivity all together!

## Candidate Solution

Here, the projection map concept from one is going to leverage existing reactivity and table invalidations to shield down chain invalidation.

```adama
record R {
  public int id;
  public string name;
  public string photo;
  public datetime last_activity;
  private formula simple = {name:name, photo:photo};
}

table<R> tbl;

projection<tbl.simple> tbl_slow;
```

### Rules
* Private by default; since this is a conversion from a record to a message, we will not expose this to users by default to prevent an information leak scenario.
* Readonly by default; since this is the output image of a table and a function, the readonly keyword isn't needed nor welcome.
* The given table (i.e. tbl) must exist and have a record type compatible with the domain of the given function (i.e. r_to_rslow)
* The field (i.e. simple) must exist within the record

This new design is nicer in that it has less stuff and is more powerful (as it can leverage readonly procedures), so let's look at the important aspect!

### Invalidation Flow

When the projection map learns of a row changes, it will simply provide a proxy of the id to the given field (which may be a formula). 
Here is where we can leverage the reactivity of the field to service us as we have to contend with three types of events:
* when a record is created -> send invalidation
* when a record is deleted -> send invalidation
* when the field is changed -> send invalidation

In the case of the simple field, the last_activity will only invalidate that the record changes which does nothing to name which then in terms does nothing to simple.
So, simple will only change when name or photo changes. The map here is a way to skip the entire record changing allowing read/invalidation pressure to only be on the map.




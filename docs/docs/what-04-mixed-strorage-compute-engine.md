---
id: what-mixed-compute-with-storage
title: Mixed Compute and Storage Engine
---

Doing stuff based on [time](what-the-living-document), reacting to [messages](what-actors-are-actings), and [coordinating human behavior](what-workflow-dungeon-master-as-a-service) brings compute to the table in a fun and almost complete way. Previously, we have hinted at the ability to have global fields within the **living document**, but we need more. We need containers of data, and the best container of data ever is: the table.

In Praise of Databases
----------------------

The way a table works is you first define a record

```adama
record MyRecord {
  public string name;
}
```

and then define a table:

```adama
table<MyRecord> my_records;
```

Tables are not directly exported to people, and instead require a formula to yield data. We can do that via the iterate expression

```adama
public formula records_by_name = iterate my_records order by name asc;
```

We leverage the messaging abilities to *ingest* to the table. The "<-" operator ingests data into tables and free form records.

```adama
message AddRecord {
	string name;
}

channel my_channel(client who, AddRecord msg) {
  my_records <- msg;
}
```

TODO:
* talk about more stuff

Mental Model: Tiny Personal Databases
-------------------------------------
TODO
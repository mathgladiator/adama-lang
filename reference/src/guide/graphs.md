# Graph Indexing

A common pattern within Adama is nesting tables within records which themselves are in a table, and pattern trade space for reduced document size and reactivity pressure. Consider the following specification

```adama

record User {
  public int id;
  public string name;
  public principal account;
}
table<User> _users;

record Member {
  int user_id;
}

record Group {
  public int id;
  public string name;
  table<Member> _members;
}

table<Group> _groups;
```

The problem with the above specification is that if you want to find all the groups for a specific user, then you have to iterate over every group.
The normal way to do this within an RDBMS is to have a table relating users to groups, but this introduces replication of the associated group_id and the blast radius of reactive flux increases.
Instead, Adama supports graph indexing via association maps which are a many-to-many index.

## Using Assoc

In the above example, we can retrofit an index between User and Group via the **assoc** keyword.

```adama
assoc<User,Group> _users_to_groups;
```

At this moment, it is empty, so we populate it by having the _members table join in

```adama
record Group {
  public int id;
  public string name;
  table<Member> _members;
  
  join _users_to_groups via _members[x] from x.user_id to id;
}
```

This now binds the group's record (and lifetime) with the elements within the table.
The table now drives a partial aspect of the graph such that as members come and go, the graph will update.
This creates an index of users to the associate group id, and we can use this via the **traverse** keyworld.

```adama
bubble my_groups = iterate _users where account == @who traverse _users_to_groups; 
```

This transforms the list&lt;User&gt; into a list&lt;Group&gt; where the user is the viewer and the group was joined to that user via the assoc table.

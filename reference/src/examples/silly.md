# A Silly Example to Show Off

```adama
// 1/6: Layout state with a schema
public principal owner;
public string name;
public string description;
public int viewers;

record AddOn {
  public string name;
  public string description;
}

// tables within documents!
table<AddOn> _addons;

// 2/6: create documents with constructors
@static {
  // policy: who can create? anyone!
  create { return true; }
}

message Arg {
  string name;
  string description;
}

@construct(Arg arg) {
  owner = @who;
  name = arg.name;
  description = arg.description;
}

// 3/6: people connect with WebSocket
@connected {
  viewers++;
  return true;
}

@disconnected {
  viewers--;
}

// 4/6: people manipulate document via messages
message AddAddOn {
  string name;
  string description;
}

channel create_new_add_on(AddAddOn arg) {
  // tables can "ingest" data easily
  _addons <- arg;
}

// 5/6: deletes are handled within document (for safety!)
message Nothing {
}

channel delete(Nothing arg) {
  if (owner == @who) {
    Document.destroy();
  }
}

// 6/6: reactive formulas (just like a spreadsheet)
public formula addons = iterate _addons order by name asc;
public formula addons_size = addons.size();
public formula name_uppercase = name.upper();

```
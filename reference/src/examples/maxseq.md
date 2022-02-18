# Maximum Number

```adama

@static {
  create(who) { return true; }
}

@connected (who) {
  return true;
}

public int max_db_seq = 0;

message NotifyWrite {
  int db_seq;
}

channel notify(client who, NotifyWrite message) {
  if (message.db_seq > max_db_seq) {
    max_db_seq = message.db_seq;
  }
}

```
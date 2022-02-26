# Writing and deploying Adama code to a space

With the space created, we can now deploy some code to it! Create a file called **chat.adama**

```adama
@static {
  // anyone can create
  create(who) { return true; }
  invent(who) { return true; }
}

// let anyone into the document
@connected (who) {
  return true;
}

// the lines of chat
record Line {
  public client who;
  public string what;
}

// the chat table
table<Line> _chat;

// how someone communicates to the document
message Say {
  string what;
}

// the "channel" which enables someone to say something
channel say(client who, Say what) {
  // ingest the line into the chat
  _chat <- {who:who, what:what.what};
}

// emit the data out
public formula chat = iterate _chat;
```


```shell
java -jar adama.jar space create --space chat001
```

```shell
java -jar adama.jar spaces deploy --space chat001 --file chat.adama
```


```shell
java -jar adama.jar document connect --space chat001 --key 0
```
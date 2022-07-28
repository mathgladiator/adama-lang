# Writing and deploying Adama code to a space

With the space created, we can now deploy some code to it! Create a file called **chat.adama**

The first thing we need to do is some ceremony around who can create documents.

```adama
@static {
  // only allow users from the authority we just created
  create {
    return @who.fromAuthority("Z2YISR3YMJN29XZ2");
  }

  // Here "Inventing" is the act of creating the
  // document on demand with connect with no need 
  // for a create(...) call
  invent {
    return @who.fromAuthority("Z2YISR3YMJN29XZ2");
  }
}
```

Please note that when you copy-pasta this, the **Z2YISR3YMJN29XZ2** will need to be replaced with the authority used to generate your initial users from the prior step.

Now, empty documents can be created with the above policy work.
Unfortunately, there is no data and no one can connect.
Let's enable connections

```adama
// let anyone into the document
@connected {
  return @who.fromAuthority("Z2YISR3YMJN29XZ2");
}
```

Now, people within the developer's authority can connect to the sadly empty document.
So, let's add some data.

```adama
// `who said `what `when
record Line {
  public principal who;
  public string what;
  public long when;
}

// a table will privately store messages
table<Line> _chat;

// since we want all connected parties to
// see everything, just reactively expose it
public formula chat = iterate _chat;
```

The document has structure, so let's enable users to populate the chat.

```adama
// what users will say stored in a message
message Say {
  string what;
}

// the "channel" enables someone to send a message
// bound to some code
channel say(Say what) {
  // ingest the line into the chat
  _chat <- {who:@who, what:what.what, when: Time.now()};
  
  // since you are paying for the chat, let's cap the 
  // size to 50 total messages.
  (iterate _chat order by when desc offset 50).delete();
}
```

At this point, the backend for chat is done and we can upload it via:

```shell
java -jar adama.jar spaces deploy --space chat001 --file chat.adama
```

With the space uploaded, you can now build a [UI with only HTML.](05-js-client.md)
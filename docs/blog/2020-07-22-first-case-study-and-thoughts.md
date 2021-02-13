---
slug: first-case-study-and-thoughts
title: First Case Study (Chat) & Open Thoughts
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, ui]
---

The language is in a high functioning state, and it is validating the vision that I have as a madman lunatic. Today, I would like to jump ahead into the future and share the vision for how to build products with the platform that I envision. This is a useful exercise as I am in the process of defining the service beyond the series of hacks that got my prototype working. More importantly, I want to share this vision with you, and I’d also like to quickly contrast this to the past.

## User Story: Chat Room
Let us get started with a concrete use-case: a chat room. The story of this use-case is that you want to create a chat room with your friends, or you want to extend your website with a chat room feature of sorts. The key is that you want people chatting on a website. There are 3 steps to achieve this story.

## Step 1: Write the entire infrastructure in Adama

We need some infrastructure (i.e. servers) to provide the connective glue between people that use unreliable devices (they go to sleep, go through tunnels, must manage power, etc...). In this new universe, the first step is to write code within Adama, and so we must justify the reasoning for learning yet another programming language. The reasoning starts with why we write schemas or use interface description languages like [thrift](https://en.wikipedia.org/wiki/Apache_Thrift): it is a good practice to lay out the state in a formal and rigorous way. Adama is no different, so we must layout our state. The following code will define the shape of table.

```adama
// the lines of chat
record Line {
  public client who;
  public string what;
}

// the chat table
table<Line> _chat;
```

Intuitively, the above defines a ledger of chat lines within the chat room. At this point, Adama escapes the confines of just laying out state into the manipulation of state. People that connect to this chat room are able to send messages to it, and we can outline a message thusly:

```
// how someone communicates to the document
message Say {
  string what;
}
```

This representation outlines what a single person can contribute to a conversation, and we can handle that message with a channel handler. A channel handler is a procedure which is available only to consumers of the document (i.e. the people in the chat) that are connected.

```
// the "channel" which enables someone to say something
channel speak(client who, Say what) {
  // ingest the line into the chat, this is a strongly typed INSERT
  _chat <- {who:who, what:what.what};
}
```

The above will combine the ```who``` (which is the authenticated sender) with the message ```what``` into an item within the table using the ingestion operator ("<-"). The "<-" operator is how data is inserted into tables.

What this means is that people can come together around the outlined data and contribute, but how do they read the data? Well, we expose data reactively via formulas. In this case, we exploit language integrated queries such that every update to the table will reactively update all people connected.

```adama
// emit the data out
public formula chat = iterate _chat;
```

This will expose a field ```chat``` to all consumers containing a list of all chat items. Note: the ```iterate _chat``` expression is shorthand for the SQL ```SELECT * FROM _chat```. Every time the ```_chat``` table changes, the ```chat``` field will be recomputed. Now, this begs a question of how expensive this is for devices, and the answer is not at all expensive because we leverage a socket such that clients can leverage prior state to incorporate changes from the server. That is, if someone sends a message "Hello Human", then every client will get a change that looks like this on the wire:

```js
{"chat":
  {
    "44":{"who":{"agent":"jeffrey","authority":"me"},"what":"Hello Human"},
    "@o":[{"@r":[0,43]},"44"]
  }
}
````

This will be discussed in further detail in a future post around "Calculus". At this point, this trifecta of (1) laying out state, (2) ingesting state from people, and (3) reactively exposing state in real-time to people via formulas is sufficient to build a wide array of products. The chat infrastructure is done!

## Step 2: Upload Script to the Goat Cloud

The above Adama script is called “chat_room.a”, and the developer can spin up their chat room infrastructure via the handy goat tool.

```bash
./goat upload --gamespace chatrooms --file chat_room.a
```

Here, the "gamespace" term is a play on "namespace", but it is a globally unique identifier to identify and isolate the space of all instances of a "chat_room.a" experience. It is worth noting that the script defines a class of chatrooms, and there are an infinite number of chatrooms available. Once this script is uploaded, the gamespace enables UIs to create a chatroom and connect into a chatroom.

## Step 3: Build the UI

Disclaimer: This is not a lesson about how to build pretty UIs as the UI is very ugly. This is about the way the UI is populated from the server. So, with much shame, the UI looks like this:

![the way the chat UI looks](/img/20200722-the-chat-ui.png)

And it has the expected behavior that:
* First person clicks "Create a New Room",
* First person shares that "Room ID" with friends (somehow)
* First person clicks "Connect"
* Other people paste the id into the "Room ID" box, then they click "Connect".
* Everyone connected can then chat by typing in the last box and hit "Speak".

We will walk through how the **Adama JavaScript Client Library** enables these behaviors and fulfills the expectations. First, here is the HTML for that ugly UI:

```html
<html>
<head>
  <title>Chat</title>
  <script type="text/javascript" src="adama.js"></script>
</head>
<body>
  <button id="create_new_room">Create a New Room</button>
  <hr />
  Room ID: <input type="text" id="chat_id" />
  <button id="connect_to_room">Connect</button>
  <hr />
  <pre id="chat"></pre>
  <hr />
  <input type="text" id="say" />
  <button id="speak">Speak</button>
</body>
</html>
```

With this skeleton, let's make it do stuff. First, let's connect this document to the devkit which is running locally.

```js
  var adama = new AdamaClient("localhost", 8080);
  // some auth stuff ignored for now
  adama.connect();
```

This will establish a connection to the server, but now we need to make the "create_new_room" button work. Ultimately, we are going to let the server decide the ID such that it is globally unique.

```js
  document.getElementById("create_new_room").onclick = async function() {
    document.getElementById("chat_id").value =
      await adama.createAndGenerateId("chat.a");
  };
```

With this, an id will pop into the text box. Now, we need to make the "connect_to_room" room button work, and this button should populate the "chat" ```<pre>``` element. Since this is going to result in a stream of document change, we need a way to accumulate those changes in a coherent way. This is where the ```AdamaTree``` comes into play.

```js
// first, we create a tree to receive updates
var tree = new AdamaTree();
```

This tree can receive updates from the server and hold a most recent copy of the document, so we can use this to attach events to learn of specific updates to the tree. Here, we will subscribe to when the "chat" field changes within the document. We will then construct the HTML for the "chat" element.

```js
// second we outline how changes on the tree manifest
tree.onTreeChange({chat: function(change) {
  // tree.chat has changed, so let's recompute the "chat" element's innerHTML
  var chat = change.after;
  var html = [];
  for (var k = 0; k < chat.length; k++) {
    html.push(chat[k].who.agent + ":" + chat[k].what);
  }
  // 
  document.getElementById("chat").innerHTML = html.join("\n");
}});
```
This tree now needs to be connected to a specific document, and this needs to happen when the "connect_to_room" button is clicked. So, we will do just that.

```js
// the button was clicked
document.getElementById("connect_to_room").onclick = function() {
  adama.connectTree("chat.a", document.getElementById("chat_id").value, tree);
};
```

This illustrate a core design pattern. Namely, you outline how changes within the tree manifest in changes in UI. The above shows how to update the UI when the "chat" field changes, but since the "chat" field is a list it may be prudent to update the UI based on specific list changes (i.e. append item, reordered, inserts, etc...). These specific updates are possible, but they will be saved for a later example as they introduce more DOM complexity. For now, let's focus on what happens when the "speak" button gets clicked.

```js
document.getElementById("speak").onclick = function() {
  var msg = {what:document.getElementById("say").value};
  adama.send("chat.a", document.getElementById("chat_id").value, "speak", msg);
};
```

This will send the ```msg``` to the document via _speak_ channel handler. That handler will insert data, and this will invalidate the chat field which gets recomputed. This recomputation will manifest a change for all connected people, and each person person will get a delta changing their copy of the chat field. This delta will trigger the above onTreeChange which will render the message.

This completes the UI, and it completes the usecase story.

## A Time to Reflect

At core, these three steps demonstrate how to create a working product which brings people together. This is just one demo of the future platform-as-a-service, and I am working on a few more. The key takeaway, I hope, is that every step is minimal and intuitive.

I am beginning to realize that the language is a red herring of sorts in terms of marketing. While the Adama language is the keystone for building back-ends which connect people together, the key is the platform and how it works to enable people to build. Put another way, it would be more prudent to talk about it as a real-time document store or database rather than a programming language. However, it feels like something new, and new stuff is hard to market.

It is very interesting to be in a state of seeing and believing in something, but it makes sense when I look back. Personally, I've been developing web properties for over twenty years, and I look at AWS as an inspiring enabler of doing more with less. However, a pattern is emerging where if you look at what it takes to build a web property change over time, then the following emerges.

![less is more](/img/20200722-less-is-more-funnel.png)

In a sense, things are getting better on many dimensions, and the key is that our progress as a species depends on a persistence to make things better by enabling more with less. I'm a bit biased, but there is something here. I'm excited to wrestle with it.


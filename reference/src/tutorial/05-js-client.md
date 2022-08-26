# Using the JavaScript client

The stage is set! Let's use some [vanilla.js](http://vanilla-js.com/) to craft a new browser experience with Adama powering the back-end.
Note, this example is derived from the [chat example available from github.](https://github.com/mathgladiator/adama-lang/tree/master/examples/vanilla-chat).

```html
<!DOCTYPE html>
  <html>
    <head>
      <title>Adama Vanilla.JS Chat</title>
      <script src="https://aws-us-east-2.adama-platform.com/libadama.js"></script>
    </head>
  <body>
  <div id="status"></div>
  <table border="0">
    <tr>
      <td colspan="2" id="setup">
        <fieldset>
          <legend>Inputs: Space, Key, and Identities</legend>
          <label for="space">Space</label>
          <input type="text" id="space" name="space" value="chat000" size="100"/>
          <br />
          <label for="key">Key</label>
          <input type="text" id="key" name="key" value="room-as-key" size="100"/>
          <br />
          <label for="identity-user-1">User 1</label>
          <input type="text" id="identity-user-1" name="identity-user-1" size="100"/>
          <br />
          <label for="identity-user-2">User 2</label>
          <input type="text" id="identity-user-2" name="identity-user-2" size="100"/>
          <br />
          <button id="connect">Connect both users</button>
          </fieldset>
        </td>
    </tr>
    <tr>
      <td>
        <fieldset>
          <legend>Chat Log (User 1)</legend>
          <div id="chat-output-1"></div>
          <label for="speak-user-1">User 1 Says What</label>
          <input type="text" id="speak-user-1" size="25"/>
          <br />
          <button id="send-1">Speak</button>
        </fieldset>
      </td>
      <td>
        <fieldset>
          <legend>Chat Log (User 2)</legend>
          <div id="chat-output-2"></div>
          <label for="speak-user-2">User 2 Says What</label>
          <input type="text" id="speak-user-2" size="25"/>
          <br />
          <button id="send-2">Speak</button>
        </fieldset>
      </td>
    </tr>
  </table>
</body>
  <script>
     // INSERT CODE BELOW HERE
  </script>
</html>
```

For your own personal sake, it would be useful to replace chat000 with whatever name you choose for a space.
This mess of old-school HTML is a skeleton to demonstrate the basics, so let's connect to Adama.


```javascript
// connect to Adama
var connection = new Adama.Connection(Adama.Production);
connection.start();

// wait until we are connected
document.getElementById("status").innerHTML = "Connecting to production...";
connection.wait_connected().then(function() {
  document.getElementById("status").innerHTML = "Connected!!!";
});
```

Before we can make the connect button work, we will create a handler for dealing with document deltas.
At core, the below code bridges how data from Adama flows into the DOM.

```javascript
// write chat changes to the DOM
function makeBoundTree(outputId) {
  var tree = new AdamaTree();
  tree.subscribe({chat: function(chat) {
      var lines = [];
      lines.push("<table border=\"1\"><thead><tr><th>Who</th><th>Said</th></tr></thead><tbody>");
      for (var k = 0; k < chat.length; k++) {
        lines.push("<tr><td>" + chat[k].who.agent + "</td><td>" + chat[k].what + "</td></tr>");
      }
      lines.push("</tbody></table>");
      document.getElementById(outputId).innerHTML = lines.join("");
    }});
  return {
    next: function(payload) {
      if ('delta' in payload) {
        var delta = payload.delta;
        if ('data' in delta) {
          tree.update(delta.data);
        }
      }
    },
    complete: function() {
      document.getElementById(outputId).innerHTML = "chat completed";
    },
    failure: function(reason) {
      document.getElementById(outputId).innerHTML = "Failed: " + reason;
    }
  };
}

// log send errors to console.log
function failureToConsoleLog(prefix) {
  return {
    success: function() {},
    failure: function(reason) {
      console.log(prefix + reason);
    }
  };
}
```

The above code will simply manifest changes of the following [chat formula from chat.adama](04-space.md) into the DOM.

```adama
public formula chat = iterate _chat;
```

It does this by creating a tree which will absorb data differentials and rebuild the chat lines.
Now we make the connect button work!

```javascript
document.getElementById("connect").onclick = function() {
  // fetch the input values
  var space = document.getElementById('space').value;
  var key = document.getElementById('key').value;
  var identity1 = document.getElementById('identity-user-1').value;
  var identity2 = document.getElementById('identity-user-2').value;

  // create the connections to the document and bind them to the DOM
  var connection1 = connection.ConnectionCreate(
    identity1, space, key, {}, makeBoundTree('chat-output-1'));
  var connection2 = connection.ConnectionCreate(
    identity2, space, key, {}, makeBoundTree('chat-output-2'));

  // hook up the buttons to send messages to the say channel per user
  document.getElementById("send-1").onclick = function() {
    connection1.send("say",
      {what:document.getElementById("speak-user-1").value}, failureToConsoleLog("user-1 send:"));
  }
  document.getElementById("send-2").onclick = function() {
    connection2.send("say",
      {what:document.getElementById("speak-user-2").value}, failureToConsoleLog("user-2 send:"));
  }

  // remove the setup html
  document.getElementById("setup").innerHTML = "";
}
```

This will connect each user's identity to appropriate window and make the buttons work.
Here, we can observe that reactivity is no longer a client concern.
Instead, we have very simple JavaScript with data bound to a tree.

At this point, the tutorial is over which is sad. However, there are [examples](/examples/start/md) to inspect.
Given the early release nature of this, questions and feedback are welcomed!

The best place for help is to [join the discord channel!](https://discord.gg/W3Cj4By)



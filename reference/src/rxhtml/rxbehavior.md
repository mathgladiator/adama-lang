# rx:behavior

One escape hatch available to create great user experiences is the attribute rx:behavior which will run some custom javascript once the associated element is created.
This is the most limited escape hatch since it scopes the customization down to just having access to (1) the associated DOM node, (2) the connection, (3) static config, (4) and the RxHTML framework.
It's worth noting that the spirit behind rx:behavior is what is used to extend RxHTML, and this is made available for custom code.

## Start with custom.js

Somehow, you will link a custom.js or whatcodeyouwant.js into the project via the &lt;script&gt tag within the &lt;shell&gt, and you can access rxhtml via window.rxhtml.
As an example, we are going to build a simple and generic drag and drop system and evaluate how to make it the canonical default way to do drag and drop in RxHTML.

The core game starts by defining the behavior
```js
window.rxhtml.defineBehavior('dnd', function (el, connection, config, $) {
  // do fun things here
});
```

This enables elements
```html
  <div rx:behavior="dnd">Drag And Drop Me Bro</div>
```

Ideally, we want a drag and drop system and we work backwards from what needs to happen to invoke a change: send a message.
Sending a message ultimately requires information from the element being dragged and the element being dropped on.
Thus, dragging thing X to thing Y will generate a message taking information from both X and Y and merging them together.
With this, we will annotate an element that is draggable with the element with data using attribute like **drag:data:$field** or and similarly for an element that is droppable **drop:data:$field**.

We will leverage the default **draggable** attribute to indicate that an element opts-in to being dragged, and we will introduce a new attribute **drop:channel** to indicate an element opts-in to being dropped upon and which channel to send the associate message to.

Given the composition of various drag and drop elements within the same page, we will also create a simple match making system such that the thing being dragged can even be dropped on the element with droppable.
We will introduce a bit vector on both draggable and droppable elements under the attributes **drag:types** and **drop:types**. The value is a list of fields seperated by comma which we split on, and if the intersection of the bit vectors is true then we allow the drop.

Putting these requirements together, we come up with two helpers: (1) scan an element to produce a spec, (2) intersect two bit vectors.

```js
var scanElementIntoSpec = function (type, spec, el) {
  var dataPrefix = type + ":data:";
  spec.data = {};
  spec.types = { basic: true };
  for (const attr of el.attributes) {
    if (attr.name.startsWith(dataPrefix)) {
      spec.data[attr.name.substring(dataPrefix.length)] = attr.value;
    }
    if (attr.name == type + ":types") {
      spec.types = {};
      var types = attr.value.split(",");
      for (var k = 0; k < types.length; k++) {
        spec.types[types[k]] = true;
      }
    }
  }
};
var intersectTypes = function (a, b) {
  for (t in a) {
    if (b[t]) {
      return true;
    }
  }
  for (t in b) {
    if (a[t]) {
      return true;
    }
  }
  return false;
};
```

Now, we leverage this in the defineBehavior call by sketching out the event structure
```js

window.rxhtml.defineBehavior('dnd', function (el, connection, config, $) {
    if (el.draggable === true) {
        var drag = {};
        el.addEventListener("dragstart", function (e) {
            // START
        });
        el.addEventListener("dragend", function (e) {
            // STOP
        });
    }
    if ('drop:channel' in el.attributes) {
        var drop = {};
        drop.channel = el.attributes['drop:channel'].value;
        el.addEventListener("dragenter", function (e) {
            // ENTER
        });
        el.addEventListener("dragover", function (e) {
            // OVER
        });
        el.addEventListener("dragleave", function (e) {
            // LEAVE
        });
        el.addEventListener("drop", function (e) {
            // DROP
        });
    }
});
```

At this point, we have all the tools to enable this element
```html
<div class="cursor-move"
    rx:behavior="dnd"
    draggable="true" 
    drag:data:x="1"
    drop:channel="some_adama_channel"
    drop:data:y="0">
    Thing A to Drag or Drop onto
</div>
```

to drop onto this element
```html
<div class="cursor-move"
    rx:behavior="dnd"
    draggable="true"
    drag:data:x="2"
    drop:channel="some_adama_channel"
    drop:data:y="20">
    Thing B to Drag or Drop onto
</div>
```

### START

When the browser detects an element should be dragged, it will invoke dragstart. We need to (1) mark the element has being dragged so we don't drop onto itself, (2) read the data from the element being drag and copy into the data transfer.

```js
el.addEventListener("dragstart", function (e) {
  e.target._dragging = true;
  var drag = {};
  scanElementIntoSpec("drag", drag, e.target);
  e.dataTransfer.setData("application/json", JSON.stringify(drag));
});
```

### End

The only thing we need to do when the dragging ends is unmark that the element is being dragged.

```js
el.addEventListener("dragend", function (e) {
  e.target._dragging = false;
});
```

### Enter
When an element enters another element, we need to (1) make sure we do nothing if it is itself, (2) extract the bit vectors, (3) intersect the bit vectors and set the dropEffect appropriately. A hard thing at hand is how to specify the behavior for visualizing the reaction, so for now we hack at the border.

```js
el.addEventListener("dragenter", function (e) {
    // ignore self
    if (e.target._dragging) { return; }
    e.preventDefault();
    scanElementIntoSpec("drop", drop, e.target);
    var drag = JSON.parse(e.dataTransfer.getData("application/json"));
    if (intersectTypes(drag.types, drop.types)) {
        e.target.style = "border:1px solid red";
        e.dataTransfer.dropEffect = 'move';
        drop.effect = 'move';
    } else {
        e.dataTransfer.dropEffect = 'none';
        drop.effect = 'none';
    }
});
```

## Over
As a bug, we need to leverage the drag:over to echo the dropEffect

```js
el.addEventListener("dragover", function (e) {
    e.preventDefault();
    e.dataTransfer.dropEffect = drop.effect;
});
```

## Leave
The element is no longer interesting, so let's clean up

```js
el.addEventListener("dragleave", function (e) {
    e.preventDefault();
    e.target.style = "";
});
```

## Drop
When the drop happens, if the bit vectors intersect then we merge the data messages and send the message.

```js
el.addEventListener("drop", function (e) {
    e.preventDefault();
    e.target.style = "";
    scanElementIntoSpec("drop", drop, e.target);
    var drag = JSON.parse(e.dataTransfer.getData("application/json"));
    if (intersectTypes(drag.types, drop.types) && connection) {
        var msg = {};
        for (var k in drag.data) {
            msg[k] = drag.data[k];
        }
        for (var k in drop.data) {
            msg[k] = drop.data[k];
        }
        connection.send(drop.channel, msg, {
          success: function () {
            // TODO: fire success
          },
          failure: function (reason) {
            // TODO: fire failure
          }
        });
    }
});
```


And boom, a simple drag and drop system is born. Now, there are some thing to sort out before integrating into RxHTML such as:

I need a way to sort out 
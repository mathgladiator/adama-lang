# Audit Details

This is a linear deep dive of the features as I build the type checker.

# element: &lt;connection&gt;

&lt;connection&gt; establishes the data: channel with a reactive tree that updates via a WebSocket.

* not a real element
* attribute 'name' will define the name of the connection
* mode: attribute 'use-domain' will use the location.host to pick a document via the domain mapping parts
* mode: attribute 'space' +  attribute 'key' will find the document directly via the space and key
* mode: attribute 'billing' will connect to the current user's billing document
* attribute 'identity' is used for authorization
* attribute 'redirect' will be used to redirect the page to a different location
* attribute 'name', 'identity', 'redirect', 'space', 'key' are all reactive
* children branch based on connection status
* attribute 'keep-open' prevents the falling edge of connection status from from connected to disconnected from rendering the false branch

# element: &lt;connection-status&gt;

&lt;connection-status&gt; is used to present the status of the connection (connected/disconnected)

* not a real element
* attribute 'name' is used to find the connection to reflect the status of
* children branch of connected and disconnected (rx:else/rx:disconnected) 

# element: &lt;pick&gt;

&lt;pick&gt; is used to find an existing connection to use as the data channel

* not a real element
* attribute 'name' is used to find a previously defined connection, this connection is then re-used
* children branch based on connection status
* attribute 'keep-open' prevents the falling edge of connection status from from connected to disconnected from rendering the false branch

# attribute: rx:expand-view-state

This attribute modifies rx:scope, rx:iterate, rx:repeat such that the scope of the view state is expanded. For example, if you scope into an object, then rx:expand-view-state will scope the view to mirror it such that the view state is isolated to that object's name.

# attribute: rx:scope

This attribute is like "change directory" where you scope into a object. rx:scope="field"

* rx:scope="field" is "cd field"
* rx:scope="/root" is "cd /root"
* rx:scope="../sibling is "cd ../sibling"
* rx:scope="view:x" is like "cd /mount/tree/view/x" (or cd "V:\x)

# attribute: rx:iterate

This attribute will iterate over the elements in an array or list. Unlike scope, this will change directory effectively twice because there are two levels: the list level and the element level.

* The value follows the same rules as rx:scope EXCEPT each iterate also scopes into the element by index
* Requires there to be exactly one child element. If this isn't the case, then a false div is injected.
* Hint: for &lt;table&gt; use &lt;tbody&gt;

# attribute rx:if, rx:ifnot

Conditions!

* at core, rx:if and rx:ifnot are the same up beyond how the value is expressed
* branch: "decide:$channel" will be true when there is a decision to be made based on the 'name' (default to the 'name' attribute)
* branch: "choose:$channel"/"chosen:$channel" will be true when a value has been chosen for a multi-select as determined by channel & name
* branch: "finalize:$channel" will be true if a multi-select is capable of being sent
* rx:if="$path" is true when the given path evaluates to a value of true (or a value exists)
* rx:if="$path1=$path2" is true when the given path evalutes to values that are equal
* attribute 'force-hiding' when present will show/hide the dom element based on the true branch (this ignores rx:else from a rendering perspective)

A decision is a concept of Adama from board game days where there is an inversion of control such that the server asks the client to make a decision. This decision is determined by a channel along with a unique field determined by the name attribute. The default field is 'id'.

A multi-select decision is when the server asks "pick between #X to #Y elements from the given array."

An odd behavior of rx:if is it only renders either the true/false branches if the $path is valid and has a value.

# attribute: rx:monitor

rx:monitor will watch the given path and if it is a number will trigger rx:rise and rx:fall commands

* rx:rise is fired when the integer/number goes up (or becomes true for the first time)
* rx:fall is fired when the integer/number goes down (or becomes false for the first time)

# attribute: rx:behavior

rx:behavior is an escape hatch for very simple behaviors

See [escape hatches](/rxhtml/escape.md)

# attribute: rx:wrap (deprecated)

rx:wrap is the precursor to rx:custom, see [escape hatches](/rxhtml/escape.md)

# attribute: rx:custom

rx:custom behaves like rx:template except custom JavaScript code run which is registered.

see [escape hatches](/rxhtml/escape.md)

# attribute: rx:repeat

rx:repeat is like rx:iterate except the path revolves an integer

* as the number increases, more children are added
* as the number decreases, the most recently added children are removed

This is useful very useful for adding form elements dynamically for a form submission

# attribute: rx:switch

rx:switch works with rx:case such that path revolves a value and the DOM is constructed on the fly based on the value. The immediate children are chosen based on rx:case's value matching the value of the path resolved via rx:switch.
As a side effect, this allows multiple children to be added such that complex things are built. Note: this behavior is respected in templates

```html
<div rx:switch="thing">
    Always rendered
    <div rx:case="a">Thing is A</div>
    <div rx:case="b">Thing is B</div>
    <div rx:case="a">Thing is A</div>
</div>
```

# attribute: rx:template
This attribute is used to control the children of the element by pulling from a &lt;template&gt; element

The children within a template can use rx:case to pick

## element: fragment

When used within a &lt;template&gt;, this references the children of the invocation.

```html
<forest>
    <template name="foo">
        <nav>
            Hello <fragment/>
        </nav>
    </template>
    <page uri="/demo">
        <div rx:template="foo">
            World
        </div>
    </page>
</forest>
```

fragment also has the capability of leveraging the attribute rx:case

```html
<forest>
    <template name="foo">
        <nav>
            Hello <fragment case="name"/>!
            It is time to begin the <fragment case="task"/>
        </nav>
    </template>
    <page uri="/demo">
        <div rx:template="foo">
            <span rx:case="name">World</span>
            <span rx:case="task">Ritual</span>
        </div>
    </page>
</forest>
```

# attribute: children-only

For rx:case usage (rx:switch and templates), the "children-only" attribute will ignore the holding element and merge the children into the parent

```html
<forest>
    <template name="foo">
        <nav>
            Hello <fragment case="name"/>!
            It is time to begin the <fragment case="task"/>
        </nav>
    </template>
    <page uri="/demo">
        <div rx:template="foo">
            <span rx:case="name" children-only>World</span>
            <span rx:case="task" children-only>Ritual</span>
        </div>
    </page>
</forest>
```

# element: &lt;inline-template&gt;
This element is like rx:template except it merges the children of the &lt;template&gt; element into the current parent

* not a real element
* children are merged into the parent

# element: &lt;todo-task&gt;
At one point, RxHTML had a vision of having an embedded project management system for tracking TODOs...

* should be deprecated

# element: &lt;monitor&gt;

Like rx:monitor except introduces no dom element. Only supports rx:rise and rx:fall
* the attribute delay controls how the signal is debounced

# element: &lt;view-write&gt;

This is a transfer of state from anything to the view.

```html
<view-write path="v" value="{name} is {value}" />
```

# element: &lt;lookup&gt;

Simply look up a field by path and render to the DOM as a text node.

* attribute 'path' is used to resolve to a value that is injected as a Text Node
* attribute 'transform' is a function that makes the value more pretty. For the datetime, 'time_ago' is a nice one
* attribute 'refresh' is used to recompute the transform based on a frequency (refresh="$x" where $x is an integer representing milliseconds)

TODO: how to add custom transforms

# element: &lt;trusted-html&gt;

Simply look up a field by path and render to the DOM as a dom element under a 'div'

* attribute 'path' is used to resolve to a value that is then injected via innerHTML

# element: &lt;exit-gate&gt;

Set a guard to protect traversal away from the current page

* attribute 'guard' is used to read only from the view a path to detect a guard

# element: &lt;title&gt;

Normally, &lt;title&gt; is in the &lt;meta&gt; of a page, but since RxHTML operates with a forest of pages, the &lt;title&gt; is now part of a page based on any branch and uses a dynamic value attribute

```html
<forest>
    <template>
        <title value="Hi {name}" />
    </template>
</forest>
```

# element: &lt;view-state-params&gt;

This little neat non-element will monitor variables in the view state via "sync:$name=$path" and then dump them into the location's search after ?$name=path. This lets the view state become reactively deep linked

# elements (input, textarea, select)

* attribute rx:sync will periodically (see rx:debounce) write the value into the viewstate
* attribute rx:debounce defines the frequency (via milliseconds) of how often the viewstate gets updated

# element &lt;sign-out&gt;

This element will destroy the connected identity

* attribute 'name' will define which identity to destroy

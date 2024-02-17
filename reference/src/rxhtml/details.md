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

This attribute is like "change directory" where you scope into a object

# attribute: rx:iterate

# attribute: rx:repeat


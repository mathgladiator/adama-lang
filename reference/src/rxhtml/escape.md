# Escape Hatches

## rx:behavior

A simple pass-through indexing thingy... TODO

## rx:custom

Ultimately, components care about three things: (1) reading data from server, (2) writing control state, (3) sending messages to the server to mutate data.

### port:$path=$viewPath

attributes prefixed "port:" are injected to create functions such that the rx:custom module can emit data to the view state controlled by a path

### parameter:$name=$value

attributed prefixed by "parameter:" are turned into a reactive object that the custom code can hook into to learn about updates

## TODO: what is hard

* get by id not possible, it's not mounted
* good "settle" signal when the data and DOM are stable. How do you know when the DOM is ready to be scanned? or when it was updated? -> examples

examples: 
* drag and drop where the items are dynamic
* easier to grab an element and add an event listener
* viewstate that is iterable AND THEN ViewState.merge can populate
* mini around around manipulating the view state with respect to trees
* lifecycle, research various other frames about lifecycle
* testing web components with RxHTML like shoe-lace
* feature flags
* any event -> access to the event data
* scrollbar -> figure better ways of addressing bars
* a better event escape hatcing (improving events that have java with full event parity
* manipulate data on front-end
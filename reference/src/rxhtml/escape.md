# Escape Hatches

## rx:custom

Ultimately, components care about three things: (1) reading data from server, (2) writing control state, (3) sending messages to the server to mutate data.

### port:$path=$viewPath

attributes prefixed "port:" are injected to create functions such that the rx:custom module can emit data to the view state controlled by a path

### parameter:$name=$value

attributed prefixed by "parameter:" are turned into a reactive object that the custom code can hook into to learn about updates
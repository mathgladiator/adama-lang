# The state machine


Each document in Adama acts as a [state machine](https://en.wikipedia.org/wiki/Finite-state_machine) where there is a single state label indicating the major state of the document.
The rest of the document is considered supporting or related state included within the state machine.
This approach simplifies the coding process.
Initially, the code is associated with a state machine label:


```adama
#start {
  /* fun times */
}
```
Second, transitioning into a state within the state machine is done via the ```transition``` keyword.
This keyword allows you to specify the new state label.

```adama
@construct {
  transition #state;
}
```

The ```transition``` keyword not only allows you to transition to a new state but can also delay the transition using the in keyword, which specifies the time in seconds.
This is useful when you want to schedule a future transition, for example, transitioning to a different state after a certain amount of time has passed.
By specifying the delay in seconds, you can set a timer to transition automatically to the desired state.

```adama
bool done;
@construct {
  done = false;
  transition #state;
}

#start {
  transition #end in 60;
}

#end {
  done = true;
}
```
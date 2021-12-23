---
id: reference-state-machine
title: The State Machine
---

## Fast Intro

Each document acts like a [state machine](https://en.wikipedia.org/wiki/Finite-state_machine) where there is a single state label indicating the major state of the document. The rest of the document is considered supporting or related state included within the state machine. The game this induces on the code is very simple. First, you associate code to a state machine label:

```adama
#start {
  /* fun times */
}
```

Second, you *transition* into that state _somehow_ (i.e. [constructor](reference-constructor) or [message handler](reference-channels-handlers-futures)):
```adama
@construct {
  transition #state;
}
```

Finally, the state transitions can happen over time via the **in** keyword on the **transition** keyword.
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

## Diving Into Details

### Outline Code

### Relationship to Messages & Futures

### Transition

### Pre-emption
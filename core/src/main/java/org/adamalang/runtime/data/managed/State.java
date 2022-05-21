package org.adamalang.runtime.data.managed;

public enum State {
  // we don't know the state
  Unknown,

  // an outbound find request has been requested
  Finding,

  // the state is on the machine without any updates
  OnMachine,

  // the state is in the archive and is being restored
  Restoring
}

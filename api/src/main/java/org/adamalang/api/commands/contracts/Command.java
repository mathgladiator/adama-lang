package org.adamalang.api.commands.contracts;

/** At core, a command is an asynchronous state machine to track stack during a linear asynchronous workflow */
public interface Command {
  public void execute();
}

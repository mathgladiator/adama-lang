package org.adamalang.common.net;

/** a handle to the server so consumer can control its behavior */
public interface ServerHandle {
  /** wait until the end of the server's life */
  public void waitForEnd();
  /** stop the server */
  public void kill();
}

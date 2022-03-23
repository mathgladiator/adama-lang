package org.adamalang.bald.organization;

public class Region {
  public final long position;
  public final int size;
  boolean freed;

  public Region(long position, int size) {
    this.position = position;
    this.size = size;
    this.freed = false;
  }
}

package org.adamalang.caravan.index;

/** a region in memory */
public class Region {
  public final long position;
  public final int size;

  public Region(long position, int size) {
    this.position = position;
    this.size = size;
  }

  @Override
  public String toString() {
    return "[" + position + "," + (position + size) + ")";
  }
}

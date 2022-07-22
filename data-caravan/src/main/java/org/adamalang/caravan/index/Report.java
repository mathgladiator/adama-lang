package org.adamalang.caravan.index;

/** a report of free space */
public class Report {
  private long total;
  private long free;

  /** add total bytes allocated to the report */
  public void addTotal(long total) {
    this.total += total;
  }

  /** report some free bytes available */
  public void addFree(long free) {
    this.free += free;
  }

  /** get the total bytes allocated to the storage */
  public long getTotalBytes() {
    return total;
  }

  /** get the total bytes free to the storage system */
  public long getFreeBytesAvailable() {
    return free;
  }

  /** return true if the free is below the given threshold (i.e. 0.2) */
  public boolean alarm(double ratio) {
    return free < ((long) total * ratio);
  }
}

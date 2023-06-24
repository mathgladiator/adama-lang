/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
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

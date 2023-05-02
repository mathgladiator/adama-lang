/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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

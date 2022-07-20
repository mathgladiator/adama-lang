/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.data;

public class MeteringSummaryPartialPerTarget {
  public long count;
  public long memory;
  public long connections;

  public MeteringSummaryPartialPerTarget() {
    this.count = 0;
    this.memory = 0;
    this.connections = 0;
  }

  public void include(long count, long memory, long connections) {
    this.count = Math.max(this.count, count);
    this.memory = Math.max(this.memory, memory);
    this.connections = Math.max(this.connections, connections);
  }
}

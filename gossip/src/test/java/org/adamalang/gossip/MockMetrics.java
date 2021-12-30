/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.gossip;

import org.junit.Assert;

public class MockMetrics implements Metrics {

  private final String name;
  private final StringBuilder seq;

  public MockMetrics() {
    this("no-name");
  }

  public MockMetrics(String name) {
    this.name = name;
    this.seq = new StringBuilder();
  }

  public synchronized void dump() {
    System.err.println(seq);
  }

  @Override
  public synchronized void bump_sad_return() {
    seq.append("[SR]");
  }

  @Override
  public synchronized void bump_complement() {
    seq.append("[COMP]");
  }

  @Override
  public synchronized void bump_optimistic_return() {
    seq.append("[OPRET]");
  }

  @Override
  public synchronized void bump_turn_tables() {
    seq.append("[TT]");
  }

  @Override
  public synchronized void bump_start() {
    seq.append("[BS]");
  }

  @Override
  public synchronized void bump_found_reverse() {
    seq.append("[FR]");
  }

  @Override
  public synchronized void bump_quick_gossip() {
    seq.append("[QG]");
  }

  @Override
  public synchronized void bump_slow_gossip() {
    seq.append("[SG]");
  }

  @Override
  public void log_error(Throwable cause) {
    seq.append("[LOG-ERROR]");
  }

  public void assertFlow(String expected) {
    Assert.assertEquals(expected, seq.toString());
  }
}

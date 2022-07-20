/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.gossip;

import org.adamalang.common.metrics.Inflight;
import org.junit.Assert;

public class MockGossipMetrics implements GossipMetrics {

  private final String name;
  private final StringBuilder seq;

  public MockGossipMetrics() {
    this("no-name");
  }

  public MockGossipMetrics(String name) {
    this.name = name;
    this.seq = new StringBuilder();
  }

  @Override
  public void wake() {
    seq.append("[WAKE]");
  }

  public synchronized void dump() {
    System.err.println(seq);
  }

  @Override
  public synchronized void bump_sad_return() {
    seq.append("[SR]");
  }

  @Override
  public synchronized void bump_client_slow_gossip() {
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
  public synchronized void bump_server_slow_gossip() {
    seq.append("[SG]");
  }

  @Override
  public void log_error(Throwable cause) {
    seq.append("[LOG-ERROR]");
  }

  public void assertFlow(String expected) {
    Assert.assertEquals(expected, seq.toString());
  }

  public Inflight gossips_inflight() {
    return new Inflight() {
      @Override
      public void up() {

      }

      @Override
      public void down() {

      }

      @Override
      public void set(int value) {
      }
    };
  }
}

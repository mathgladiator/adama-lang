/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.mocks;

import org.adamalang.net.client.contracts.SimpleEvents;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockSimpleEvents implements SimpleEvents {
  private final ArrayList<String> history;
  private ArrayList<CountDownLatch> latches;

  public MockSimpleEvents() {
    this.history = new ArrayList<>();
    latches = new ArrayList<>();
  }

  public synchronized Runnable latchAt(int write) {
    CountDownLatch latch = new CountDownLatch(write);
    latches.add(latch);
    return () -> {
      try {
        Assert.assertTrue(latch.await(20000, TimeUnit.MILLISECONDS));
      } catch (InterruptedException ie) {
        Assert.fail();
      }
    };
  }

  public synchronized void assertWrite(int write, String expected) {
    Assert.assertTrue(write < history.size());
    Assert.assertEquals(expected, history.get(write));
  }

  @Override
  public void connected() {
    write("CONNECTED");
  }

  @Override
  public void delta(String data) {
    write("DELTA:" + data);
  }

  @Override
  public void error(int code) {
    write("ERROR:" + code);
  }

  @Override
  public void disconnected() {
    write("DISCONNECTED");
  }

  private synchronized void write(String x) {
    System.err.println("SMOCK:" + x);
    history.add(x);
    for (CountDownLatch latch : latches) {
      latch.countDown();
    }
  }
}

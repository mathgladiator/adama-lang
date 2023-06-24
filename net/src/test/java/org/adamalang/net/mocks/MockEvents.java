/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.mocks;

import org.adamalang.net.client.contracts.Events;
import org.adamalang.net.client.contracts.Remote;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MockEvents implements Events {
  private final ArrayList<String> history;
  private ArrayList<CountDownLatch> latches;
  private final AtomicReference<Remote> gotRemote;
  private CountDownLatch remoteFound;

  public MockEvents() {
    this.history = new ArrayList<>();
    latches = new ArrayList<>();
    this.gotRemote = new AtomicReference<>();
    this.remoteFound = new CountDownLatch(1);
  }

  public Remote getRemote() throws Exception {
    Assert.assertTrue(remoteFound.await(5000, TimeUnit.MILLISECONDS));
    return gotRemote.get();
  }

  public synchronized Runnable latchAt(int write) {
    CountDownLatch latch = new CountDownLatch(write);
    latches.add(latch);
    return () -> {
      try {
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
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
  public void connected(Remote remote) {
    gotRemote.set(remote);
    remoteFound.countDown();
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
    System.err.println("MOCK:" + x);
    history.add(x);
    for (CountDownLatch latch : latches) {
      latch.countDown();
    }
  }
}

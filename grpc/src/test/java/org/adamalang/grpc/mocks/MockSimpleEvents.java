package org.adamalang.grpc.mocks;

import org.adamalang.grpc.client.contracts.Remote;
import org.adamalang.grpc.client.contracts.SimpleEvents;
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

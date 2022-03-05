package org.adamalang.net.mocks;

import org.adamalang.net.client.contracts.MeteringStream;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockMeteringFlow implements MeteringStream {

  private final ArrayList<String> history;
  private ArrayList<CountDownLatch> latches;

  public MockMeteringFlow() {
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

  private synchronized void write(String x) {
    System.err.println("SMOCK:" + x);
    history.add(x);
    for (CountDownLatch latch : latches) {
      latch.countDown();
    }
  }

  public synchronized void assertWrite(int write, String expected) {
    Assert.assertTrue(write < history.size());
    Assert.assertEquals(expected, history.get(write));
  }

  @Override
  public synchronized void handle(String target, String batch, Runnable after) {
    write("HANDLE:" + batch);
    after.run();
  }

  @Override
  public synchronized void failure(int code) {
    write("ERROR:" + code);
  }

  @Override
  public synchronized void finished() {
    write("FINISHED");
  }
}

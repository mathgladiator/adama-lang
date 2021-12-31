package org.adamalang.grpc.client.contracts;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class QueueActionTests {
  @Test
  public void timeout() {
    AtomicInteger x = new AtomicInteger(0);
    QueueAction<String> action = new QueueAction<String>() {
      @Override
      protected void executeNow(String item) {
        x.incrementAndGet();
      }

      @Override
      protected void timeOut() {
        x.addAndGet(100);
      }

      @Override
      protected void rejected() {
        x.addAndGet(1000);
      }
    };
    action.execute("z");
    action.execute("z");
    action.execute("z");
    Assert.assertEquals(3, x.get());
    Assert.assertTrue(action.isAlive());
    action.killDueToTimeout();
    action.execute("z");
    action.execute("z");
    action.execute("z");
    Assert.assertFalse(action.isAlive());
    Assert.assertEquals(103, x.get());
  }

  @Test
  public void rejected() {
    AtomicInteger x = new AtomicInteger(0);
    QueueAction<String> action = new QueueAction<String>() {
      @Override
      protected void executeNow(String item) {
        x.incrementAndGet();
      }

      @Override
      protected void timeOut() {
        x.addAndGet(100);
      }

      @Override
      protected void rejected() {
        x.addAndGet(1000);
      }
    };
    action.execute("z");
    action.execute("z");
    action.execute("z");
    Assert.assertEquals(3, x.get());
    Assert.assertTrue(action.isAlive());
    action.killDueToReject();
    action.execute("z");
    action.execute("z");
    action.execute("z");
    Assert.assertFalse(action.isAlive());
    Assert.assertEquals(1003, x.get());
  }
}

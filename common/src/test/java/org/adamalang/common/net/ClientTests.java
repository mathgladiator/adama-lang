package org.adamalang.common.net;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.MachineIdentity;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientTests {
  @Test
  public void nope() throws Exception {
    MachineIdentity identity = NetSuiteTests.identity();
    NetBase base = new NetBase(2, 4);
    try {
      Client client = new Client(base, identity);
      CountDownLatch latch = new CountDownLatch(1);
      client.connect("192.1.200.1:4242", new Lifecycle() {
        @Override
        public void connected(ChannelClient channel) {
        }

        @Override
        public void failed(ErrorCodeException ex) {
          latch.countDown();
          System.err.println("GotException:" + latch.getCount());
          ex.printStackTrace();
        }

        @Override
        public void disconnected() {
        }
      });
      System.err.println("Waiting");
      long started = System.currentTimeMillis();
      Assert.assertTrue(latch.await(60000, TimeUnit.MILLISECONDS));
      System.err.println("Took:" + (System.currentTimeMillis() - started));
    } finally {
      base.shutdown();
    }
  }

  @Test
  public void badtarget() throws Exception {
    MachineIdentity identity = NetSuiteTests.identity();
    NetBase base = new NetBase(2, 4);
    try {
      Client client = new Client(base, identity);
      CountDownLatch latch = new CountDownLatch(1);
      client.connect("192.1.200.1", new Lifecycle() {
        @Override
        public void connected(ChannelClient channel) {
        }

        @Override
        public void failed(ErrorCodeException ex) {
          latch.countDown();
          System.err.println("GotException:" + latch.getCount());
          ex.printStackTrace();
        }

        @Override
        public void disconnected() {
        }
      });
      System.err.println("Waiting");
      long started = System.currentTimeMillis();
      Assert.assertTrue(latch.await(60000, TimeUnit.MILLISECONDS));
      System.err.println("Took:" + (System.currentTimeMillis() - started));
    } finally {
      base.shutdown();
    }
  }
}

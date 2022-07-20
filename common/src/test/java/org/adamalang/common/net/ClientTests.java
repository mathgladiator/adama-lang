/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.net;

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientTests {
  @Test
  public void nope() throws Exception {
    NetBase base = new NetBase(NetSuiteTests.identity(), 1, 4);
    try {
      CountDownLatch latch = new CountDownLatch(1);
      base.connect("192.1.200.1:4242", new Lifecycle() {
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
    NetBase base = new NetBase(NetSuiteTests.identity(), 2, 4);
    try {
      CountDownLatch latch = new CountDownLatch(1);
      base.connect("192.1.200.1", new Lifecycle() {
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

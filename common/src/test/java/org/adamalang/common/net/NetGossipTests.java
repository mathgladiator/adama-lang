/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.gossip.Engine;
import org.adamalang.common.gossip.EngineTests;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class NetGossipTests {
  @Test
  public void flow() throws Exception {
    NetBase A = new NetBase(new NetMetrics(new NoOpMetricsFactory()), NetSuiteTests.identity(), 2, 4);
    NetBase B = new NetBase(new NetMetrics(new NoOpMetricsFactory()), NetSuiteTests.identity(), 2, 4);
    int portA = (int) (45000 + Math.random() * 2000);

    Engine a = A.startGossiping();
    Engine b = B.startGossiping();

    EngineTests.TargetCollectorAsserter target = new EngineTests.TargetCollectorAsserter();
    Runnable second = target.latchAt(2);
    b.subscribe("adama", target);

    AtomicReference<Runnable> aHB = new AtomicReference<>();
    {
      CountDownLatch aHBlatch = new CountDownLatch(1);
      a.createLocalApplicationHeartbeat("adama", 5000, 5001, new Consumer<Runnable>() {
        @Override
        public void accept(Runnable runnable) {
          aHB.set(runnable);
          aHBlatch.countDown();
        }
      });
      Assert.assertTrue(aHBlatch.await(10000, TimeUnit.MILLISECONDS));
    }
    aHB.get().run();

    A.serve(portA, (upstream) -> {
      return new ByteStream() {
        @Override
        public void request(int bytes) {
        }

        @Override
        public ByteBuf create(int bestGuessForSize) {
          throw new UnsupportedOperationException();
        }

        @Override
        public void next(ByteBuf buf) {
          System.err.println("GOT BYTES");
        }

        @Override
        public void completed() {
          System.err.println("COMPLETED");
        }

        @Override
        public void error(int errorCode) {
          System.err.println("ERRORCODE");
        }
      };
    });
    AtomicBoolean needConnect = new AtomicBoolean(true);
    int attempts = 5;
    while (needConnect.get() && attempts-- > 0) {
      CountDownLatch connectStatus = new CountDownLatch(1);
      B.connect("127.0.0.1:" + portA, new Lifecycle() {
        @Override
        public void connected(ChannelClient channel) {
          needConnect.set(false);
          connectStatus.countDown();
          System.err.println("CONNECTED CLIENT");
        }

        @Override
        public void failed(ErrorCodeException ex) {
          connectStatus.countDown();
          System.err.println("FAILED:" + ex.code);
        }

        @Override
        public void disconnected() {
          System.err.println("DISCONNECTED");
        }
      });
      Assert.assertTrue(connectStatus.await(5000, TimeUnit.MILLISECONDS));
    }

    second.run();
    Assert.assertEquals("127.0.0.1:5000", target.logAt(1));
  }
}

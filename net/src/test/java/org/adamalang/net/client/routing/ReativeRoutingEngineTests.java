/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.routing;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.contracts.RoutingSubscriber;
import org.adamalang.net.client.routing.reactive.ReativeRoutingEngine;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ReativeRoutingEngineTests {
  @Test
  public void flow() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    MockSpaceTrackingEvents events = new MockSpaceTrackingEvents();
    SimpleExecutor derp = SimpleExecutor.create("derp");
    ReativeRoutingEngine engine = new ReativeRoutingEngine(metrics, derp, events, 50, 25);

    AtomicReference<Runnable> cancelRunnable = new AtomicReference<>();
    CountDownLatch latchGotCancel = new CountDownLatch(1);
    CountDownLatch becameZ = new CountDownLatch(1);
    CountDownLatch becameW = new CountDownLatch(1);
    CountDownLatch becameWAgain = new CountDownLatch(2);

    engine.subscribe(new Key("space", "key"), new RoutingSubscriber() {
        @Override
        public void onRegion(String region) {

        }

        @Override
        public void onMachine(String machine) {
          System.err.println(machine);
          if ("z".equals(machine)) {
            becameZ.countDown();
          }
          if ("w".equals(machine)) {
            becameW.countDown();
            becameWAgain.countDown();
          }
        }
      }, (cancel) -> {
        cancelRunnable.set(cancel);
        latchGotCancel.countDown();
      });
    Assert.assertTrue(latchGotCancel.await(10000, TimeUnit.MILLISECONDS));
    // NOTE: this list was built to be adversarial, so each one will trigger an immediate change
    // without the broadcast delay
    // you can play with this by increase the thread sleep to 200 and each thing will pop out
    for (String inj : new String[] {"y", "3", "t", "4", "w", "2", "1", "x", "z"}) {
      if (inj.equals("y")) {
        CountDownLatch latch = new CountDownLatch(1);
        engine.random(
            new Consumer<String>() {
              @Override
              public void accept(String s) {
                Assert.assertNull(s);
                latch.countDown();
              }
            });
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
      }
      engine.integrate(inj, Collections.singleton("space"));
      if ("w".equals(inj)) {
        // we checkpoint on W because we will be removing a bunch of stuff
        Assert.assertTrue(becameW.await(10000, TimeUnit.MILLISECONDS));
      }
      if (inj.equals("y")) {
        CountDownLatch latch = new CountDownLatch(1);
        engine.random(
            new Consumer<String>() {
              @Override
              public void accept(String s) {
                Assert.assertTrue("y".equals(s));
                latch.countDown();
              }
            });
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
      } else {
        engine.random(new Consumer<String>() {
          @Override
          public void accept(String s) {
            System.err.println("random: " + s);
          }
        });
      }
      if ("2".equals(inj)) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        engine.list("space", new Consumer<TreeSet<String>>() {
          @Override
          public void accept(TreeSet<String> strings) {
            success.set(true);
            for (String inj : new String[] {"y", "3", "t", "4", "w", "2"}) {
              if (!strings.contains(inj)) {
                success.set(false);
              }
            }
            latch.countDown();
          }
        });
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(success.get());
      }
      {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        engine.get(new Key("space", "key"), new RoutingSubscriber() {
          @Override
          public void onRegion(String region) {

          }

          @Override
          public void onMachine(String machine) {
            // given the irregularity of the broadcast, can't assert much... hrmm
            success.set(true);
            latch.countDown();
          }
        });
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(success.get());
      }
      Thread.sleep(25);
    }
    Assert.assertTrue(becameZ.await(10000, TimeUnit.MILLISECONDS));
    for (String kill : new String[] {"2", "1", "x", "z"}) {
      engine.remove(kill);
    }
    Assert.assertTrue(becameWAgain.await(10000, TimeUnit.MILLISECONDS));
    cancelRunnable.get().run();
    {
      CountDownLatch latch = new CountDownLatch(1);
      derp.execute(new NamedRunnable("flush") {
        @Override
        public void execute() throws Exception {
          latch.countDown();
        }
      });
      Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    }
  }
}

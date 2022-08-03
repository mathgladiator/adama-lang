/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.gossip;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.net.ByteStream;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class EngineTests {

  private Runnable createApp(Engine x, String role, int port, int mport) throws Exception {
    AtomicReference<Runnable> heartbeat = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);
    x.createLocalApplicationHeartbeat(role, port, mport, (hb) -> {
      heartbeat.set(hb);
      latch.countDown();
    });
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    return heartbeat.get();
  }

  private void exchange(Engine a, Engine b) {
    Engine.Exchange ex = a.client();
    ByteStream up = b.server(ex);
    ex.start(up);
  }

  public static class TargetCollectorAsserter implements Consumer<Collection<String>> {
    public ArrayList<String> log;
    public ArrayList<CountDownLatch> latches;

    public TargetCollectorAsserter() {
      this.log = new ArrayList<>();
      this.latches = new ArrayList<>();
    }

    private synchronized Runnable latchAt(int at) {
      CountDownLatch latch = new CountDownLatch(at);
      latches.add(latch);
      return () -> {
        try {
          Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        } catch (Exception ex) {
          Assert.fail();
        }
      };
    }

    public synchronized String logAt(int k) {
      Assert.assertTrue(k < log.size());
      return log.get(k);
    }

    private synchronized void witness(String str) {
      log.add(str);
      Iterator<CountDownLatch> it = latches.iterator();
      while (it.hasNext()) {
        CountDownLatch latch = it.next();
        latch.countDown();
        if (latch.getCount() == 0) {
          it.remove();
        }
      }
    }

    @Override
    public void accept(Collection<String> values) {
      TreeSet<String> sorted = new TreeSet<>(values);
      StringBuilder sb = new StringBuilder();
      boolean first = true;
      for (String val : sorted) {
        if (!first) {
          sb.append(",");
        }
        sb.append(val);
        first = false;
      }
      witness(sb.toString());
    }
  }

  @Test
  public void nothing() {
    Engine a = new Engine("127.0.0.1", new GossipMetrics(new NoOpMetricsFactory()));
    Engine b = new Engine("127.0.0.2", new GossipMetrics(new NoOpMetricsFactory()));
    exchange(a, b);
  }

  @Test
  public void single() throws Exception {
    Engine a = new Engine("127.0.0.1", new GossipMetrics(new NoOpMetricsFactory()));
    Runnable hb = createApp(a, "role", 100, 101);
    hb.run();
    Engine b = new Engine("127.0.0.2", new GossipMetrics(new NoOpMetricsFactory()));
    TargetCollectorAsserter blog = new TargetCollectorAsserter();
    Runnable first = blog.latchAt(1);
    Runnable second = blog.latchAt(2);
    b.subscribe("role", blog);
    first.run();
    Assert.assertEquals("", blog.logAt(0));
    exchange(a, b);
    second.run();
    Assert.assertEquals("127.0.0.1:100", blog.logAt(1));
  }
}

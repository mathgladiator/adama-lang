/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.gossip;

import org.adamalang.common.TimeSource;
import org.adamalang.common.gossip.codec.GossipProtocol;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.net.ByteStream;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
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
    private final ArrayList<String> log;
    private final ArrayList<CountDownLatch> latches;
    private final CountDownLatch sizeLatch;
    private final int size;

    public TargetCollectorAsserter() {
      this(1);
    }

    public TargetCollectorAsserter(int size) {
      this.log = new ArrayList<>();
      this.latches = new ArrayList<>();
      this.sizeLatch = new CountDownLatch(1);
      this.size = size;
    }

    public synchronized Runnable latchAt(int at) {
      CountDownLatch latch = new CountDownLatch(at);
      latches.add(latch);
      return () -> {
        try {
          Assert.assertTrue(latch.await(15000, TimeUnit.MILLISECONDS));
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
      System.err.println(str);
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
      if (values.size() == size) {
        sizeLatch.countDown();
      }
    }

    public void awaitSize() throws Exception {
      Assert.assertTrue(sizeLatch.await(2500, TimeUnit.MILLISECONDS));
    }

    public boolean testSize() throws Exception {
      return !sizeLatch.await(5, TimeUnit.MILLISECONDS);
    }
  }

  @Test
  public void nothing() {
    Engine a = new Engine("127.0.0.1", new GossipMetrics(new NoOpMetricsFactory()), TimeSource.REAL_TIME);
    Engine b = new Engine("127.0.0.2", new GossipMetrics(new NoOpMetricsFactory()), TimeSource.REAL_TIME);
    exchange(a, b);
  }

  @Test
  public void single() throws Exception {
    Engine a = new Engine("127.0.0.1", new GossipMetrics(new NoOpMetricsFactory()), TimeSource.REAL_TIME);
    ArrayList<GossipProtocol.Endpoint[]> history = new ArrayList<>();
    a.setWatcher(new Consumer<GossipProtocol.Endpoint[]>() {
      @Override
      public void accept(GossipProtocol.Endpoint[] endpoints) {
        history.add(endpoints);
      }
    });
    Runnable hb = createApp(a, "role", 100, 101);
    hb.run();
    Engine b = new Engine("127.0.0.2", new GossipMetrics(new NoOpMetricsFactory()), TimeSource.REAL_TIME);
    TargetCollectorAsserter blog = new TargetCollectorAsserter();
    Runnable first = blog.latchAt(1);
    Runnable second = blog.latchAt(2);
    b.subscribe("role", blog);
    first.run();
    Assert.assertEquals("", blog.logAt(0));
    exchange(a, b);
    second.run();
    Assert.assertEquals("127.0.0.1:100", blog.logAt(1));
    Assert.assertEquals(2, history.size());
    Assert.assertEquals(0, history.get(0).length);
    Assert.assertEquals(1, history.get(1).length);
  }

  @Test
  public void cross() throws Exception {
    Engine a = new Engine("127.0.0.1", new GossipMetrics(new NoOpMetricsFactory()), TimeSource.REAL_TIME);
    createApp(a, "role", 100, 101).run();
    Engine b = new Engine("127.0.0.2", new GossipMetrics(new NoOpMetricsFactory()), TimeSource.REAL_TIME);
    createApp(b, "role", 100, 101).run();
    TargetCollectorAsserter a_log = new TargetCollectorAsserter();
    TargetCollectorAsserter b_log = new TargetCollectorAsserter();
    Runnable a_first = a_log.latchAt(1);
    Runnable a_second = a_log.latchAt(2);
    Runnable b_first = b_log.latchAt(1);
    Runnable b_second = b_log.latchAt(2);
    a.subscribe("role", a_log);
    b.subscribe("role", b_log);
    a_first.run();
    b_first.run();
    Assert.assertEquals("127.0.0.1:100", a_log.logAt(0));
    Assert.assertEquals("127.0.0.2:100", b_log.logAt(0));
    exchange(a, b);
    a_second.run();
    b_second.run();
    Assert.assertEquals("127.0.0.1:100,127.0.0.2:100", a_log.logAt(1));
    Assert.assertEquals("127.0.0.1:100,127.0.0.2:100", b_log.logAt(1));
  }

  @Test
  public void ten_fold() throws Exception {
    MockTime time = new MockTime();
    Engine[] e = new Engine[10];
    Runnable[] a = new Runnable[e.length];
    TargetCollectorAsserter[] l = new TargetCollectorAsserter[e.length];
    {
      for (int k = 0; k < e.length; k++) {
        e[k] = new Engine("127.0.0.1" + k, new GossipMetrics(new NoOpMetricsFactory()), time);
        l[k] = new TargetCollectorAsserter(e.length);
        a[k] = createApp(e[k], "role", 100 + 2 * k, -1);
        a[k].run();
        e[k].subscribe("role", l[k]);
      }
    }
    for(int j = 0; j < 2; j++) {
      for (int k = 0; k < e.length; k++) {
        exchange(e[k], e[(k + 1) % e.length]);
        CountDownLatch ready = new CountDownLatch(2);
        e[k].ready(() -> ready.countDown());
        e[(k + 1) % e.length].ready(() -> ready.countDown());
        Assert.assertTrue(ready.await(1000, TimeUnit.MILLISECONDS));
      }
      for (int k = 0; k < e.length; k++) {
        a[k].run();
      }
      time.currentTime += 100;
    }
    for(int j = 0; j < e.length; j++) {
      l[j].awaitSize();
    }
    e[0].summarizeHtml((str) -> {
      System.err.println(str);
    });
  }

  @Test
  public void ten_random_converge() throws Exception {
    MockTime time = new MockTime();
    Engine[] e = new Engine[10];
    Runnable[] a = new Runnable[e.length];
    TargetCollectorAsserter[] l = new TargetCollectorAsserter[e.length];
    {
      for (int k = 0; k < e.length; k++) {
        e[k] = new Engine("127.0.0.1" + k, new GossipMetrics(new NoOpMetricsFactory()), time);
        l[k] = new TargetCollectorAsserter(e.length);
        a[k] = createApp(e[k], "role", 100 + 2 * k, -1);
        a[k].run();
        e[k].subscribe("role", l[k]);
      }
    }
    Random rng = new Random();
    for (int k = 0; k < e.length; k++) {
      while (l[k].testSize()) {
        int p0 = rng.nextInt(e.length);
        int p1 = p0;
        while (p1 == p0) {
          p0 = rng.nextInt(e.length);
        }
        exchange(e[p0], e[p1]);
      }
    }
    e[0].summarizeHtml((str) -> {
      System.err.println(str);
    });
  }
}

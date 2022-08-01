/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.gossip;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.TimeSource;
import org.adamalang.common.gossip.EngineRole;
import org.adamalang.gossip.proto.Endpoint;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class EngineTests {
  @Test
  public void failuresHappen() throws Exception {
    ArrayList<Engine> engines = new ArrayList<>();

    HashSet<String> initial = new HashSet<>();
    initial.add("127.0.0.1:20000");
    initial.add("127.0.0.1:20005");
    MachineIdentity identity = MachineIdentity.fromFile(prefixForLocalhost());

    Engine app = new Engine(identity, TimeSource.REAL_TIME, initial, 19999, -1, new MockGossipMetrics("app"), EngineRole.Node);
    engines.add(app);
    app.start();

    AtomicReference<TreeSet<String>> values = new AtomicReference<>();

    app.subscribe(
        "app",
        new Consumer<Collection<String>>() {
          @Override
          public void accept(Collection<String> strings) {
            values.set(new TreeSet<>(strings));
          }
        });
    AtomicReference<Runnable> appHeartBeat = new AtomicReference<>();
    CountDownLatch latchForSet = new CountDownLatch(1);
    app.newApp(
        "app",
        4242, runnable -> {
          appHeartBeat.set(runnable);
          latchForSet.countDown();
          runnable.run();
        });

    latchForSet.await(1000, TimeUnit.MILLISECONDS);
    Engine lateEngine = null;
    for (int k = 0; k < 10; k++) {
      Engine engine =
          new Engine(identity, TimeSource.REAL_TIME, initial, 20000 + k, -1, new MockGossipMetrics("k:" + k), EngineRole.Node);
      engines.add(engine);
      if (k == 5) {
        lateEngine = engine;
      } else {
        engine.start();
      }
    }
    int versionCount = 100;
    for (int k = 0; k < 30 && versionCount > 1; k++) {
      appHeartBeat.get().run();
      HashSet<String> versions = new HashSet<>();
      CountDownLatch latch = new CountDownLatch(engines.size());
      for (Engine engine : engines) {
        engine.hash(
            (hash) -> {
              versions.add(hash);
              latch.countDown();
            });
      }
      if (k == 15) {
        lateEngine.start();
      }
      latch.await(5000, TimeUnit.MILLISECONDS);
      versionCount = versions.size();
      System.err.println(versionCount);
      Thread.sleep(1000);
    }
    CountDownLatch gotHtml = new CountDownLatch(1);
    engines.get(0).summarizeHtml(new Consumer<String>() {
      @Override
      public void accept(String s) {
        System.err.println(s);
        gotHtml.countDown();
      }
    });
    Assert.assertTrue(gotHtml.await(5000, TimeUnit.MILLISECONDS));
    Assert.assertEquals(1, versionCount);
    for (int k = 1; k < engines.size(); k++) {
      engines.get(k).close();
    }
    int timeout = 1000;
    CountDownLatch latchClean = new CountDownLatch(1);
    while (!latchClean.await(1000, TimeUnit.MILLISECONDS)) {
      engines.get(0).size(new Consumer<Integer>() {
        @Override
        public void accept(Integer sz) {
          if (sz == 1) {
            latchClean.countDown();
          } else {
            System.err.println("SIZE@" + sz);
          }
        }
      });
      timeout--;
      if (timeout < 0) {
        Assert.fail("failed to clean up");
      }
    }
    engines.get(0).close();
  }

  @Test
  public void summarize() throws Exception {
    HashSet<String> initial = new HashSet<>();
    initial.add("127.0.0.1:20000");
    initial.add("127.0.0.1:20005");
    MachineIdentity identity = MachineIdentity.fromFile(prefixForLocalhost());
    Engine app = new Engine(identity, TimeSource.REAL_TIME, initial, 19999, -1, new MockGossipMetrics("app"), EngineRole.Node);
    app.start();
    CountDownLatch latch = new CountDownLatch(1);
    app.summarizeHtml(new Consumer<String>() {
      @Override
      public void accept(String s) {
        latch.countDown();
      }
    });
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
  }

  private String prefixForLocalhost() {
    for (String search : new String[] {"./", "../", "./grpc/"}) {
      String candidate = search + "localhost.identity";
      File file = new File(candidate);
      if (file.exists()) {
        return candidate;
      }
    }
    throw new NullPointerException("could not find identity.localhost");
  }

  @Test
  public void empty() throws Exception {
    HashSet<String> initial = new HashSet<>();
    MachineIdentity identity = MachineIdentity.fromFile(prefixForLocalhost());
    CountDownLatch latch = new CountDownLatch(2);
    MockGossipMetrics metrics = new MockGossipMetrics() {
      @Override
      public void wake() {
        latch.countDown();
        super.wake();
      }
    };
    Engine app = new Engine(identity, TimeSource.REAL_TIME, initial, 19999, -1, metrics, EngineRole.Node);
    app.start();
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
    app.close();
  }

  @Test
  public void convergence10() throws Exception {
    ArrayList<Engine> engines = new ArrayList<>();

    HashSet<String> initial = new HashSet<>();
    initial.add("127.0.0.1:20000");
    initial.add("127.0.0.1:20009");
    MachineIdentity identity = MachineIdentity.fromFile(prefixForLocalhost());

    Engine app = new Engine(identity, TimeSource.REAL_TIME, initial, 19999, -1, new MockGossipMetrics("app"), EngineRole.SuperNode);
    engines.add(app);
    CountDownLatch latchForWatchingUpdate = new CountDownLatch(1);
    app.setWatcher(new Consumer<Collection<Endpoint>>() {
      @Override
      public void accept(Collection<Endpoint> endpoints) {
        if (endpoints.size() >= 11) {
          latchForWatchingUpdate.countDown();
        }
      }
    });
    app.start();

    AtomicReference<TreeSet<String>> values = new AtomicReference<>();
    CountDownLatch latchForBroadcast = new CountDownLatch(1);
    app.subscribe(
        "app",
        new Consumer<Collection<String>>() {
          @Override
          public void accept(Collection<String> strings) {
            values.set(new TreeSet<>(strings));
            latchForBroadcast.countDown();
          }
        });
    AtomicReference<Runnable> appHeartBeat = new AtomicReference<>();
    CountDownLatch latchForSet = new CountDownLatch(1);
    app.newApp(
        "app",
        4242, runnable -> {
          appHeartBeat.set(runnable);
          latchForSet.countDown();
          runnable.run();
        });

    latchForSet.await(1000, TimeUnit.MILLISECONDS);
    for (int k = 0; k < 10; k++) {
      Engine engine =
          new Engine(identity, TimeSource.REAL_TIME, initial, 20000 + k, -1, new MockGossipMetrics("k:" + k), EngineRole.Node);
      engines.add(engine);
      engine.start();
    }
    int versionCount = 100;
    for (int k = 0; k < 20 && versionCount > 1; k++) {
      appHeartBeat.get().run();
      HashSet<String> versions = new HashSet<>();
      CountDownLatch latch = new CountDownLatch(engines.size());
      for (Engine engine : engines) {
        engine.hash(
            (hash) -> {
              versions.add(hash);
              latch.countDown();
            });
      }
      latch.await(5000, TimeUnit.MILLISECONDS);
      versionCount = versions.size();
      Thread.sleep(1000);
    }
    Assert.assertTrue(latchForWatchingUpdate.await(100000, TimeUnit.MILLISECONDS));
    Assert.assertEquals(1, versionCount);
    Assert.assertTrue(latchForBroadcast.await(2500, TimeUnit.MILLISECONDS));
    // this shutdown is very noisy
    for (Engine engine : engines) {
      engine.close();
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.gossip;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.TimeSource;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class EngineTests {
  @Test
  public void convergence10() throws Exception {
    ArrayList<Engine> engines = new ArrayList<>();

    HashSet<String> initial = new HashSet<>();
    initial.add("127.0.0.1:20000");
    initial.add("127.0.0.1:20009");
    MachineIdentity identity = MachineIdentity.fromFile(prefixForLocalhost());

    Engine app = new Engine(identity, TimeSource.REAL_TIME, initial, 19999, new MockMetrics("app"));
    engines.add(app);
    app.start();

    AtomicReference<Runnable> appHeartBeat = new AtomicReference<>();
    CountDownLatch latchForSet = new CountDownLatch(1);
    app.newApp(
        "app",
        4242,
        new Consumer<Runnable>() {
          @Override
          public void accept(Runnable runnable) {
            appHeartBeat.set(runnable);
            latchForSet.countDown();
            runnable.run();
          }
        });

    latchForSet.await(1000, TimeUnit.MILLISECONDS);
    for (int k = 0; k < 10; k++) {
      Engine engine =
          new Engine(identity, TimeSource.REAL_TIME, initial, 20000 + k, new MockMetrics("k:" + k));
      engines.add(engine);
      engine.start();
    }
    int versionCount = 100;
    for (int k = 0; k < 10 && versionCount > 1; k++) {
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
      System.err.println("ROUND:" + versions.size());
    }
    // this shutdown is very noisy
    for (Engine engine : engines) {
      engine.close();
    }
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
}

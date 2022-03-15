/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.disk.mocks.FilteredSimpleExecutor;
import org.adamalang.disk.mocks.SimpleMockCallback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

public class StartupTests {

  private static final RemoteDocumentUpdate INIT = new RemoteDocumentUpdate(1, 1, NtClient.NO_ONE, "REQUEST", "{\"x\":1,\"y\":4}", "{\"x\":0,\"y\":0}", false, 0, 100, UpdateType.AddUserData);

  private static RemoteDocumentUpdate UPDATE(int n) {
    return new RemoteDocumentUpdate(n, n, null, "REQUEST", "{\"x\":" + n + "}", "{\"x\":" + (n - 1) + "}", false, 0, 100, UpdateType.AddUserData);
  }

  public static void nuke(File root) {
    for (File child : root.listFiles()) {
      System.err.println(child.getAbsolutePath());
      if (child.isDirectory()) {
        nuke(child);
      } else {
        child.delete();
      }
    }
    root.delete();
  }

  @Test
  public void startup_resurrects() throws Exception {
    {
      File root = new File(File.createTempFile("ADAMATEST_", "SUFF").getParentFile(), "wal_test_" + System.currentTimeMillis());
      root.mkdir();
      SimpleExecutor realExecutor = SimpleExecutor.create("real");
      try {

        {
          SimpleExecutor executor = new FilteredSimpleExecutor(realExecutor) {
            @Override
            public boolean test(NamedRunnable runnable) {
              return !"flushing-document".equals(runnable.name);
            }
          };
          DiskBase base = new DiskBase(new DiskDataMetrics(new NoOpMetricsFactory()), executor, root);
          WriteAheadLog log = new WriteAheadLog(base, 1024, 5000, 16 * 1024);
          DiskDataService service = new DiskDataService(base, log);
          int keys = 10;
          {
            ArrayList<SimpleMockCallback> callbacks = new ArrayList<>();
            for (int k = 0; k < keys; k++) {
              SimpleMockCallback initcb = new SimpleMockCallback();
              callbacks.add(initcb);
              service.initialize(new Key("space", "key-" + k), INIT, initcb);
            }
            for (SimpleMockCallback callback : callbacks) {
              callback.assertSuccess();
            }
          }
          for (int n = 2; n < 100; n++) {
            ArrayList<SimpleMockCallback> callbacks = new ArrayList<>();
            for (int k = 0; k < keys; k++) {
              SimpleMockCallback patch = new SimpleMockCallback();
              callbacks.add(patch);
              service.patch(new Key("space", "key-" + k), new RemoteDocumentUpdate[]{UPDATE(n)}, patch);
            }
            for (SimpleMockCallback callback : callbacks) {
              callback.assertSuccess();
            }
          }
          log.close().run();
        }

        System.err.println(root.getAbsolutePath());
        Assert.assertEquals(0, new File(new File(root, "data"), "space").listFiles().length);

        {
          DiskBase base = new DiskBase(new DiskDataMetrics(new NoOpMetricsFactory()), realExecutor, root);
          Startup.transfer(base);
        }

        {
          DiskBase base = new DiskBase(new DiskDataMetrics(new NoOpMetricsFactory()), realExecutor, root);
          DocumentMemoryLog log = base.getOrCreate(new Key("space", "key-5"));
          log.ensureLoaded(new SimpleMockCallback());
          Assert.assertEquals("{\"x\":99,\"y\":4}", log.get().patch);
          Assert.assertEquals("{\"x\":24}", log.computeRewind(25));
          Assert.assertEquals("{\"x\":49}", log.computeRewind(50));
          Assert.assertEquals("{\"x\":74}", log.computeRewind(75));
        }

      } finally {
        nuke(root);
      }
    }
  }
}

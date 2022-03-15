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
import org.adamalang.disk.mocks.SimpleMockCallback;
import org.adamalang.disk.wal.WriteAheadMessage;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WriteAheadLogTests {

  public static final RemoteDocumentUpdate INIT =
      new RemoteDocumentUpdate(
          1, 1, NtClient.NO_ONE, "REQUEST", "{\"x\":1,\"y\":4}", "{\"x\":0,\"y\":0}", false, 0, 100, UpdateType.AddUserData);

  public static RemoteDocumentUpdate UPDATE(int n) {
    return new RemoteDocumentUpdate(
        n,
        n,
        null,
        "REQUEST",
        "{\"x\":" + n + "}",
        "{\"x\":" + (n-1) + "}",
        false,
        0,
        100,
        UpdateType.AddUserData);
  }

  public static WriteAheadMessage.Patch patch(int n) {
    WriteAheadMessage.Patch patch = new WriteAheadMessage.Patch();
    patch.key = "key";
    patch.space = "space";
    patch.changes = new WriteAheadMessage.Change[] { new WriteAheadMessage.Change() };
    patch.changes[0].copyFrom(UPDATE(n));
    return patch;
  }


  @Test
  public void battery() throws Exception {
    File file = new File(File.createTempFile("ADAMATEST_", "suffix").getParentFile(), "base_"+ System.currentTimeMillis());
    DiskBase base = new DiskBase(new DiskDataMetrics(new NoOpMetricsFactory()), SimpleExecutor.create("executor"), file);
    try {
      WriteAheadLog log = new WriteAheadLog(base, 8196, 1000000, 64 * 1024);
      WriteAheadMessage.Initialize initialize = new WriteAheadMessage.Initialize();
      initialize.space = "space";
      initialize.key = "key";
      initialize.initialize = new WriteAheadMessage.Change();

      ArrayList<SimpleMockCallback> callbacks = new ArrayList<>();
      {
        SimpleMockCallback callbackInit = new SimpleMockCallback();
        callbacks.add(callbackInit);
        base.executor.execute(new NamedRunnable("enqueue") {
          @Override
          public void execute() throws Exception {
            base.executor.execute(new NamedRunnable("enqueue") {
              @Override
              public void execute() throws Exception {
                log.write(initialize, callbackInit);
              }
            });
          }
        });
      }
      for (int k = 0; k < 1024 * 64; k++) {
        SimpleMockCallback callback = new SimpleMockCallback();
        callbacks.add(callback);
        final int j = 2 + k;
        base.executor.execute(new NamedRunnable("enqueue") {
          @Override
          public void execute() throws Exception {
            log.write(patch(j), callback);
          }
        });
      }
      log.close().run();
      for (SimpleMockCallback callback : callbacks) {
        callback.assertSuccess();
      }
      SimpleMockCallback failure = new SimpleMockCallback();
      base.executor.execute(new NamedRunnable("enqueue") {
        @Override
        public void execute() throws Exception {
          log.write(patch(0), failure);
        }
      });
      failure.assertFailure(703539);
    } finally {
      base.shutdown();
    }
  }
}

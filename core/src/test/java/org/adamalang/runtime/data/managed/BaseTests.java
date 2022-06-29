/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data.managed;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.data.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BaseTests {
  @FunctionalInterface
  public static interface ThrowConsumer<T> {
    public void run(T item) throws Exception;
  }

  @Test
  public void coverage() throws Exception {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      MockArchiveDataSource data = new MockArchiveDataSource(new InMemoryDataService(executor, TimeSource.REAL_TIME));
      flow((base) -> {
        CountDownLatch latch = new CountDownLatch(2);
        base.on(new Key("space", "key"), (machine) -> {
          latch.countDown();
        });
        base.on(new Key("space", "key"), (machine) -> {
          latch.countDown();
        });
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
      }, data);
    } finally {
      executor.shutdown();
    }
  }

  public static void flow(ThrowConsumer<Base> body, ArchivingDataService data) throws Exception {
    MockFinderService mockFinder = new MockFinderService();
    mockFinder.bindLocal(new Key("space", "key"));
    SimpleExecutor bexecutor = SimpleExecutor.create("executor");
    Base base = new Base(mockFinder, data, "test-region", "test-machine", bexecutor, 1000);
    try {
      body.run(base);
    } finally {
      bexecutor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
    while (base.reportFailureGetRetryBackoff() < 2000) {
    }
    for (int k = 0; k < 1000; k++) {
      base.reportSuccess();
    }
  }
}

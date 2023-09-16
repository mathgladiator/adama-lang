/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.data.managed;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.data.mocks.MockArchiveDataSource;
import org.adamalang.runtime.data.mocks.MockFinderService;
import org.adamalang.runtime.data.mocks.MockPostDocumentDelete;
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
    MockFinderService mockFinder = new MockFinderService("test-machine");
    mockFinder.bindLocal(new Key("space", "key"));
    MockPostDocumentDelete delete = new MockPostDocumentDelete();
    SimpleExecutor bexecutor = SimpleExecutor.create("executor");
    Base base = new Base(mockFinder, data, delete, "test-region", "test-machine", bexecutor, 1000);
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

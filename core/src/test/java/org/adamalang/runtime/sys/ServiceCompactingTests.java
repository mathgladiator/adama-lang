/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.DocumentSnapshot;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.data.LocalDocumentChange;
import org.adamalang.runtime.mocks.MockBackupService;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.mocks.MockWakeService;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceCompactingTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());

  private static final Key KEY = new Key("space", "key");
  private static final String SUPER_COMPACT =
      "@static { create { return true; } maximum_history = 0; } public int x; public asset a; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String MODERATE_COMPACT =
      "@static { create { return true; } maximum_history = 10; } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }";

  @Test
  public void super_compact() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SUPER_COMPACT, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    InMemoryDataService dataService = new InMemoryDataService(executor, TimeSource.REAL_TIME);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      for (int k = 0; k < 100; k++) {
        LatchCallback cb1 = new LatchCallback();
        streamback.get().send("foo", null, "{}", cb1);
        cb1.await_success(5 + k);
      }
      AtomicInteger reads = new AtomicInteger(100);
      int attempts = 25;
      while (reads.get() > 1 && attempts > 0) {
        attempts--;
        CountDownLatch latch = new CountDownLatch(1);
        dataService.get(KEY, new Callback<LocalDocumentChange>() {
          @Override
          public void success(LocalDocumentChange value) {
            reads.set(value.reads);
            executor.execute(() -> {
              latch.countDown();
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
          }
        });
        Assert.assertTrue(latch.await(2500, TimeUnit.MILLISECONDS));
        Thread.sleep(500);
      }
    } finally {
      service.shutdown();
      executor.shutdown();
    }
  }

  @Test
  public void moderate_compact() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(MODERATE_COMPACT, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    InMemoryDataService dataService = new InMemoryDataService(executor, TimeSource.REAL_TIME);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      for (int k = 0; k < 100; k++) {
        LatchCallback cb1 = new LatchCallback();
        streamback.get().send("foo", null, "{}", cb1);
        cb1.await_success(5 + k);
      }
      AtomicInteger reads = new AtomicInteger(100);
      int attempts = 25;
      while (reads.get() > 20 && attempts > 0) {
        attempts--;
        CountDownLatch latch = new CountDownLatch(1);
        dataService.get(KEY, new Callback<LocalDocumentChange>() {
          @Override
          public void success(LocalDocumentChange value) {
            System.err.println("READS:" + value.reads);
            reads.set(value.reads);
            executor.execute(() -> {
              latch.countDown();
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
          }
        });
        Assert.assertTrue(latch.await(2500, TimeUnit.MILLISECONDS));
        Thread.sleep(500);
      }
    } finally {
      service.shutdown();
      executor.shutdown();
    }
  }


  @Test
  public void compact_turned_off_and_on() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(MODERATE_COMPACT, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    AtomicBoolean compactOn = new AtomicBoolean(false);
    InMemoryDataService dataService = new InMemoryDataService(executor, TimeSource.REAL_TIME) {
      @Override
      public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
        if (compactOn.get()) {
          super.snapshot(key, snapshot, callback);
          return;
        }
        callback.failure(new ErrorCodeException(-1));
      }
    };
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      for (int k = 0; k < 100; k++) {
        LatchCallback cb1 = new LatchCallback();
        streamback.get().send("foo", null, "{}", cb1);
        cb1.await_success(5 + k);
      }
      CountDownLatch nothingHappened = new CountDownLatch(1);
      dataService.get(KEY, new Callback<LocalDocumentChange>() {
        @Override
        public void success(LocalDocumentChange value) {
          Assert.assertEquals(103, value.reads);
          executor.execute(() -> {
            nothingHappened.countDown();
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
        }
      });
      Assert.assertTrue(nothingHappened.await(5000, TimeUnit.MILLISECONDS));
      compactOn.set(true);
      {
        LatchCallback cb1 = new LatchCallback();
        streamback.get().send("foo", null, "{}", cb1);
        cb1.await_success(105);
      }
      AtomicInteger reads = new AtomicInteger(100);
      int attempts = 25;
      while (reads.get() > 20 && attempts > 0) {
        attempts--;
        CountDownLatch latch = new CountDownLatch(1);
        dataService.get(KEY, new Callback<LocalDocumentChange>() {
          @Override
          public void success(LocalDocumentChange value) {
            System.err.println("READS:" + value.reads);
            reads.set(value.reads);
            executor.execute(() -> {
              latch.countDown();
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
          }
        });
        Assert.assertTrue(latch.await(2500, TimeUnit.MILLISECONDS));
        Thread.sleep(500);
      }
    } finally {
      service.shutdown();
      executor.shutdown();
    }
  }
}

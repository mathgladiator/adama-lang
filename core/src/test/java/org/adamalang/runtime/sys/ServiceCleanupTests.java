/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.runtime.sys.web.WebContext;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebResponse;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceCleanupTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@static { create { return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; } @web get / { return {html:\"Hi\"}; } ";

  @Test
  public void cleanup_happens() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockTime time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    AtomicReference<CountDownLatch> latchMade = new AtomicReference<>(null);
    CountDownLatch latchSet = new CountDownLatch(1);
    CoreService service = new CoreService(METRICS, factoryFactory, (samples) -> {
      PredictiveInventory.MeteringSample meteringSample = samples.get(KEY.space);
      if (meteringSample != null) {
        if (meteringSample.count == 1) {
          if (latchMade.get() == null) {
            latchMade.set(new CountDownLatch(1));
            latchSet.countDown();
          }
        } else if (meteringSample.count == 0) {
          if (latchMade.get() != null) {
            latchMade.get().countDown();
          }
        }
        System.err.println(meteringSample.count);
      }
    }, dataService, time, 3);
    service.tune(
        (base) -> {
          base.setInventoryMillisecondsSchedule(1000, 50);
        });
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback.get(1));
      TreeMap<String, String> query = new TreeMap<>();
      CountDownLatch latchQuery = new CountDownLatch(1);
      query.put("space", KEY.space);
      query.put("key", KEY.key);
      AtomicReference<String> queryResult = new AtomicReference<>();
      service.query(query, new Callback<String>() {
        @Override
        public void success(String value) {
          queryResult.set(value);
          latchQuery.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(latchQuery.await(5000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(queryResult.get().startsWith("{\"space\":\"space\",\"key\":\"key\""));
      LatchCallback cb1 = new LatchCallback();
      streamback.get().send("foo", null, "{}", cb1);
      cb1.await_success(5);
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":5}", streamback.get(2));
      streamback.get().close();
      latch3.run();
      Assert.assertEquals("STATUS:Disconnected", streamback.get(3));
      Assert.assertTrue(latchSet.await(5000, TimeUnit.MILLISECONDS));
      while (!latchMade.get().await(1000, TimeUnit.MILLISECONDS)) {
        time.time += 10000;
      }
      Thread.sleep(1000);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cleanup_happens_just_load() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockTime time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    AtomicReference<CountDownLatch> latchMade = new AtomicReference<>(null);
    CountDownLatch latchSet = new CountDownLatch(1);
    Runnable dataLatch = dataService.latchLogAt(3);
    CoreService service = new CoreService(METRICS, factoryFactory, (samples) -> {
      time.time += 1000;
      PredictiveInventory.MeteringSample meteringSample = samples.get(KEY.space);
      if (meteringSample != null) {
        if (meteringSample.count == 1) {
          if (latchMade.get() == null) {
            latchMade.set(new CountDownLatch(1));
            latchSet.countDown();
          }
        } else if (meteringSample.count == 0) {
          if (latchMade.get() != null) {
            latchMade.get().countDown();
          }
        }
        System.err.println(meteringSample.count);
      }
    }, dataService, time, 3);
    service.tune(
        (base) -> {
          base.setInventoryMillisecondsSchedule(250, 50);
          base.setMillisecondsInactivityBeforeCleanup(25);
        });
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      CountDownLatch got = new CountDownLatch(1);
      service.webGet(KEY, new WebGet(new WebContext(NtPrincipal.NO_ONE, "Origin", "1.2.3.4"), "/", new TreeMap<>(), new NtDynamic("{}")), new Callback<WebResponse>() {
        @Override
        public void success(WebResponse value) {
          got.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(got.await(5000, TimeUnit.MILLISECONDS));
      dataLatch.run();
      dataService.assertLogAtStartsWith(0, "INIT:space/key:");
      dataService.assertLogAt(1, "LOAD:space/key");
      dataService.assertLogAt(2, "CLOSE:space/key");
    } finally {
      service.shutdown();
    }
  }

}

package org.adamalang.runtime.sys;

import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceCleanupTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@can_create(who) { return true; } public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String SIMPLE_CODE_ATTACH =
      "@can_create(who) { return true; } public int x; @connected(who) { x = 42; return who == @no_one; } @can_attach(who) { return true; } @attached (who, a) { x++; } ";

  @Test
  public void cleanup_happens() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockTime time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    AtomicReference<CountDownLatch> latchMade = new AtomicReference<>(null);
    CountDownLatch latchSet = new CountDownLatch(1);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {
      PredictiveInventory.Billing billing = bill.get(KEY.space);
      if (billing != null) {
        if (billing.count == 1) {
          if (latchMade.get() == null) {
            latchMade.set(new CountDownLatch(1));
            latchSet.countDown();
          }
        } else if (billing.count == 0) {
          if (latchMade.get() != null) {
            latchMade.get().countDown();
          }
        }
        System.err.println(billing.count);
      }
    }, dataService, time, 3);
    service.tune(
        (base) -> {
          base.setInventoryMillisecondsSchedule(1000, 50);
        });
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback.get(1));
      LatchCallback cb1 = new LatchCallback();
      streamback.get().send("foo", null, "{}", cb1);
      cb1.await_success(6);
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":6}", streamback.get(2));
      streamback.get().disconnect();
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

}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class ServiceMultiboxTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final NtClient ALICE = new NtClient("alice", "test");
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@static { create(who) { return true; } } public int x; @connected(who) { x += 1; return true; } @disconnected(who) { x -= 1; } message M {} channel foo(M y) { x += 1000; }";

  @Test
  public void seq_conflict_at_data_service() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    service.tune(
        (base) -> {
          base.setMillisecondsForCleanupCheck(25);
          base.setMillisecondsAfterLoadForReconciliation(10000);
        });
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ALICE, KEY, "{}", "1", created);
      created.await_success();
      MockStreamback streamback1 = new MockStreamback();
      Runnable latch1 = streamback1.latchAt(7);
      service.connect(ALICE, KEY, streamback1);
      streamback1.await_began();
      realDataService.skipAt(5);
      LatchCallback cb1 = new LatchCallback();
      streamback1.get().send("foo", null, "{}", cb1);
      cb1.await_success(10003);
      realDataService.skipAt(10004);
      LatchCallback cb2 = new LatchCallback();
      streamback1.get().send("foo", null, "{}", cb2);
      cb2.await_success(20003);
      realDataService.skipAt(20004);
      realDataService.killSkip();
      LatchCallback cb3 = new LatchCallback();
      streamback1.get().send("foo", null, "{}", cb3);
      cb3.await_failure(555);
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("{\"data\":{\"x\":1},\"seq\":4}", streamback1.get(1));
      Assert.assertEquals("{\"data\":{\"x\":70000},\"seq\":10001}", streamback1.get(2));
      Assert.assertEquals("{\"data\":{\"x\":71000},\"seq\":10003}", streamback1.get(3));
      Assert.assertEquals("{\"data\":{\"x\":140000},\"seq\":20001}", streamback1.get(4));
      Assert.assertEquals("{\"data\":{\"x\":141000},\"seq\":20003}", streamback1.get(5));
      Assert.assertEquals("STATUS:Disconnected", streamback1.get(6));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void too_many_conflicts() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    service.tune(
        (base) -> {
          base.setMillisecondsForCleanupCheck(25);
          base.setMillisecondsAfterLoadForReconciliation(10000);
        });
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ALICE, KEY, "{}", "1", created);
      created.await_success();
      MockStreamback streamback1 = new MockStreamback();
      Runnable latch1 = streamback1.latchAt(3);
      service.connect(ALICE, KEY, streamback1);
      streamback1.await_began();
      realDataService.infiniteSkip();
      LatchCallback cb1 = new LatchCallback();
      streamback1.get().send("foo", null, "{}", cb1);
      cb1.await_failure(621580);
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("{\"data\":{\"x\":1},\"seq\":4}", streamback1.get(1));
      Assert.assertEquals("STATUS:Disconnected", streamback1.get(2));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void splitBrainMultiBoxSync() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    InMemoryDataService dataService = new InMemoryDataService((r) -> r.run(), time);
    CoreService service1 = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    CoreService service2 = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service1.create(ALICE, KEY, "{}", "1", created);
      created.await_success();

      MockStreamback streamback1 = new MockStreamback();
      Runnable latch1 = streamback1.latchAt(11);
      service1.connect(ALICE, KEY, streamback1);
      streamback1.await_began();

      MockStreamback streamback2 = new MockStreamback();
      Runnable latch2 = streamback2.latchAt(10);
      service2.connect(ALICE, KEY, streamback2);
      streamback2.await_began();

      { // 2000
        // service 1
        LatchCallback cb1 = new LatchCallback();
        streamback1.get().send("foo", null, "{}", cb1);
        cb1.await_success(8);
        // service 2
        LatchCallback cb2 = new LatchCallback();
        streamback2.get().send("foo", null, "{}", cb2);
        cb2.await_success(11);
      }

      { // 4000
        // service 1
        LatchCallback cb1 = new LatchCallback();
        streamback1.get().send("foo", null, "{}", cb1);
        cb1.await_success(14);
        LatchCallback cb2 = new LatchCallback();
        streamback1.get().send("foo", null, "{}", cb2);
        cb2.await_success(16);
      }

      { // 6000
        // service 2
        LatchCallback cb1 = new LatchCallback();
        streamback2.get().send("foo", null, "{}", cb1);
        cb1.await_success(19);
        LatchCallback cb2 = new LatchCallback();
        streamback2.get().send("foo", null, "{}", cb2);
        cb2.await_success(21);
      }
      { // 8000
        // service 1
        LatchCallback cb1 = new LatchCallback();
        streamback1.get().send("foo", null, "{}", cb1);
        cb1.await_success(24);
        // service 2
        LatchCallback cb2 = new LatchCallback();
        streamback2.get().send("foo", null, "{}", cb2);
        cb2.await_success(27);
      }
      { // 10000
        // service 2
        LatchCallback cb2 = new LatchCallback();
        streamback2.get().send("foo", null, "{}", cb2);
        cb2.await_success(29);
        // service 1
        LatchCallback cb1 = new LatchCallback();
        streamback1.get().send("foo", null, "{}", cb1);
        cb1.await_success(32);
      }
      latch1.run();
      latch2.run();

      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("{\"data\":{\"x\":1},\"seq\":4}", streamback1.get(1));
      Assert.assertEquals("{\"data\":{\"x\":1},\"seq\":6}", streamback1.get(2));
      Assert.assertEquals("{\"data\":{\"x\":1001},\"seq\":8}", streamback1.get(3));
      Assert.assertEquals("{\"seq\":12}", streamback1.get(4));
      Assert.assertEquals("{\"data\":{\"x\":3001},\"seq\":14}", streamback1.get(5));
      Assert.assertEquals("{\"data\":{\"x\":4001},\"seq\":16}", streamback1.get(6));
      Assert.assertEquals("{\"data\":{\"x\":6001},\"seq\":22}", streamback1.get(7));
      Assert.assertEquals("{\"data\":{\"x\":7001},\"seq\":24}", streamback1.get(8));
      Assert.assertEquals("{\"data\":{\"x\":9001},\"seq\":30}", streamback1.get(9));
      Assert.assertEquals("{\"data\":{\"x\":10001},\"seq\":32}", streamback1.get(10));
      // last write got to 10000, which is what we expect

      Assert.assertEquals("STATUS:Connected", streamback2.get(0));
      Assert.assertEquals("{\"data\":{\"x\":1},\"seq\":5}", streamback2.get(1));
      Assert.assertEquals("{\"seq\":9}", streamback2.get(2));
      Assert.assertEquals("{\"data\":{\"x\":2001},\"seq\":11}", streamback2.get(3));
      Assert.assertEquals("{\"data\":{\"x\":4001},\"seq\":17}", streamback2.get(4));
      Assert.assertEquals("{\"data\":{\"x\":5001},\"seq\":19}", streamback2.get(5));
      Assert.assertEquals("{\"data\":{\"x\":6001},\"seq\":21}", streamback2.get(6));
      Assert.assertEquals("{\"seq\":25}", streamback2.get(7));
      Assert.assertEquals("{\"data\":{\"x\":8001},\"seq\":27}", streamback2.get(8));
      Assert.assertEquals("{\"data\":{\"x\":9001},\"seq\":29}", streamback2.get(9));
      // NOTE: the second server hasn't caught up yet
    } finally {
      service1.shutdown();
      service2.shutdown();
    }
  }
}

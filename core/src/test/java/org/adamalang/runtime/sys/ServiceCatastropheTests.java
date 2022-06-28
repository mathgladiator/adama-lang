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
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class ServiceCatastropheTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final NtClient ALICE = new NtClient("alice", "test");
  private static final NtClient BOB = new NtClient("bob", "test");
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@static { create { return true; } } public int x; @connected { x += 1; return true; } @disconnected { x -= 1; } message M {} channel foo(M y) { x += 1000; }";
  private static final String SIMPLE_CODE_MSG_DELETES =
      "@static { create { return true; } delete_on_close = true; } public int x; @connected { x += 1; return true; } @disconnected { x -= 1; } message M {} channel foo(M y) { x += 1000; }";

  @Test
  public void connect_failures_trigger_reload() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockFailureDataService failureDataService = new MockFailureDataService();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      Runnable latch = realDataService.latchLogAt(2);
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", "1", created);
      created.await_success();
      dataService.pause();
      dataService.set(failureDataService);
      dataService.unpause();

      MockStreamback streamback1 = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, streamback1);
      streamback1.await_failure(999);

      dataService.pause();
      dataService.set(realDataService);
      dataService.unpause();

      MockStreamback streamback2 = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, streamback2);
      streamback2.await_began();
      latch.run();
      realDataService.assertLogAt(0, "INIT:space/key:1->{\"__constructed\":true,\"__entropy\":\"-4964420948893066024\",\"__messages\":null,\"__seq\":1}");
      realDataService.assertLogAt(1, "LOAD:space/key");
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void failure_triggers_delete_based_on_config() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_DELETES);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockFailureDataService failureDataService = new MockFailureDataService();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    service.tune(
        (base) -> {
          base.setMillisecondsAfterLoadForReconciliation(250);
        });
    try {
      Runnable latch = realDataService.latchLogAt(5);
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(ALICE), KEY, "{}", "1", created);
      created.await_success();
      MockStreamback streamback1 = new MockStreamback();
      Runnable latch1 = streamback1.latchAt(3);
      service.connect(ContextSupport.WRAP(ALICE), KEY, "{}", null, streamback1);
      streamback1.await_began();
      dataService.pause();
      dataService.set(failureDataService);
      dataService.unpause();
      LatchCallback callback1 = new LatchCallback();
      streamback1.get().send("foo", null, "{}", callback1);
      callback1.await_failure(999);
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("{\"data\":{\"x\":1},\"seq\":4}", streamback1.get(1));
      Assert.assertEquals("STATUS:Disconnected", streamback1.get(2));
      dataService.pause();
      dataService.set(realDataService);
      dataService.unpause();

      MockStreamback streamback2 = new MockStreamback();
      service.connect(ContextSupport.WRAP(BOB), KEY, "{}", null, streamback2);
      streamback2.await_failure(625676);
      latch.run();
      realDataService.assertLogAt(0, "INIT:space/key:1->{\"__constructed\":true,\"__entropy\":\"-4964420948893066024\",\"__messages\":null,\"__seq\":1}");
      realDataService.assertLogAt(1, "LOAD:space/key");
      realDataService.assertLogAt(2, "PATCH:space/key:2-3->{\"__seq\":3,\"__entropy\":\"-6153234687710755147\",\"__connection_id\":1,\"x\":1,\"__clients\":{\"0\":{\"agent\":\"alice\",\"authority\":\"test\"}},\"__messages\":null}");
      realDataService.assertLogAt(3, "PATCH:space/key:4-4->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"6497997367891420869\"}");
      realDataService.assertLogAt(4, "DELETE:space/key");
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void send_failure_disconnects_and_reconcile() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockFailureDataService failureDataService = new MockFailureDataService();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    service.tune(
        (base) -> {
          base.setMillisecondsAfterLoadForReconciliation(250);
        });
    try {
      Runnable latch = realDataService.latchLogAt(9);
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(ALICE), KEY, "{}", "1", created);
      created.await_success();
      MockStreamback streamback1 = new MockStreamback();
      Runnable latch1 = streamback1.latchAt(3);
      service.connect(ContextSupport.WRAP(ALICE), KEY, "{}", null, streamback1);
      streamback1.await_began();
      dataService.pause();
      dataService.set(failureDataService);
      dataService.unpause();
      LatchCallback callback1 = new LatchCallback();
      streamback1.get().send("foo", null, "{}", callback1);
      callback1.await_failure(999);
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("{\"data\":{\"x\":1},\"seq\":4}", streamback1.get(1));
      Assert.assertEquals("STATUS:Disconnected", streamback1.get(2));
      dataService.pause();
      dataService.set(realDataService);
      dataService.unpause();

      MockStreamback streamback2 = new MockStreamback();
      Runnable latch2 = streamback2.latchAt(3);
      service.connect(ContextSupport.WRAP(BOB), KEY, "{}", null, streamback2);
      streamback2.await_began();
      latch2.run();
      Assert.assertEquals("STATUS:Connected", streamback2.get(0));
      Assert.assertEquals("{\"data\":{\"x\":2},\"seq\":7}", streamback2.get(1));
      Assert.assertEquals("{\"data\":{\"x\":1},\"seq\":9}", streamback2.get(2));
      latch.run();
      realDataService.assertLogAt(0, "INIT:space/key:1->{\"__constructed\":true,\"__entropy\":\"-4964420948893066024\",\"__messages\":null,\"__seq\":1}");
      realDataService.assertLogAt(1, "LOAD:space/key");
      realDataService.assertLogAt(2, "PATCH:space/key:2-3->{\"__seq\":3,\"__entropy\":\"-6153234687710755147\",\"__connection_id\":1,\"x\":1,\"__clients\":{\"0\":{\"agent\":\"alice\",\"authority\":\"test\"}},\"__messages\":null}");
      realDataService.assertLogAt(3, "PATCH:space/key:4-4->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"6497997367891420869\"}");
      realDataService.assertLogAt(4, "CLOSE:space/key");
      realDataService.assertLogAt(5, "LOAD:space/key");
      realDataService.assertLogAt(6, "PATCH:space/key:5-6->{\"__seq\":6,\"__entropy\":\"-7509292263826677178\",\"__connection_id\":2,\"x\":2,\"__clients\":{\"1\":{\"agent\":\"bob\",\"authority\":\"test\"}},\"__messages\":null}");
      realDataService.assertLogAt(7, "PATCH:space/key:7-7->{\"__messages\":null,\"__seq\":7,\"__entropy\":\"3749908817274402468\"}");
      realDataService.assertLogAt(8, "PATCH:space/key:8-9->{\"__seq\":9,\"__entropy\":\"3155693776702036113\",\"x\":1,\"__clients\":{\"0\":null},\"__messages\":null}");
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void send_many() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockFailureDataService failureDataService = new MockFailureDataService();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    service.tune(
        (base) -> {
          base.setMillisecondsAfterLoadForReconciliation(250);
        });
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(ALICE), KEY, "{}", "1", created);
      created.await_success();
      MockStreamback streamback1 = new MockStreamback();
      Runnable latch1 = streamback1.latchAt(3);
      service.connect(ContextSupport.WRAP(ALICE), KEY, "{}", null, streamback1);
      streamback1.await_began();
      dataService.pause();
      dataService.set(failureDataService);
      LatchCallback callback1 = new LatchCallback();
      streamback1.get().send("foo", null, "{}", callback1);
      LatchCallback callback2 = new LatchCallback();
      streamback1.get().send("foo", null, "{}", callback2);
      LatchCallback callback3 = new LatchCallback();
      streamback1.get().send("foo", null, "{}", callback3);
      dataService.unpause();
      latch1.run();
      callback1.await_failure(999);
      callback2.await_failure(144416);
      callback3.await_failure(144416);
      LatchCallback callback4 = new LatchCallback();
      streamback1.get().send("foo", null, "{}", callback4);
      callback4.await_failure(144416);
      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("{\"data\":{\"x\":1},\"seq\":4}", streamback1.get(1));
      Assert.assertEquals("STATUS:Disconnected", streamback1.get(2));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void reconcile_recovery() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockFailureDataService failureDataService = new MockFailureDataService();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    service.tune(
        (base) -> {
          base.setMillisecondsForCleanupCheck(25);
          base.setMillisecondsAfterLoadForReconciliation(10000);
        });
    try {
      Runnable latch = realDataService.latchLogAt(9);
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(ALICE), KEY, "{}", "1", created);
      created.await_success();
      MockStreamback streamback1 = new MockStreamback();
      Runnable latch1 = streamback1.latchAt(3);
      service.connect(ContextSupport.WRAP(ALICE), KEY, "{}", null, streamback1);
      streamback1.await_began();
      MockStreamback streamback2 = new MockStreamback();
      Runnable latch2 = streamback2.latchAt(3);
      service.connect(ContextSupport.WRAP(BOB), KEY, "{}", null, streamback2);
      streamback2.await_began();
      dataService.pause();
      dataService.set(failureDataService);
      dataService.unpause();
      LatchCallback callback1 = new LatchCallback();
      streamback1.get().send("foo", null, "{}", callback1);
      callback1.await_failure(999);
      latch1.run();
      latch2.run();
      dataService.pause();
      dataService.set(realDataService);
      dataService.unpause();
      MockStreamback streamback3 = new MockStreamback();
      service.connect(ContextSupport.WRAP(BOB), KEY, "{}", null, streamback3);
      streamback3.await_began();
      streamback3.get().disconnect();
      latch.run();
    } finally {
      service.shutdown();
    }
  }
}

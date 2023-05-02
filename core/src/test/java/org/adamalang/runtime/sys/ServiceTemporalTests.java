/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class ServiceTemporalTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@static { create { return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; transition #bump in 0.25; } #bump { x += 1000; transition #end; } #end {} ";
  private static final String SIMPLE_BOUNCER =
      "@static { create { return true; } } public int x; @connected { return true; } @construct { transition #bounce in 0.05; } #bounce { x += 1; transition #bounce in 0.05; } ";

  @Test
  public void transitions_happy_on_time() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, TimeSource.REAL_TIME, 3);
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
      Assert.assertEquals(42, (int) getX(streamback, 1));
      LatchCallback cb1 = new LatchCallback();
      long started = System.currentTimeMillis();
      streamback.get().send("foo", null, "{}", cb1);
      cb1.await_success(5);
      latch2.run();
      Assert.assertEquals(142, (int) getX(streamback, 2));
      latch3.run();
      Assert.assertEquals(1142, (int) getX(streamback, 3));
      long delta = System.currentTimeMillis() - started;
      Assert.assertTrue(delta >= 200);
    } finally {
      service.shutdown();
    }
  }

  private static Integer getX(MockStreamback streamback, int k) {
    String json = streamback.get(k);
    JsonStreamReader reader = new JsonStreamReader(json);
    return (Integer)
        ((HashMap<String, Object>) ((HashMap<String, Object>) reader.readJavaTree()).get("data"))
            .get("x");
  }

  @Test
  public void drive_after_load() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_BOUNCER, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockInstantDataService dataService = new MockInstantDataService();
    Runnable latch = dataService.latchLogAt(10);

    RemoteDocumentUpdate init = new RemoteDocumentUpdate(0, 1, NtPrincipal.NO_ONE, "{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"1\"}", "{\"__state\":\"bounce\",\"__constructed\":true,\"__next_time\":\"50\",\"__entropy\":\"-4964420948893066024\",\"__messages\":null,\"__seq\":1}", "{\"__seq\":0,\"__entropy\":\"-4276096898218380257\",\"__state\":\"\",\"__constructed\":false,\"__next_time\":\"0\"}", true, 0, 0L, UpdateType.AddUserData);
    dataService.initialize(KEY, init, Callback.DONT_CARE_VOID);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, TimeSource.REAL_TIME, 3);
    service.tune((base) -> base.setMillisecondsForCleanupCheck(50));
    try {
      service.startupLoad(KEY);
      latch.run();
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void load_after_create_pause() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, new MockTime(), 3);
    service.tune((base) -> base.setMillisecondsForCleanupCheck(5));
    try {
      Runnable latch = dataService.latchLogAt(10);
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", "1", created);
      created.await_success();
      Thread.sleep(100);
      {
        MockStreamback streamback = new MockStreamback();
        service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
        streamback.await_began();
        streamback.get().close();
      }
      Thread.sleep(100);
      {
        MockStreamback streamback = new MockStreamback();
        service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
        streamback.await_began();
        streamback.get().close();
      }

      latch.run();
      dataService.assertLogAt(0, "INIT:space/key:1->{\"__constructed\":true,\"__entropy\":\"-4964420948893066024\",\"__messages\":null,\"__seq\":1}");
      dataService.assertLogAt(1, "LOAD:space/key");
      dataService.assertLogAt(2, "PATCH:space/key:2-3->{\"__seq\":3,\"__entropy\":\"-6153234687710755147\",\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__messages\":null}");
      dataService.assertLogAt(3, "PATCH:space/key:4-4->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"6497997367891420869\"}");
      dataService.assertLogAt(4, "PATCH:space/key:5-6->{\"__seq\":6,\"__entropy\":\"-7509292263826677178\",\"__clients\":{\"0\":null},\"__messages\":null}");
      dataService.assertLogAt(5, "CLOSE:space/key");
      dataService.assertLogAt(6, "LOAD:space/key");
      dataService.assertLogAt(7, "PATCH:space/key:7-8->{\"__seq\":8,\"__entropy\":\"-3276852164016475938\",\"__connection_id\":2,\"__clients\":{\"1\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__messages\":null}");
      dataService.assertLogAt(8, "PATCH:space/key:9-9->{\"__messages\":null,\"__seq\":9,\"__entropy\":\"3155693776702036113\"}");
    } finally {
      service.shutdown();
    }
  }
}

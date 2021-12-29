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

import org.adamalang.common.Callback;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class ServiceTemporalTests {
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; transition #bump in 0.25; } #bump { x += 1000; transition #end; } #end {} ";
  private static final String SIMPLE_BOUNCER =
      "public int x; @connected(who) { return true; } @construct { transition #bounce in 0.05; } #bounce { x += 1; transition #bounce in 0.05; } ";

  @Test
  public void transitions_happy_on_time() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(factoryFactory, dataService, TimeSource.REAL_TIME, 3);
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
      Assert.assertEquals(42, (int) getX(streamback, 1));
      LatchCallback cb1 = new LatchCallback();
      long started = System.currentTimeMillis();
      streamback.get().send("foo", null, "{}", cb1);
      cb1.await_success(6);
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
  public void transitions_scan_process_start() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_BOUNCER);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    MockFailureDataService failureDataService = new MockFailureDataService();

    realDataService.initialize(
        KEY,
        ServiceConnectTests.wrap(
            "{\"__state\":\"bounce\",\"__constructed\":true,\"__next_time\":\"1637878366533\",\"__time\":\"1637878366483\"}"),
        Callback.DONT_CARE_VOID);
    realDataService.patch(
        KEY,
        ServiceConnectTests.wrap(
            "{\"__messages\":null,\"__seq\":1,\"__entropy\":\"-4776469697456872557\",\"__time\":\"1637878366498\"}"),
        Callback.DONT_CARE_VOID);
    realDataService.ready(KEY);
    Runnable latchPrimed = realDataService.latchLogAt(10);

    CoreService service = new CoreService(factoryFactory, dataService, TimeSource.REAL_TIME, 2);
    try {
      latchPrimed.run();
      dataService.pause();
      dataService.set(failureDataService);
      dataService.unpause();
      MockStreamback streamback = new MockStreamback();
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_any_failure();
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void load_after_create_pause() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(factoryFactory, dataService, new MockTime(), 3);
    service.tune((base) -> base.setMillisecondsForCleanupCheck(5));
    try {
      Runnable latch = dataService.latchLogAt(9);
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", "1", created);
      created.await_success();
      Thread.sleep(100);
      {
        MockStreamback streamback = new MockStreamback();
        service.connect(NtClient.NO_ONE, KEY, streamback);
        streamback.await_began();
        streamback.get().disconnect();
      }
      Thread.sleep(100);
      {
        MockStreamback streamback = new MockStreamback();
        service.connect(NtClient.NO_ONE, KEY, streamback);
        streamback.await_began();
        streamback.get().disconnect();
      }

      latch.run();
      dataService.assertLogAt(0, "INIT:space/key:0->{\"__constructed\":true,\"__entropy\":\"1\"}");
      dataService.assertLogAt(
          1,
          "PATCH:space/key:1->{\"__messages\":null,\"__seq\":1,\"__entropy\":\"-4964420948893066024\"}");
      dataService.assertLogAt(2, "LOAD:space/key");
      dataService.assertLogAt(
          3,
          "PATCH:space/key:2->{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}");
      dataService.assertLogAt(
          4,
          "PATCH:space/key:3->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"323091568684100223\"}");
      dataService.assertLogAt(
          5,
          "PATCH:space/key:4->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"-6153234687710755147\"}");
      dataService.assertLogAt(6, "PATCH:space/key:5->{\"__seq\":5,\"__clients\":{\"0\":null}}");
      dataService.assertLogAt(
          7,
          "PATCH:space/key:6->{\"__messages\":null,\"__seq\":6,\"__entropy\":\"6497997367891420869\"}");
      dataService.assertLogAt(8, "LOAD:space/key");
    } finally {
      service.shutdown();
    }
  }
}

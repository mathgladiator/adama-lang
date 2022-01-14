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
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class ServiceConnectTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@can_create(who) { return true; } public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String SIMPLE_CODE_ATTACH =
      "@can_create(who) { return true; } public int x; @connected(who) { x = 42; return who == @no_one; } @can_attach(who) { return true; } @attached (who, a) { x++; } ";

  @Test
  public void connect_super_happy_connect() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
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
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_super_happy_load() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    dataService.initialize(KEY, wrap("{\"__constructed\":true}"), Callback.DONT_CARE_VOID);
    dataService.patch(
        KEY,
        wrap(
            "{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}"),
        Callback.DONT_CARE_VOID);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":3}", streamback.get(1));
      LatchCallback cb1 = new LatchCallback();
      streamback.get().send("foo", null, "{}", cb1);
      cb1.await_success(5);
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":5}", streamback.get(2));
      streamback.get().disconnect();
      latch3.run();
      Assert.assertEquals("STATUS:Disconnected", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  public static DataService.RemoteDocumentUpdate wrap(String json) {
    JsonStreamReader reader = new JsonStreamReader(json);
    Object obj = reader.readJavaTree();
    Integer seq = (Integer) (((HashMap<String, Object>) obj).get("__seq"));
    return new DataService.RemoteDocumentUpdate(
        seq == null ? 0 : seq, NtClient.NO_ONE, "setup", json, "{}", false, 0);
  }

  @Test
  public void connect_attach_nope() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    dataService.initialize(KEY, wrap("{\"__constructed\":true}"), Callback.DONT_CARE_VOID);
    dataService.patch(
        KEY,
        wrap(
            "{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}"),
        Callback.DONT_CARE_VOID);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":3}", streamback.get(1));
      LatchCallback cb1 = new LatchCallback();
      streamback.get().canAttach(cb1.toBool(-5, 5));
      cb1.await_success(5);
      streamback.get().disconnect();
      latch2.run();
      Assert.assertEquals("STATUS:Disconnected", streamback.get(2));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_attach_yay() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_ATTACH);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    dataService.initialize(KEY, wrap("{\"__constructed\":true}"), Callback.DONT_CARE_VOID);
    dataService.patch(
        KEY,
        wrap(
            "{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}"),
        Callback.DONT_CARE_VOID);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":3}", streamback.get(1));
      LatchCallback cb1 = new LatchCallback();
      LatchCallback cb2 = new LatchCallback();
      streamback.get().canAttach(cb1.toBool(-5, 5));
      cb1.await_success(-5);
      streamback.get().attach(new NtAsset("id", "name", "meme", 1, "", ""), cb2);
      cb2.await_success(5);
      Assert.assertEquals("{\"data\":{\"x\":43},\"seq\":5}", streamback.get(2));
      latch2.run();
      streamback.get().disconnect();
      latch3.run();
      Assert.assertEquals("STATUS:Disconnected", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_twice_series() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback1 = new MockStreamback();
      MockStreamback streamback2 = new MockStreamback();
      Runnable latch1a = streamback1.latchAt(2);
      Runnable latch2a = streamback2.latchAt(2);
      Runnable latch1b = streamback1.latchAt(4);
      Runnable latch2b = streamback2.latchAt(4);
      service.connect(NtClient.NO_ONE, KEY, streamback1);
      streamback1.await_began();
      service.connect(NtClient.NO_ONE, KEY, streamback2);
      streamback2.await_began();
      latch1a.run();
      latch2a.run();
      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback1.get(1));
      Assert.assertEquals("STATUS:Connected", streamback2.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":5}", streamback2.get(1));
      streamback1.get().disconnect();
      streamback2.get().disconnect();
      latch1b.run();
      latch2b.run();
      Assert.assertEquals("{\"seq\":5}", streamback1.get(2));
      Assert.assertEquals("STATUS:Disconnected", streamback1.get(3));
      Assert.assertEquals("STATUS:Disconnected", streamback2.get(2));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_twice_parallel() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, new Key("space", "key"), "{}", null, created);
      created.await_success();
      dataService.pause();
      MockStreamback streamback1 = new MockStreamback();
      MockStreamback streamback2 = new MockStreamback();
      Runnable latch1 = streamback1.latchAt(2);
      Runnable latch2 = streamback2.latchAt(2);
      Runnable queuedUp = dataService.latchAt(1);
      service.connect(NtClient.NO_ONE, KEY, streamback1);
      service.connect(NtClient.NO_ONE, KEY, streamback2);
      queuedUp.run();
      dataService.unpause();
      streamback1.await_began();
      streamback2.await_began();
      latch1.run();
      latch2.run();
      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":3}", streamback1.get(1));
      Assert.assertEquals("STATUS:Connected", streamback2.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":3}", streamback2.get(1));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_failed_factory() throws Exception {
    MockRacerLivingDocumentFactoryFactory factoryFactory =
        new MockRacerLivingDocumentFactoryFactory();
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latchAfter = factoryFactory.latchAt(1);
      service.connect(NtClient.NO_ONE, KEY, streamback);
      latchAfter.run();
      factoryFactory.satisfyNone(KEY);
      streamback.await_failure(50000);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_failed_connect() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created);
      created.await_success();
      dataService.pause();
      dataService.set(new MockFailureDataService());
      dataService.unpause();
      MockStreamback streamback = new MockStreamback();
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_failure(999);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_failed_load() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockFailureDataService dataService = new MockFailureDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_failure(999);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_failed_create_view() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created);
      created.await_success();
      dataService.pause();
      Runnable latch1 = dataService.latchAt(1);
      Runnable latch2 = dataService.latchAt(2);
      MockStreamback streamback = new MockStreamback();
      service.connect(NtClient.NO_ONE, KEY, streamback);
      latch1.run();
      dataService.once();
      latch2.run();
      dataService.once();
      dataService.set(new MockFailureDataService());
      dataService.unpause();
      streamback.await_failure(999);
    } finally {
      service.shutdown();
    }
  }
}

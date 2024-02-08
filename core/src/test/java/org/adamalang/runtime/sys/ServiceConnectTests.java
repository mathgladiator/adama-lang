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
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class ServiceConnectTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@static { create { return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String SIMPLE_CODE_ATTACH =
      "@static { create { return true; } } public int x; @connected { x = 42; return @who == @no_one; } @can_attach { return true; } @attached (a) { x++; } ";
  private static final String CONNECT_CRASH =
      "@static { create { return true; } } public int x; @connected { x = 1; while(x > 0) { x = 2; } return @who == @no_one; } @can_attach { return true; } @attached (a) { x++; } ";
  private static final String CONNECT_CRASH_2 =
      "@static { create { return true; } } public int x; @connected { transition #crash; return @who == @no_one; } #crash { x = 1; while(x > 0) { x = 2; } } @can_attach { return true; } @attached (a) { x++; } ";

  private static final String MIRROR =
      "@static { create { return true; } } public int x; @connected { x = 42; return @who == @no_one; } @can_attach { return true; } @attached (a) { x++; } view int z; bubble zpx = @viewer.z + x;";

  @Test
  public void connect_super_happy_connect() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback.get(1));
      LatchCallback cb1 = new LatchCallback();
      streamback.get().send("foo", null, "{}", cb1);
      cb1.await_success(5);
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":5}", streamback.get(2));
      streamback.get().close();
      latch3.run();
      Assert.assertEquals("STATUS:Disconnected", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_read_only() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streambackFull = new MockStreamback();
      MockStreamback streambackReadonly = new MockStreamback();
      Runnable latch1f = streambackFull.latchAt(2);
      Runnable latch2f = streambackFull.latchAt(4);
      Runnable latch3f = streambackFull.latchAt(5);
      Runnable latch1r = streambackReadonly.latchAt(2);
      Runnable latch2r = streambackReadonly.latchAt(3);
      Runnable latch3r = streambackReadonly.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streambackFull);
      streambackFull.await_began();

      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.ReadOnly, streambackReadonly);
      streambackReadonly.await_began();

      latch1f.run();
      latch1r.run();
      Assert.assertEquals("STATUS:Connected", streambackFull.get(0));
      Assert.assertEquals("STATUS:Connected", streambackReadonly.get(0));

      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streambackFull.get(1));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":5}", streambackReadonly.get(1));

      {
        LatchCallback cb1 = new LatchCallback();
        streambackFull.get().send("foo", null, "{}", cb1);
        cb1.await_success(6);
      }
      {
        LatchCallback cb1 = new LatchCallback();
        streambackReadonly.get().send("foo", null, "{}", cb1);
        cb1.await_failure(144583);
      }

      latch2f.run();
      latch2r.run();

      Assert.assertEquals("{\"seq\":5}", streambackFull.get(2));
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":6}", streambackFull.get(3));
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":6}", streambackReadonly.get(2));


      streambackReadonly.get().close();
      streambackFull.get().close();

      latch3f.run();
      latch3r.run();

      Assert.assertEquals("STATUS:Disconnected", streambackFull.get(4));
      Assert.assertEquals("STATUS:Disconnected", streambackReadonly.get(3));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_write_only() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streambackFull = new MockStreamback();
      MockStreamback streambackWriteOnly = new MockStreamback();
      Runnable latch1f = streambackFull.latchAt(2);
      Runnable latch2f = streambackFull.latchAt(3);
      Runnable latch3f = streambackFull.latchAt(5);
      Runnable latch1w = streambackWriteOnly.latchAt(1);
      Runnable latch2w = streambackWriteOnly.latchAt(2);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streambackFull);
      streambackFull.await_began();

      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.WriteOnly, streambackWriteOnly);
      streambackWriteOnly.await_began();

      latch1f.run();
      latch1w.run();
      Assert.assertEquals("STATUS:Connected", streambackFull.get(0));
      Assert.assertEquals("STATUS:Connected", streambackWriteOnly.get(0));

      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streambackFull.get(1));
      {
        LatchCallback cb1 = new LatchCallback();
        streambackFull.get().send("foo", null, "{}", cb1);
        cb1.await_success(5);
      }
      {
        LatchCallback cb1 = new LatchCallback();
        streambackWriteOnly.get().send("foo", null, "{}", cb1);
        cb1.await_success(6);
      }

      latch2f.run();

      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":5}", streambackFull.get(2));
      Assert.assertEquals("{\"data\":{\"x\":242},\"seq\":6}", streambackFull.get(3));

      streambackWriteOnly.get().close();
      streambackFull.get().close();

      latch3f.run();
      latch2w.run();

      Assert.assertEquals("STATUS:Disconnected", streambackFull.get(4));
      Assert.assertEquals("STATUS:Disconnected", streambackWriteOnly.get(1));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_crash() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(CONNECT_CRASH, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_failure(950384);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_crash_invalidation() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(CONNECT_CRASH_2, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_failure(950384);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_super_happy_load() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    dataService.initialize(KEY, wrap("{\"__constructed\":true}")[0], Callback.DONT_CARE_VOID);
    dataService.patch(
        KEY,
        wrap(
            "{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}"),
        Callback.DONT_CARE_VOID);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":3}", streamback.get(1));
      LatchCallback cb1 = new LatchCallback();
      streamback.get().send("foo", null, "{}", cb1);
      cb1.await_success(4);
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":4}", streamback.get(2));
      streamback.get().close();
      latch3.run();
      Assert.assertEquals("STATUS:Disconnected", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  public static RemoteDocumentUpdate[] wrap(String json) {
    JsonStreamReader reader = new JsonStreamReader(json);
    Object obj = reader.readJavaTree();
    Integer seq = (Integer) (((HashMap<String, Object>) obj).get("__seq"));
    return new RemoteDocumentUpdate[] { new RemoteDocumentUpdate(
        seq == null ? 0 : seq, seq == null ? 0 : seq, NtPrincipal.NO_ONE, "setup", json, "{}", false, 0, 0L, UpdateType.AddUserData) };
  }

  @Test
  public void connect_attach_nope() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    dataService.initialize(KEY, wrap("{\"__constructed\":true}")[0], Callback.DONT_CARE_VOID);
    dataService.patch(
        KEY,
        wrap(
            "{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}"),
        Callback.DONT_CARE_VOID);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":3}", streamback.get(1));
      LatchCallback cb1 = new LatchCallback();
      streamback.get().canAttach(cb1.toBool(-5, 5));
      cb1.await_success(5);
      streamback.get().close();
      latch2.run();
      Assert.assertEquals("STATUS:Disconnected", streamback.get(2));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_view_failure() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    dataService.setPatchFailureAt(1);
    dataService.initialize(KEY, wrap("{\"__constructed\":true}")[0], Callback.DONT_CARE_VOID);
    dataService.patch(
        KEY,
        wrap(
            "{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}"),
        Callback.DONT_CARE_VOID);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_failure(9999);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_attach_yay() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_ATTACH, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    dataService.initialize(KEY, wrap("{\"__constructed\":true}")[0], Callback.DONT_CARE_VOID);
    dataService.patch(
        KEY,
        wrap(
            "{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}"),
        Callback.DONT_CARE_VOID);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":3}", streamback.get(1));
      LatchCallback cb1 = new LatchCallback();
      LatchCallback cb2 = new LatchCallback();
      streamback.get().canAttach(cb1.toBool(-5, 5));
      cb1.await_success(-5);
      streamback.get().attach("id", "name", "meme", 1, "", "", cb2);
      cb2.await_success(4);
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":43},\"seq\":4}", streamback.get(2));
      streamback.get().close();
      latch3.run();
      Assert.assertEquals("STATUS:Disconnected", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_view_update() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(MIRROR, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    dataService.initialize(KEY, wrap("{\"__constructed\":true}")[0], Callback.DONT_CARE_VOID);
    dataService.patch(
        KEY,
        wrap(
            "{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}"),
        Callback.DONT_CARE_VOID);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(3);
      Runnable latch2 = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"view-state-filter\":[\"z\"]}", streamback.get(1));
      Assert.assertEquals("{\"data\":{\"x\":42,\"zpx\":42},\"seq\":3}", streamback.get(2));
      streamback.get().update("{\"z\":100}");
      latch2.run();
      Assert.assertEquals("{\"data\":{\"zpx\":142}}", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_twice_series() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback1 = new MockStreamback();
      MockStreamback streamback2 = new MockStreamback();
      Runnable latch1a = streamback1.latchAt(2);
      Runnable latch2a = streamback2.latchAt(2);
      Runnable latch1b = streamback1.latchAt(4);
      Runnable latch2b = streamback2.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback1);
      streamback1.await_began();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback2);
      streamback2.await_began();
      latch1a.run();
      latch2a.run();
      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback1.get(1));
      Assert.assertEquals("STATUS:Connected", streamback2.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":5}", streamback2.get(1));
      streamback1.get().close();
      streamback2.get().close();
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
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), new Key("space", "key"), "{}", null, created);
      created.await_success();
      dataService.pause();
      MockStreamback streamback1 = new MockStreamback();
      MockStreamback streamback2 = new MockStreamback();
      Runnable latch1 = streamback1.latchAt(2);
      Runnable latch2 = streamback2.latchAt(2);
      Runnable queuedUp = dataService.latchAt(1);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback1);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback2);
      queuedUp.run();
      dataService.unpause();
      streamback1.await_began();
      streamback2.await_began();
      latch1.run();
      latch2.run();
      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback1.get(1));
      Assert.assertEquals("STATUS:Connected", streamback2.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":5}", streamback2.get(1));
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
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latchAfter = factoryFactory.latchAt(1);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      latchAfter.run();
      factoryFactory.satisfyNone(KEY);
      streamback.await_failure(50000);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_failed_connect() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      dataService.pause();
      dataService.set(new MockFailureDataService());
      dataService.unpause();
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_failure(999);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_failed_load() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockFailureDataService dataService = new MockFailureDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_failure(999);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void connect_failed_create_view() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      dataService.pause();
      Runnable latch1 = dataService.latchAt(1);
      Runnable latch2 = dataService.latchAt(2);
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
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

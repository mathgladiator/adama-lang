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
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.mocks.MockBackupService;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.mocks.MockWakeService;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.*;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceRemoteTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");

  private static Deliverer BIND_LAZY(AtomicReference<Deliverer> latent) {
    return new Deliverer() {
      @Override
      public void deliver(NtPrincipal agent, Key key, int id, RemoteResult result, boolean firstParty, Callback<Integer> callback) {
        latent.get().deliver(agent, key, id, result, firstParty, callback);
      }
    };
  }

  @Test
  public void service_failure() throws Exception {
    ArrayList<Runnable> actions = new ArrayList<>();

    synchronized (ServiceRegistry.REGISTRY) {
      ServiceRegistry.REGISTRY.put("sqr1", (space, properties, keys) -> {
        return new SimpleService("sqr1", NtPrincipal.NO_ONE, true) {
          @Override
          public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
            actions.add(() -> {
              callback.failure(new ErrorCodeException(403, "Fire-bidden"));
            });
          }
        };
      });
    }
    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { class=\"sqr1\"; method<M, M> square; }" +
        "public int x = 4;" +
        "public formula y = s.square(@no_one, {x:x});" +
        "channel foo(M y) { x += y.x; }", lazy);

    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), new MockReplicationInitiator(), time, 3);
    latent.set(service);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      Runnable latch4 = streamback.latchAt(5);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":4,\"y\":{\"failed\":false,\"message\":\"waiting...\",\"code\":0},\"seq\":4}", streamback.get(1));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch2.run();
      Assert.assertEquals("{\"data\":{\"y\":{\"failed\":true,\"message\":\"Fire-bidden\",\"code\":403},\"seq\":6}", streamback.get(2));
      LatchCallback cb1 = new LatchCallback();
      streamback.get().send("foo", null, "{\"x\":8}", cb1);
      cb1.await_success(7);
      latch3.run();
      Assert.assertEquals("{\"data\":{\"x\":12,\"y\":{\"failed\":false,\"message\":\"waiting...\",\"code\":0},\"seq\":7}", streamback.get(3));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch4.run();
      Assert.assertEquals("{\"data\":{\"y\":{\"failed\":true,\"message\":\"Fire-bidden\",\"code\":403},\"seq\":9}", streamback.get(4));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void service_invoke() throws Exception {
    ArrayList<Runnable> actions = new ArrayList<>();

    synchronized (ServiceRegistry.REGISTRY) {
      ServiceRegistry.REGISTRY.put("sqr2", (space, properties, keys) -> {
        return new SimpleService("sqr2", NtPrincipal.NO_ONE, true) {
          @Override
          public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
            int _x = 1;
            JsonStreamReader reader = new JsonStreamReader(request);
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                switch (reader.fieldName()) {
                  case "x":
                    _x = reader.readInteger();
                    break;
                  default:
                    reader.skipValue();
                }
              }
            }
            int x = _x;
            actions.add(() -> {
              callback.success("{\"x\":" + (x * x) + "}");
            });
          }
        };
      });
    }
    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { class=\"sqr2\"; method<M, M> square; }" +
        "public int x = 4;" +
        "public formula y = s.square(@no_one, {x:x});" +
        "channel foo(M y) { x += y.x; }", lazy);

    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), new MockReplicationInitiator(), time, 3);
    latent.set(service);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      Runnable latch4 = streamback.latchAt(5);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":4,\"y\":{\"failed\":false,\"message\":\"waiting...\",\"code\":0},\"seq\":4}", streamback.get(1));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch2.run();
      Assert.assertEquals("{\"data\":{\"y\":{\"message\":\"OK\",\"result\":{\"x\":16}},\"seq\":6}", streamback.get(2));
      LatchCallback cb1 = new LatchCallback();
      streamback.get().send("foo", null, "{\"x\":8}", cb1);
      cb1.await_success(7);
      latch3.run();
      Assert.assertEquals("{\"data\":{\"x\":12,\"y\":{\"message\":\"waiting...\",\"result\":null},\"seq\":7}", streamback.get(3));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch4.run();
      Assert.assertEquals("{\"data\":{\"y\":{\"message\":\"OK\",\"result\":{\"x\":144}},\"seq\":9}", streamback.get(4));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void service_invoke_message_handler() throws Exception {
    ArrayList<Runnable> actions = new ArrayList<>();

    synchronized (ServiceRegistry.REGISTRY) {
      ServiceRegistry.REGISTRY.put("sqr3", (space, properties, keys) -> {
        return new SimpleService("sqr3", NtPrincipal.NO_ONE, false) {
          @Override
          public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
            int _x = 1;
            JsonStreamReader reader = new JsonStreamReader(request);
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                switch (reader.fieldName()) {
                  case "x":
                    _x = reader.readInteger();
                    break;
                  default:
                    reader.skipValue();
                }
              }
            }
            int x = _x;
            actions.add(() -> {
              callback.success("{\"x\":" + (x * x) + "}");
            });
          }
        };
      });
    }

    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { class=\"sqr3\"; method<M, M> square; }" +
        "public int x = 4;" +
        "channel foo(M m) {" + //
        " x += m.x;" + //
        " if (s.square(@no_one, {x:x}).await() as zz) { x += zz.x; }" +
        " if (s.square(@no_one, {x:x}).await() as zz) { x += zz.x; }" +
        " x += m.x;" + //
        "}", lazy);

    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), new MockReplicationInitiator(), time, 3);
    latent.set(service);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      Runnable latch4 = streamback.latchAt(5);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":4},\"seq\":4}", streamback.get(1));
      LatchCallback cb1 = new LatchCallback();
      streamback.get().send("foo", null, "{\"x\":10}", cb1);
      cb1.await_success(5);
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":14},\"seq\":5}", streamback.get(2));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch3.run();
      Assert.assertEquals("{\"data\":{\"x\":210},\"seq\":7}", streamback.get(3));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch4.run();
      Assert.assertEquals("{\"data\":{\"x\":44320},\"seq\":10}", streamback.get(4));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void service_invoke_state_machine() throws Exception {
    ArrayList<Runnable> actions = new ArrayList<>();

    synchronized (ServiceRegistry.REGISTRY) {
      ServiceRegistry.REGISTRY.put("sqr4", (space, properties, keys) -> {
        return new SimpleService("sqr4", NtPrincipal.NO_ONE, false) {
          @Override
          public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
            int _x = 1;
            JsonStreamReader reader = new JsonStreamReader(request);
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                switch (reader.fieldName()) {
                  case "x":
                    _x = reader.readInteger();
                    break;
                  default:
                    reader.skipValue();
                }
              }
            }
            int x = _x;
            actions.add(() -> {
              callback.success("{\"x\":" + (x * x) + "}");
            });
          }
        };
      });
    }
    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { class=\"sqr4\"; method<M, M> square; }" +
        "public int x = 4;" +
        "@construct { transition #gogo; }" +
        "#gogo {" + //
        "   if (s.square(@no_one, {x:x}).await() as zz) { x += zz.x; }" + //
        "   if (s.square(@no_one, {x:x}).await() as zz) { x += zz.x; }" + //
        "}", lazy);

    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), new MockReplicationInitiator(), time, 3);
    latent.set(service);
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
      Assert.assertEquals("{\"data\":{\"x\":4},\"seq\":2}", streamback.get(1));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":20},\"seq\":4}", streamback.get(2));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch3.run();
      Assert.assertEquals("{\"data\":{\"x\":420},\"seq\":7}", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void service_invoke_state_machine_func() throws Exception {
    ArrayList<Runnable> actions = new ArrayList<>();

    synchronized (ServiceRegistry.REGISTRY) {
      ServiceRegistry.REGISTRY.put("sqr5", (space, properties, keys) -> {
        return new SimpleService("sqr5", NtPrincipal.NO_ONE, false) {
          @Override
          public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
            int _x = 1;
            JsonStreamReader reader = new JsonStreamReader(request);
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                switch (reader.fieldName()) {
                  case "x":
                    _x = reader.readInteger();
                    break;
                  default:
                    reader.skipValue();
                }
              }
            }
            int x = _x;
            actions.add(() -> {
              callback.success("{\"x\":" + (x * x) + "}");
            });
          }
        };
      });
    }

    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { class=\"sqr5\"; method<M, M> square; }" +
        "public int x = 4;" +
        "@construct { transition #gogo; }" +
        "procedure go() {" +
        "   if (s.square(@no_one, {x:x}).await() as zz) { x += zz.x; }" + //
        "   if (s.square(@no_one, {x:x}).await() as zz) { x += zz.x; }" + //
        "}" +
        "#gogo {" + //
        "go();" +
        "}", lazy);

    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), new MockReplicationInitiator(), time, 3);
    latent.set(service);
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
      Assert.assertEquals("{\"data\":{\"x\":4},\"seq\":2}", streamback.get(1));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":20},\"seq\":4}", streamback.get(2));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch3.run();
      Assert.assertEquals("{\"data\":{\"x\":420},\"seq\":7}", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void service_invoke_state_machine_method() throws Exception {
    ArrayList<Runnable> actions = new ArrayList<>();

    synchronized (ServiceRegistry.REGISTRY) {
      ServiceRegistry.REGISTRY.put("sqr6", (space, properties, keys) -> {
        return new SimpleService("sqr6", NtPrincipal.NO_ONE, true) {
          @Override
          public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
            int _x = 1;
            JsonStreamReader reader = new JsonStreamReader(request);
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                switch (reader.fieldName()) {
                  case "x":
                    _x = reader.readInteger();
                    break;
                  default:
                    reader.skipValue();
                }
              }
            }
            int x = _x;
            actions.add(() -> {
              callback.success("{\"x\":" + (x * x) + "}");
            });
          }
        };
      });
    }
    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { class=\"sqr6\"; method<M, M> square; }" +
        "record R {" +
        "  public int x = 4;" +
        "  method go() {" +
        "   if (s.square(@no_one, {x:x}).await() as zz) { x += zz.x; }" + //
        "   if (s.square(@no_one, {x:x}).await() as zz) { x += zz.x; }" + //
        "  }" +
        "}" +
        "public R r;" +
        "@construct { transition #gogo; }" +
        "#gogo {" + //
        "r.go();" +
        "}", lazy);

    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), new MockReplicationInitiator(), time, 3);
    latent.set(service);
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
      Assert.assertEquals("{\"data\":{\"r\":{\"x\":4}},\"seq\":2}", streamback.get(1));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch2.run();
      Assert.assertEquals("{\"data\":{\"r\":{\"x\":20}},\"seq\":4}", streamback.get(2));
      Assert.assertEquals(1, actions.size());
      actions.remove(0).run();
      latch3.run();
      Assert.assertEquals("{\"data\":{\"r\":{\"x\":420}},\"seq\":7}", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

}

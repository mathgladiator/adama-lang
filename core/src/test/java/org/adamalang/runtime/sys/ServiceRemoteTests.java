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
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
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
      public void deliver(NtClient agent, Key key, int id, RemoteResult result, boolean firstParty, Callback<Integer> callback) {
        latent.get().deliver(agent, key, id, result, firstParty, callback);
      }
    };
  }
  @Test
  public void service_failure() throws Exception {
    ArrayList<Runnable> actions = new ArrayList<>();

    ServiceRegistry.REGISTRY.put("sqr1", (space, properties) -> {
      return new SimpleService("sqr1", NtClient.NO_ONE, true) {
        @Override
        public void request(String method, String request, Callback<String> callback) {
          actions.add(() -> {
            callback.failure(new ErrorCodeException(403, "Fire-bidden"));
          });
        }
      };
    });
    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { std=\"sqr1\"; method<M, M> square; }" +
        "public int x = 4;" +
        "public formula y = s.square(@no_one, {x:x});" +
        "channel foo(M y) { x += y.x; }", lazy);

    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    latent.set(service);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      Runnable latch4 = streamback.latchAt(5);
      service.connect(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, streamback);
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

    ServiceRegistry.REGISTRY.put("sqr2", (space, properties) -> {
      return new SimpleService("sqr2", NtClient.NO_ONE, true) {
        @Override
        public void request(String method, String request, Callback<String> callback) {
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
            callback.success("{\"x\":" + (x * x)+"}");
          });
        }
      };
    });
    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { std=\"sqr2\"; method<M, M> square; }" +
        "public int x = 4;" +
        "public formula y = s.square(@no_one, {x:x});" +
        "channel foo(M y) { x += y.x; }", lazy);

    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    latent.set(service);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      Runnable latch4 = streamback.latchAt(5);
      service.connect(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, streamback);
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

    ServiceRegistry.REGISTRY.put("sqr3", (space, properties) -> {
      return new SimpleService("sqr3", NtClient.NO_ONE, false) {
        @Override
        public void request(String method, String request, Callback<String> callback) {
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
            callback.success("{\"x\":" + (x * x)+"}");
          });
        }
      };
    });
    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { std=\"sqr3\"; method<M, M> square; }" +
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
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    latent.set(service);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      Runnable latch4 = streamback.latchAt(5);
      service.connect(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, streamback);
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

    ServiceRegistry.REGISTRY.put("sqr4", (space, properties) -> {
      return new SimpleService("sqr4", NtClient.NO_ONE, false) {
        @Override
        public void request(String method, String request, Callback<String> callback) {
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
            callback.success("{\"x\":" + (x * x)+"}");
          });
        }
      };
    });
    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { std=\"sqr4\"; method<M, M> square; }" +
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
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    latent.set(service);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, streamback);
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

    ServiceRegistry.REGISTRY.put("sqr5", (space, properties) -> {
      return new SimpleService("sqr5", NtClient.NO_ONE, false) {
        @Override
        public void request(String method, String request, Callback<String> callback) {
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
            callback.success("{\"x\":" + (x * x)+"}");
          });
        }
      };
    });
    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { std=\"sqr5\"; method<M, M> square; }" +
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
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    latent.set(service);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, streamback);
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

    ServiceRegistry.REGISTRY.put("sqr6", (space, properties) -> {
      return new SimpleService("sqr6", NtClient.NO_ONE, true) {
        @Override
        public void request(String method, String request, Callback<String> callback) {
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
            callback.success("{\"x\":" + (x * x)+"}");
          });
        }
      };
    });
    AtomicReference<Deliverer> latent = new AtomicReference<>(null);
    Deliverer lazy = BIND_LAZY(latent);

    LivingDocumentFactory factory = LivingDocumentTests.compile("@static { create { return true; } }" +
        "@connected { return true; }" +
        "message M { int x; }" +
        "service s { std=\"sqr6\"; method<M, M> square; }" +
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
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    latent.set(service);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, streamback);
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

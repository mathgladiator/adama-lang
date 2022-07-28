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

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ServiceDeploymentTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG_FROM =
      "@static { create { return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String SIMPLE_CODE_MSG_TO =
      "@static { create { return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 5000; }";

  @Test
  public void deploy_happy() throws Exception {
    LivingDocumentFactory factoryFrom = LivingDocumentTests.compile(SIMPLE_CODE_MSG_FROM, Deliverer.FAILURE);
    LivingDocumentFactory factoryTo = LivingDocumentTests.compile(SIMPLE_CODE_MSG_TO, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factoryFrom);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch = streamback.latchAt(5);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
      streamback.await_began();
      {
        LatchCallback callback = new LatchCallback();
        streamback.get().send("foo", null, "{}", callback);
        callback.await_success(5);
      }
      factoryFactory.set(factoryTo);
      CountDownLatch deployed = new CountDownLatch(1);
      service.deploy(
          new DeploymentMonitor() {
            @Override
            public void bumpDocument(boolean changed) {
              if (changed) {
                deployed.countDown();
              }
            }

            @Override
            public void witnessException(ErrorCodeException ex) {
              ex.printStackTrace();
            }
          });
      Assert.assertTrue(deployed.await(1000, TimeUnit.MILLISECONDS));
      {
        LatchCallback callback = new LatchCallback();
        streamback.get().send("foo", null, "{}", callback);
        callback.await_success(7);
      }
      latch.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback.get(1));
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":5}", streamback.get(2));
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":6}", streamback.get(3));
      Assert.assertEquals("{\"data\":{\"x\":5142},\"seq\":7}", streamback.get(4));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void deploy_crash_data() throws Exception {
    LivingDocumentFactory factoryFrom = LivingDocumentTests.compile(SIMPLE_CODE_MSG_FROM, Deliverer.FAILURE);
    LivingDocumentFactory factoryTo = LivingDocumentTests.compile(SIMPLE_CODE_MSG_TO, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factoryFrom);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
      streamback.await_began();
      {
        LatchCallback callback = new LatchCallback();
        streamback.get().send("foo", null, "{}", callback);
        callback.await_success(5);
      }
      factoryFactory.set(factoryTo);
      dataService.pause();
      dataService.set(new MockFailureDataService());
      dataService.unpause();
      CountDownLatch deployed = new CountDownLatch(1);
      service.deploy(
          new DeploymentMonitor() {
            @Override
            public void bumpDocument(boolean changed) {}

            @Override
            public void witnessException(ErrorCodeException ex) {
              deployed.countDown();
            }
          });
      Assert.assertTrue(deployed.await(1000, TimeUnit.MILLISECONDS));
      {
        LatchCallback callback = new LatchCallback();
        streamback.get().send("foo", null, "{}", callback);
        callback.await_failure(144416);
      }
      latch.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback.get(1));
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":5}", streamback.get(2));
      Assert.assertEquals("STATUS:Disconnected", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void deploy_bad_code() throws Exception {
    LivingDocumentFactory factoryFrom = LivingDocumentTests.compile(SIMPLE_CODE_MSG_FROM, Deliverer.FAILURE);
    LivingDocumentFactory factoryTo = LivingDocumentTests.compile(SIMPLE_CODE_MSG_TO, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factoryFrom);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
      streamback.await_began();
      {
        LatchCallback callback = new LatchCallback();
        streamback.get().send("foo", null, "{}", callback);
        callback.await_success(5);
      }
      factoryFactory.set(null);
      CountDownLatch deployed = new CountDownLatch(1);
      service.deploy(
          new DeploymentMonitor() {
            @Override
            public void bumpDocument(boolean changed) {}

            @Override
            public void witnessException(ErrorCodeException ex) {
              deployed.countDown();
            }
          });
      Assert.assertTrue(deployed.await(1000, TimeUnit.MILLISECONDS));
      {
        LatchCallback callback = new LatchCallback();
        streamback.get().send("foo", null, "{}", callback);
        callback.await_success(6);
      }
      latch.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback.get(1));
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":5}", streamback.get(2));
      Assert.assertEquals("{\"data\":{\"x\":242},\"seq\":6}", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void deploy_really_bad_code() throws Exception {
    LivingDocumentFactory factoryFrom = LivingDocumentTests.compile(SIMPLE_CODE_MSG_FROM, Deliverer.FAILURE);
    LivingDocumentFactory factoryTo = LivingDocumentTests.compile(SIMPLE_CODE_MSG_TO, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factoryFrom);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
      streamback.await_began();
      {
        LatchCallback callback = new LatchCallback();
        streamback.get().send("foo", null, "{}", callback);
        callback.await_success(5);
      }
      factoryFactory.set(
          new LivingDocumentFactory(
              "space",
              "Foo",
              "import java.util.HashMap;\nimport org.adamalang.runtime.contracts.DocumentMonitor;" +
                  "import org.adamalang.runtime.natives.*;import org.adamalang.runtime.sys.*;\n" +
                  "public class Foo { public Foo(DocumentMonitor dm) {} " +
                  "public static boolean __onCanCreate(CoreRequestContext who) { return false; } " +
                  "public static boolean __onCanInvent(CoreRequestContext who) { return false; } " +
                  "public static boolean __onCanSendWhileDisconnected(CoreRequestContext who) { return false; } " +
                  "public static HashMap<String, Object> __config() { return new HashMap<>(); }" +
                  "public static HashMap<String, HashMap<String, Object>> __services() { return new HashMap<>(); }" +
                  "}",
              "{}", Deliverer.FAILURE));
      CountDownLatch deployed = new CountDownLatch(1);
      service.deploy(
          new DeploymentMonitor() {
            @Override
            public void bumpDocument(boolean changed) {}

            @Override
            public void witnessException(ErrorCodeException ex) {
              deployed.countDown();
            }
          });
      Assert.assertTrue(deployed.await(1000, TimeUnit.MILLISECONDS));
      {
        LatchCallback callback = new LatchCallback();
        streamback.get().send("foo", null, "{}", callback);
        callback.await_success(6);
      }
      latch.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback.get(1));
      Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":5}", streamback.get(2));
      Assert.assertEquals("{\"data\":{\"x\":242},\"seq\":6}", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }
}

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
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Test;

public class ServiceCreateTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());

  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@static { create(who) { return true; } } public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; }";

  @Test
  public void create_super_happy() throws Exception {
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
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create_twice_series() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created1 = new NullCallbackLatch();
      NullCallbackLatch created2 = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created1);
      created1.await_success();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created2);
      created2.await_failure(667658);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create_after_connect_series() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created1 = new NullCallbackLatch();
      NullCallbackLatch created2 = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created1);
      created1.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      service.connect(NtClient.NO_ONE, KEY, streamback);
      latch1.run();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created2);
      created2.await_failure(130092);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create_twice_parallel() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockRacerLivingDocumentFactoryFactory factoryFactory =
        new MockRacerLivingDocumentFactoryFactory();
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created1 = new NullCallbackLatch();
      NullCallbackLatch created2 = new NullCallbackLatch();
      Runnable latchTwoCalls = factoryFactory.latchAt(2);
      service.create(NtClient.NO_ONE, KEY, "{}", null, created1);
      service.create(NtClient.NO_ONE, KEY, "{}", null, created2);
      latchTwoCalls.run();
      factoryFactory.satisfyAll(KEY, factory);
      created1.await_success();
      created2.await_failure();
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create_no_factory() throws Exception {
    MockRacerLivingDocumentFactoryFactory factoryFactory =
        new MockRacerLivingDocumentFactoryFactory();
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      Runnable latchAfter = factoryFactory.latchAt(1);
      service.create(NtClient.NO_ONE, KEY, "{}", null, created);
      latchAfter.run();
      factoryFactory.satisfyNone(KEY);
      created.await_failure();
    } finally {
      service.shutdown();
    }
  }
}

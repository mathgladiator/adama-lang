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
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.mocks.MockStreamback;
import org.adamalang.runtime.sys.mocks.NullCallbackLatch;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Test;

public class ServiceCrashCreationTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG_VAR1 =
      "@static { create(who) { while(true) {} return true; } invent(who) { while(true) {} return true; } } public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String SIMPLE_CODE_MSG_VAR2 =
      "@static { create(who) { for(;;) {} return true; } invent(who) { for(;;) {} return true; } } public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String SIMPLE_CODE_MSG_VAR3 =
      "@static { create(who) { do {} while (true); return true; } invent(who) { do {} while (true); return true; } } public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String SIMPLE_CODE_MSG_VAR4 =
      "@static { create(who) { for(;1 < 2;) {} return true; } invent(who) { for(;1 < 2;) {} return true; } } public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String REJECT =
      "@static { create(who) { return false; } invent(who) { return false; } } public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; }";

  @Test
  public void cant_create_reject() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(REJECT);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created);
      created.await_failure(134259);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_invent_reject() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(REJECT);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_failure(625676);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create1() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR1);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created);
      created.await_failure(180858);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create2() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR2);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created);
      created.await_failure(180858);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create3() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR3);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created);
      created.await_failure(180858);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create4() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR4);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", null, created);
      created.await_failure(180858);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_invent1() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR1);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_failure(146558);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_invent2() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR2);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_failure(146558);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_invent3() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR3);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_failure(146558);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_invent4() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR4);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_failure(146558);
    } finally {
      service.shutdown();
    }
  }
}

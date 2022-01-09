package org.adamalang.runtime.sys;

import org.adamalang.common.TimeSource;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class ServiceImplicitCreateTests {
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@can_create(who) { return true; } @can_invent(who) { return true; } public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; }";

  @Test
  public void ideal() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latchClient = streamback.latchAt(2);
      Runnable latchData = dataService.latchLogAt(5);
      service.connect(NtClient.NO_ONE, KEY, streamback);
      streamback.await_began();
      latchData.run();
      latchClient.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback.get(1));
      dataService.assertLogAt(0, "INIT:space/key:0->{\"__constructed\":true}");
      Assert.assertTrue(dataService.getLogAt(2).contains("\"__connection_id\":1,\"x\":42"));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void race_cause_retry() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback1 = new MockStreamback();
      MockStreamback streamback2 = new MockStreamback();
      Runnable latchClient1 = streamback1.latchAt(2);
      Runnable latchClient2 = streamback2.latchAt(2);
      Runnable bothAsking = dataService.latchAt(2);
      Runnable bothCreating = dataService.latchAt(4);
      Runnable latchData = realDataService.latchLogAt(5);
      dataService.pause();
      service.connect(NtClient.NO_ONE, KEY, streamback1);
      service.connect(NtClient.NO_ONE, KEY, streamback2);
      bothAsking.run();
      dataService.once();
      dataService.once();
      bothCreating.run();
      dataService.unpause();
      latchClient1.run();
      latchClient2.run();
      latchData.run();
      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("STATUS:Connected", streamback2.get(0));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void one_fails_during_race() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    realDataService.failInitializationAgainWithWrongErrorCode();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      MockStreamback streamback1 = new MockStreamback();
      MockStreamback streamback2 = new MockStreamback();
      Runnable bothAsking = dataService.latchAt(2);
      Runnable bothCreating = dataService.latchAt(4);
      Runnable latchData = realDataService.latchLogAt(5);
      dataService.pause();
      service.connect(NtClient.NO_ONE, KEY, streamback1);
      service.connect(NtClient.NO_ONE, KEY, streamback2);
      bothAsking.run();
      dataService.once();
      dataService.once();
      bothCreating.run();
      dataService.unpause();
      latchData.run();
    } finally {
      service.shutdown();
    }
  }
}

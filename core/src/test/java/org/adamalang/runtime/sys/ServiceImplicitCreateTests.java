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
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceImplicitCreateTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@static { create { return true; } invent { return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }";

  @Test
  public void ideal() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latchClient = streamback.latchAt(2);
      Runnable latchData = dataService.latchLogAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      latchData.run();
      latchClient.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":4}", streamback.get(1));
      dataService.assertLogAtStartsWith(0, "INIT:space/key:1->{\"__constructed\":true,\"__messages\":null,\"__seq\":1,");
      Assert.assertTrue(dataService.getLogAt(2).contains("\"__connection_id\":1,\"x\":42"));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void race_cause_create_storm_but_both_work() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback1 = new MockStreamback();
      MockStreamback streamback2 = new MockStreamback();
      Runnable latchClient1 = streamback1.latchAt(2);
      Runnable latchClient2 = streamback2.latchAt(2);
      Runnable onlyOneAsks = dataService.latchAt(1);
      Runnable onlyOneCreates = dataService.latchAt(2);
      Runnable latchData = realDataService.latchLogAt(4);
      dataService.pause();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback1);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback2);
      onlyOneAsks.run();
      dataService.once();
      onlyOneCreates.run();
      dataService.unpause();
      latchData.run();
      latchClient1.run();
      latchClient2.run();
      Assert.assertEquals("STATUS:Connected", streamback1.get(0));
      Assert.assertEquals("STATUS:Connected", streamback2.get(0));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void race_cause_retry_but_no_factory_next_time() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactoryReal =
        new MockInstantLivingDocumentFactoryFactory(factory);
    AtomicInteger countDownUntilFailure = new AtomicInteger(2);
    LivingDocumentFactoryFactory proxyFactory = new LivingDocumentFactoryFactory() {
      @Override
      public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
        int val = countDownUntilFailure.decrementAndGet();
        if (val <= 0) {
          callback.failure(new ErrorCodeException(-123));
        } else {
          factoryFactoryReal.fetch(key, callback);
        }
      }

      @Override
      public void account(HashMap<String, PredictiveInventory.MeteringSample> sample) {
      }

      @Override
      public Collection<String> spacesAvailable() {
        return factoryFactoryReal.spacesAvailable();
      }
    };
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, proxyFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback1 = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback1);
      streamback1.await_failure(-123);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void one_fails_during_race() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    realDataService.failInitializationAgainWithWrongErrorCode();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback1 = new MockStreamback();
      MockStreamback streamback2 = new MockStreamback();
      Runnable bothAskingOnlyOne = dataService.latchAt(1);
      Runnable onlyOneAsks = dataService.latchAt(2);
      Runnable latchData = realDataService.latchLogAt(3);
      dataService.pause();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback1);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback2);
      bothAskingOnlyOne.run();
      dataService.once();
      onlyOneAsks.run();
      dataService.unpause();
      latchData.run();
    } finally {
      service.shutdown();
    }
  }
}

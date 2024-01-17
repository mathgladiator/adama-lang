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
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class ServiceDocumentControlTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@static { create { return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { Document.rewind(1); }";
  private static final String SIMPLE_CODE_MSG_PLUS =
      "@static { create { return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { Document.rewind(1); } channel goo(M y) { x++; }";

  @Test
  public void rewind() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    dataService.initialize(
        KEY, ServiceConnectTests.wrap("{\"__constructed\":true}")[0], Callback.DONT_CARE_VOID);
    dataService.patch(
        KEY,
        ServiceConnectTests.wrap(
            "{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}"),
        Callback.DONT_CARE_VOID);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      Runnable latch3 = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":3}", streamback.get(1));
      LatchCallback cb1 = new LatchCallback();
      streamback.get().send("foo", null, "{}", cb1);
      cb1.await_success(4);
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":1000},\"seq\":4}", streamback.get(2));
      streamback.get().close();
      latch3.run();
      Assert.assertEquals("STATUS:Disconnected", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void rewind_in_batch() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_PLUS, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    dataService.initialize(
        KEY, ServiceConnectTests.wrap("{\"__constructed\":true}")[0], Callback.DONT_CARE_VOID);
    dataService.patch(
        KEY,
        ServiceConnectTests.wrap(
            "{\"__seq\":2,\"__connection_id\":1,\"x\":42,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}"),
        Callback.DONT_CARE_VOID);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(4);
      Runnable latch3 = streamback.latchAt(5);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", streamback);
      streamback.await_began();
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":3}", streamback.get(1));
      dataService.pause();

      LatchCallback cb1 = new LatchCallback();
      LatchCallback cb2 = new LatchCallback();
      LatchCallback cb3 = new LatchCallback();
      streamback.get().send("goo", null, "{}", cb1);
      streamback.get().send("goo", null, "{}", cb2);
      streamback.get().send("foo", null, "{}", cb3);
      dataService.unpause();
      cb1.await_success(4);
      cb2.await_failure(159869);
      cb3.await_success(5);
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":43},\"seq\":4}", streamback.get(2));
      Assert.assertEquals("{\"data\":{\"x\":1000},\"seq\":5}", streamback.get(3));
      streamback.get().close();
      latch3.run();
      Assert.assertEquals("STATUS:Disconnected", streamback.get(4));
    } finally {
      service.shutdown();
    }
  }
}

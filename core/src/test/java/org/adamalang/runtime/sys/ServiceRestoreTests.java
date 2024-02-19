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
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.DocumentRestore;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.mocks.SimpleIntCallback;
import org.adamalang.runtime.data.mocks.SimpleVoidCallback;
import org.adamalang.runtime.mocks.MockBackupService;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ServiceRestoreTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CRON =
      "@static { create { return true; } } public int x = 0; @connected { x += 42; return @who == @no_one; } @cron fooz daily 8:00 { x += 7; doit.enqueue(@no_one, {}); } message M {} channel doit(M m) { x += 100; } ";

  @Test
  public void restore() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CRON, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockTime time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, new MockBackupService(), time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      {
        MockStreamback streamback = new MockStreamback();
        Runnable latch1 = streamback.latchAt(2);
        Runnable latch2 = streamback.latchAt(3);
        service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
        streamback.await_began();
        latch1.run();
        Assert.assertEquals("STATUS:Connected", streamback.get(0));
        Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":5}", streamback.get(1));
        SimpleVoidCallback cb_Restored = new SimpleVoidCallback();
        service.restore(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, new DocumentRestore(100, "{\"x\":1000,\"__seq\":10,\"__constructed\":true,\"__entropy\":\"8552295702242200522\"}", NtPrincipal.NO_ONE), cb_Restored);
        cb_Restored.assertSuccess();
        latch2.run();
        Assert.assertEquals("STATUS:Disconnected", streamback.get(2));
      }
      dataService.assertLogAt(1, "LOAD:space/key");
      dataService.assertLogAt(4, "RECOVER:space/key");
      {
        MockStreamback streamback = new MockStreamback();
        Runnable latch1 = streamback.latchAt(2);
        service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
        streamback.await_began();
        latch1.run();
        Assert.assertEquals("STATUS:Connected", streamback.get(0));
        Assert.assertEquals("{\"data\":{\"x\":1042},\"seq\":14}", streamback.get(1));
      }
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void restore_after_patch() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CRON, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockTime time = new MockTime();
    MockInstantDataService instant = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(instant);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, new MockBackupService(), time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      {
        MockStreamback streamback = new MockStreamback();
        Runnable latch1 = streamback.latchAt(2);
        Runnable latch2 = streamback.latchAt(4);
        service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
        streamback.await_began();
        latch1.run();
        Assert.assertEquals("STATUS:Connected", streamback.get(0));
        Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":5}", streamback.get(1));
        dataService.pause();
        Runnable latchPatching = dataService.latchAt(1);
        SimpleIntCallback cb_Send = new SimpleIntCallback();
        streamback.get().send("doit", null, "{}", cb_Send);
        latchPatching.run();
        SimpleVoidCallback cb_Restored = new SimpleVoidCallback();
        service.restore(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, new DocumentRestore(100, "{\"x\":1000,\"__seq\":10,\"__constructed\":true,\"__entropy\":\"8552295702242200522\"}", NtPrincipal.NO_ONE), cb_Restored);
        dataService.unpause();
        cb_Restored.assertSuccess();
        latch2.run();
        cb_Send.assertSuccess(6);
        Assert.assertEquals("{\"data\":{\"x\":142},\"seq\":6}", streamback.get(2));
        Assert.assertEquals("STATUS:Disconnected", streamback.get(3));
      }
      instant.assertLogAt(1, "LOAD:space/key");
      instant.assertLogAt(5, "RECOVER:space/key");
      {
        MockStreamback streamback = new MockStreamback();
        Runnable latch1 = streamback.latchAt(2);
        service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
        streamback.await_began();
        latch1.run();
        Assert.assertEquals("STATUS:Connected", streamback.get(0));
        Assert.assertEquals("{\"data\":{\"x\":1042},\"seq\":14}", streamback.get(1));
      }
    } finally {
      service.shutdown();
    }
  }
}

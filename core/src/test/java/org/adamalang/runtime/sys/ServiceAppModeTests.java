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

import org.adamalang.common.Json;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockBackupService;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.mocks.MockWakeService;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceAppModeTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());

  private static final Key KEY = new Key("space", "key");
  private static final String APP_MODE =
      "@static { create { return true; } invent { return true; } frequency = 100; } public int x; public int y; @connected { x = 42; y = 0; return true; } message M {} channel foo(M z) { x += 100; y++; }";

  @Test
  public void app_mode() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(APP_MODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    InMemoryDataService dataService = new InMemoryDataService(executor, TimeSource.REAL_TIME);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), time, 1);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();

      MockStreamback streambackViewer = new MockStreamback();
      service.connect(ContextSupport.WRAP(new NtPrincipal("ag", "au")), KEY, "{}", ConnectionMode.Full, streambackViewer);
      streambackViewer.await_began();

      ArrayList<LatchCallback> callbacks = new ArrayList<>();
      for (int k = 0; k < 1000; k++) {
        LatchCallback cb = new LatchCallback();
        callbacks.add(cb);
        streamback.get().send("foo", null, "{}", cb);
        if (k % 50 == 0) {
          // do this so there are multiple
          cb.awaitJustSuccess();
        }
      }
      for(LatchCallback cb : callbacks) {
        cb.awaitJustSuccess();
      }
      int attempts = 1000;
      while (Json.parseJsonObject(streamback.get(streamback.size() - 1)).get("seq").intValue() < 1000 && attempts > 0) {
        System.err.println(streamback.get(streamback.size() - 1));
        Thread.sleep(25);
        attempts--;
      }
      Assert.assertTrue(streamback.size() < 1050);
      Assert.assertTrue(streambackViewer.size() < 100);
    } finally {
      service.shutdown();
      executor.shutdown();
    }
  }

}

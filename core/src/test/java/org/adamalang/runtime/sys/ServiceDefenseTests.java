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
import org.adamalang.runtime.data.mocks.SimpleIntCallback;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Test;

public class ServiceDefenseTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@static { create { return true; } } public int x; @connected { x += 1; return true; } @disconnected { x -= 1; } message M {} channel foo(M y) { x += 1000; }";

  @Test
  public void reject_send() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", "1", created);
      created.await_success();

      service.shield.canSendMessageExisting.set(false);
      MockStreamback streamback1 = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", streamback1);
      streamback1.await_began();
      {
        SimpleIntCallback failedSend = new SimpleIntCallback();
        streamback1.get().send("channel", "marker", "{}", failedSend);
        failedSend.assertFailure(199883);
      }
      {
        SimpleIntCallback failedAttach = new SimpleIntCallback();
        streamback1.get().canAttach(new Callback<Boolean>() {
          @Override
          public void success(Boolean value) {
            failedAttach.success(0);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            failedAttach.failure(ex);
          }
        });
        failedAttach.assertFailure(199883);
      }

    } finally {
      service.shutdown();
    }
  }

  @Test
  public void reject_connect() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", "1", created);
      created.await_success();

      service.shield.canConnectExisting.set(false);
      MockStreamback streamback1 = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", streamback1);
      streamback1.await_failure(183498);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void reject_connect_new() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", "1", created);
      created.await_success();
      service.shield.canConnectNew.set(false);
      MockStreamback streamback1 = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", streamback1);
      streamback1.await_failure(146631);
    } finally {
      service.shutdown();
    }
  }

}

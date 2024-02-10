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
import org.adamalang.runtime.mocks.MockBackupService;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ServiceBadCodeTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String BAD_CODE = "@can_attach { int x = 1; while(true) { x++; } return true; } @attached(what) { while(true) {} } @static { create { return true; } } @connected { return true; } message M {} channel foo(M y) { while(true) {} }  ";

  @Test
  public void bad_code() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(BAD_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockFailureDataService failureDataService = new MockFailureDataService();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory,(bill) -> {},  new MockMetricsReporter(), dataService, new MockBackupService(), time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", "1", created);
      created.await_success();

      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();
      CountDownLatch latch = new CountDownLatch(2);
      streamback.get().canAttach(new Callback<Boolean>() {
        @Override
        public void success(Boolean value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.assertEquals(146569, ex.code);
          latch.countDown();
        }
      });
      NtAsset a = NtAsset.NOTHING;
      streamback.get().attach(a.id, a.name, a.contentType, a.size, a.md5, a.sha384, new Callback<Integer>() {
        @Override
        public void success(Integer value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.assertEquals(950384, ex.code);
          latch.countDown();
        }
      });
      streamback.get().send("foo", "marker", "{}", new Callback<Integer>() {
        @Override
        public void success(Integer value) {
        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.assertEquals(950384, ex.code);
          latch.countDown();
        }
      });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    } finally {
      service.shutdown();
    }
  }
}

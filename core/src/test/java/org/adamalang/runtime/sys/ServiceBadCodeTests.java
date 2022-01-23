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

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ServiceBadCodeTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String BAD_CODE = "@can_attach(who) { int x = 1; while(true) { x++; } return true; } @attached(who, what) { while(true) {} } @static { create(who) { return true; } } @connected(who) { return true; } message M {} channel foo(M y) { while(true) {} }  ";

  @Test
  public void bad_code() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(BAD_CODE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockFailureDataService failureDataService = new MockFailureDataService();
    MockInstantDataService realDataService = new MockInstantDataService();
    MockDelayDataService dataService = new MockDelayDataService(realDataService);
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(NtClient.NO_ONE, KEY, "{}", "1", created);
      created.await_success();

      MockStreamback streamback = new MockStreamback();
      service.connect(NtClient.NO_ONE, KEY, streamback);
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
      streamback.get().attach(NtAsset.NOTHING, new Callback<Integer>() {
        @Override
        public void success(Integer value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.assertEquals(904318, ex.code);
          latch.countDown();
        }
      });
      streamback.get().send("foo", "marker", "{}", new Callback<Integer>() {
        @Override
        public void success(Integer value) {
        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.assertEquals(904318, ex.code);
          latch.countDown();
        }
      });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    } finally {
      service.shutdown();
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.mocks.SimpleIntCallback;
import org.adamalang.runtime.data.mocks.SimpleStringCallback;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Test;

public class ServiceAuthTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@static { create { return true; } }" +
          "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }" +
          "private string password_plain = \"password\";" +
          "@authorize(user, pw) { if (pw == password_plain) { return \"yes\"; } abort; }" +
          "@password(new_password) { password_plain = new_password; }";

  @Test
  public void flow() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      {
        SimpleStringCallback cb = new SimpleStringCallback();
        service.authorize("origin", "0.0.0.0", KEY, "user", "nope", null, cb);
        cb.assertFailure(191713);
      }
      {
        SimpleStringCallback cb = new SimpleStringCallback();
        service.authorize("origin", "0.0.0.0", KEY, "user", "password", null, cb);
        cb.assertSuccess("yes");
      }
      {
        SimpleStringCallback cb = new SimpleStringCallback();
        service.authorize("origin", "0.0.0.0", KEY, "user", "password", "next-password", cb);
        cb.assertSuccess("yes");
      }
      {
        SimpleStringCallback cb = new SimpleStringCallback();
        service.authorize("origin", "0.0.0.0", KEY, "user", "password", null, cb);
        cb.assertFailure(191713);
      }
      {
        SimpleStringCallback cb = new SimpleStringCallback();
        service.authorize("origin", "0.0.0.0", KEY, "user", "next-password", null, cb);
        cb.assertSuccess("yes");
      }
    } finally {
      service.shutdown();
    }
  }
}

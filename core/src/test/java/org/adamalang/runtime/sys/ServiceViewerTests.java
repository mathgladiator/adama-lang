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
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.mocks.MockStreamback;
import org.adamalang.runtime.sys.mocks.NullCallbackLatch;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class ServiceViewerTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_V = "@static { create { return true; } }" +
      "@connected { return true; }" +
      "view int x;" + //
      "bubble my_x = @viewer.x;";

  @Test
  public void viewer_updates() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_V, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_success();
      MockStreamback streamback = new MockStreamback();
      Runnable latch1 = streamback.latchAt(2);
      Runnable latch2 = streamback.latchAt(3);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{\"x\":42}", null, streamback);
      latch1.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"my_x\":42},\"seq\":4}", streamback.get(1));
      streamback.get().update("{\"x\":5050}");
      latch2.run();
      Assert.assertEquals("{\"data\":{\"my_x\":5050},\"seq\":5}", streamback.get(2));
    } finally {
      service.shutdown();
    }
  }
}

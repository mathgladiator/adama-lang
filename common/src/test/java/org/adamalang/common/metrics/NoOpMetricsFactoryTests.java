/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.metrics;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class NoOpMetricsFactoryTests {
  @Test
  public void coverage() {
    NoOpMetricsFactory factory = new NoOpMetricsFactory();
    RequestResponseMonitor.RequestResponseMonitorInstance rr = factory.makeRequestResponseMonitor("x").start();
    rr.success();
    rr.failure(1);
    rr.extra();
    StreamMonitor.StreamMonitorInstance s = factory.makeStreamMonitor("y").start();
    s.progress();
    s.failure(-1);
    s.finish();
    CallbackMonitor cb = factory.makeCallbackMonitor("z");
    AtomicInteger sum = new AtomicInteger(0);
    Callback<String> instance = cb.wrap(new Callback<String>() {
      @Override
      public void success(String value) {
        sum.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        sum.incrementAndGet();
        sum.incrementAndGet();
      }
    });
    instance.success("x");
    Assert.assertEquals(1, sum.get());
    instance.failure(new ErrorCodeException(-1));
    Assert.assertEquals(3, sum.get());
    factory.counter("z").run();
    factory.inflight("z").down();
    factory.inflight("z").up();
    factory.inflight("z").set(1);
    factory.makeItemActionMonitor("item").start().executed();
    factory.makeItemActionMonitor("item").start().rejected();
    factory.makeItemActionMonitor("item").start().timeout();
  }
}

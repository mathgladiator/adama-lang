/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
  }
}

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
    factory.page("page", "title");
    factory.section("section");
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

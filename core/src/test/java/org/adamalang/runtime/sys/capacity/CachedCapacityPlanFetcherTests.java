/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.sys.capacity;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.json.JsonStreamReader;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CachedCapacityPlanFetcherTests {
  @Test
  public void flow() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("x");
    AtomicBoolean alive = new AtomicBoolean(true);
    try {
      MockCapacityPlanFetcher mock = new MockCapacityPlanFetcher();
      mock.plans.put("a", new CapacityPlan(new JsonStreamReader("{\"min\":42}")));
      CachedCapacityPlanFetcher finder = new CachedCapacityPlanFetcher(TimeSource.REAL_TIME, 100, 100000, executor, mock);
      CountDownLatch latch = new CountDownLatch(2);
      finder.fetch("host", new Callback<>() {
        @Override
        public void success(CapacityPlan value) {
        }

        @Override
        public void failure(ErrorCodeException ex) {
          latch.countDown();
        }
      });
      finder.fetch("a", new Callback<>() {
        @Override
        public void success(CapacityPlan value) {
          Assert.assertEquals(42, value.minimum);
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
        }
      });
      finder.startSweeping(alive, 5, 10);
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    } finally {
      alive.set(false);
      executor.shutdown();
    }
  }
}

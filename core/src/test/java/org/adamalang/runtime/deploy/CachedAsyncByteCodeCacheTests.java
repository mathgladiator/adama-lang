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
package org.adamalang.runtime.deploy;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class CachedAsyncByteCodeCacheTests {
  @Test
  public void flow() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("now");
    AtomicBoolean alive = new AtomicBoolean(true);
    try {
      CachedAsyncByteCodeCache cache = new CachedAsyncByteCodeCache(TimeSource.REAL_TIME, 100, 10000, executor, AsyncByteCodeCache.DIRECT);
      cache.startSweeping(alive, 5, 10);
      DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\"}", (t, errorCode) -> {});
      Assert.assertTrue(AsyncCompilerTests.pump(null, plan, cache) instanceof DeploymentFactory);
    } finally {
      alive.set(false);
      executor.shutdown();
    }
  }
}

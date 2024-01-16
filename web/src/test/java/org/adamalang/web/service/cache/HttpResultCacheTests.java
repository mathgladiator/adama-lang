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
package org.adamalang.web.service.cache;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.template.tree.T;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.contracts.HttpHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class HttpResultCacheTests {
  @Test
  public void expiry_get() {
    AtomicLong time = new AtomicLong(0);
    TimeSource mock = new TimeSource() {
      @Override
      public long nowMilliseconds() {
        return time.get();
      }
    };
    HttpResultCache cache = new HttpResultCache(mock);
    cache.inject("key").accept(1000, new HttpHandler.HttpResult(200, "space", "key", NtAsset.NOTHING, null, false, 0));
    Assert.assertEquals("space", cache.get("key").space);
    time.set(999);
    Assert.assertEquals("space", cache.get("key").space);
    time.set(1001);
    Assert.assertNull(cache.get("key"));
    Assert.assertNull(cache.get("key"));
  }

  @Test
  public void expiry_sweep() {
    AtomicLong time = new AtomicLong(0);
    TimeSource mock = new TimeSource() {
      @Override
      public long nowMilliseconds() {
        return time.get();
      }
    };
    HttpResultCache cache = new HttpResultCache(mock);
    cache.inject("key").accept(1000, new HttpHandler.HttpResult(200, "space", "key", NtAsset.NOTHING, null, false, 0));
    Assert.assertEquals("space", cache.get("key").space);
    Assert.assertEquals(0, cache.sweep());
    time.set(999);
    Assert.assertEquals(0, cache.sweep());
    time.set(1001);
    Assert.assertEquals(1, cache.sweep());
    Assert.assertNull(cache.get("key"));
    Assert.assertNull(cache.get("key"));
  }

  @Test
  public void expiry_sweeper() throws Exception {
    HttpResultCache cache = new HttpResultCache(TimeSource.REAL_TIME);
    SimpleExecutor executor = SimpleExecutor.create("time");
    try {
      AtomicBoolean alive = new AtomicBoolean(true);
      HttpResultCache.sweeper(executor, alive, cache, 10, 20);
      cache.inject("key").accept(100, new HttpHandler.HttpResult(200, "space", "key", NtAsset.NOTHING, null, false, 0));
      while (cache.get("key") != null) {
        System.out.println("poll");
        Thread.sleep(5);
      }
      alive.set(false);

    } finally {
      executor.shutdown();
    }
  }
}

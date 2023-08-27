/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
    cache.inject("key").accept(1000, new HttpHandler.HttpResult("space", "key", NtAsset.NOTHING, false));
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
    cache.inject("key").accept(1000, new HttpHandler.HttpResult("space", "key", NtAsset.NOTHING, false));
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
      cache.inject("key").accept(100, new HttpHandler.HttpResult("space", "key", NtAsset.NOTHING, false));
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

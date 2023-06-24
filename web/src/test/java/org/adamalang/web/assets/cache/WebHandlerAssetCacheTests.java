/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.assets.cache;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.MockAssetStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class WebHandlerAssetCacheTests {
  @Test
  public void memory_policy() {
    Assert.assertTrue(WebHandlerAssetCache.policyCacheMemory(new NtAsset(null, null, "text/html", 40, null, null)));
    Assert.assertFalse(WebHandlerAssetCache.policyCacheMemory(new NtAsset(null, null, "text/nope", 40, null, null)));
    Assert.assertFalse(WebHandlerAssetCache.policyCacheMemory(new NtAsset(null, null, "text/html", 1024 * 1024 * 1024, null, null)));
  }

  @Test
  public void disk_policy() {
    Assert.assertTrue(WebHandlerAssetCache.policyCacheDisk(new NtAsset(null, null, "text/none", 40, null, null)));
    Assert.assertFalse(WebHandlerAssetCache.policyCacheDisk(new NtAsset(null, null, "text/html", 1024 * 1024 * 1024, null, null)));
  }

  @Test
  public void can() {
    Assert.assertTrue(WebHandlerAssetCache.canCache(new NtAsset(null, null, "text/html", 40, null, null)));
    Assert.assertTrue(WebHandlerAssetCache.canCache(new NtAsset(null, null, "text/nope", 40, null, null)));
    Assert.assertFalse(WebHandlerAssetCache.canCache(new NtAsset(null, null, "text/html", 1024 * 1024 * 1024, null, null)));
  }

  @Test
  public void flow() throws Exception {
    File root = File.createTempFile("adamafcat", "001");
    root.delete();
    Assert.assertTrue(root.mkdir());
    try {
      WebHandlerAssetCache cache = new WebHandlerAssetCache(TimeSource.REAL_TIME, root);
      CountDownLatch gotBoth = new CountDownLatch(2);
      cache.failure(new NtAsset(null, null, "text/html", 40, null, null));
      cache.get(new NtAsset("id-1", "name-1", "text/html", 40, "", ""), new Callback<CachedAsset>() {
        @Override
        public void success(CachedAsset value) {
          gotBoth.countDown();
          value.attachWhileInExecutor(new MockAssetStream()).failure(42);
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      cache.get(new NtAsset("id-2", "name-2", "text/none", 512 * 1024, "", ""), new Callback<CachedAsset>() {
        @Override
        public void success(CachedAsset value) {
          gotBoth.countDown();
          value.attachWhileInExecutor(new MockAssetStream()).failure(42);
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
    } finally {
      for (File x : root.listFiles()) {
        x.delete();
      }
      root.delete();
    }
  }
}

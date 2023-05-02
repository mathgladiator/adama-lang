/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.assets.cache;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.cache.AsyncSharedLRUCache;
import org.adamalang.common.cache.SyncCacheLRU;
import org.adamalang.runtime.natives.NtAsset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

/** the asset cache for the web handler */
public class WebHandlerAssetCache {
  private static final Logger LOG = LoggerFactory.getLogger(WebHandlerAssetCache.class);
  private final SimpleExecutor memoryExecutor;
  private final SimpleExecutor fileExecutor;
  private final SyncCacheLRU<NtAsset, CachedAsset> memoryCache;
  private final AsyncSharedLRUCache<NtAsset, CachedAsset> memoryAsync;
  private final SyncCacheLRU<NtAsset, CachedAsset> fileCache;
  private final AsyncSharedLRUCache<NtAsset, CachedAsset> fileAsync;
  private final AtomicLong localId;

  public WebHandlerAssetCache(TimeSource time, File cacheRoot) {
    this.memoryExecutor = SimpleExecutor.create("webhandle-memcache");
    this.memoryCache = new SyncCacheLRU<>(time, 10, 2000, 64 * 1024 * 1024L, 60000, (key, mem) -> {
      mem.evict();
    });
    this.memoryAsync = new AsyncSharedLRUCache<>(memoryExecutor, memoryCache, (asset, cb) -> {
      cb.success(new MemoryCacheAsset(asset, memoryExecutor));
    });
    this.fileExecutor = SimpleExecutor.create("webhandle-file");
    this.fileCache = new SyncCacheLRU<>(time, 25, 500, 1024 * 1024 * 1024L, 10 * 60000, (key, file) -> {
      file.evict();
    });
    this.localId = new AtomicLong(0);
    for (File prior : cacheRoot.listFiles((dir, name) -> name.endsWith(".cache") && name.startsWith("asset."))) {
      prior.delete();
    }
    this.fileAsync = new AsyncSharedLRUCache<>(fileExecutor, fileCache, (asset, cb) -> {
      try {
        cb.success(new FileCacheAsset(localId.getAndIncrement(), cacheRoot, asset, fileExecutor));
      } catch (ErrorCodeException ece) {
        cb.failure(ece);
      }
    });
  }

  public static boolean policyCacheMemory(NtAsset asset) {
    // TODO: expand policy
    boolean isHtml = asset.contentType.equals("text/html");
    if (asset.size < 128 * 1024 && isHtml) {
      return true;
    }
    return false;
  }

  public static boolean policyCacheDisk(NtAsset asset) {
    return asset.size < 4 * 1024 * 1024;
  }

  public static boolean canCache(NtAsset asset) {
    return policyCacheMemory(asset) || policyCacheDisk(asset);
  }

  public void get(NtAsset asset, Callback<CachedAsset> callback) {
    if (policyCacheMemory(asset)) {
      memoryAsync.get(asset, callback);
    } else {
      fileAsync.get(asset, callback);
    }
  }

  public void failure(NtAsset asset) {
    memoryAsync.forceEvictionFromCacheNoDownstreamEviction(asset);
    fileAsync.forceEvictionFromCacheNoDownstreamEviction(asset);
  }
}

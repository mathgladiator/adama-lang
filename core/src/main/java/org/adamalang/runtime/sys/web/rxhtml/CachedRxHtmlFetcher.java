/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web.rxhtml;

import org.adamalang.common.Callback;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.cache.AsyncSharedLRUCache;
import org.adamalang.common.cache.SyncCacheLRU;

import java.util.concurrent.atomic.AtomicBoolean;

public class CachedRxHtmlFetcher implements RxHtmlFetcher {
  private final SyncCacheLRU<String, LiveSiteRxHtmlResult> storage;
  private final AsyncSharedLRUCache<String, LiveSiteRxHtmlResult> cache;

  public CachedRxHtmlFetcher(TimeSource timeSource, int maxSites, long maxAge, SimpleExecutor executor, RxHtmlFetcher fetcher) {
    this.storage = new SyncCacheLRU<>(timeSource, 0, maxSites, 16 * 1024 * 1024, maxAge, (name, record) -> {
    });
    this.cache = new AsyncSharedLRUCache<>(executor, storage, fetcher::fetch);
  }

  public void startSweeping(AtomicBoolean alive, int periodMinimumMs, int periodMaximumMs) {
    this.cache.startSweeping(alive, periodMinimumMs, periodMaximumMs);
  }

  @Override
  public void fetch(String space, Callback<LiveSiteRxHtmlResult> callback) {
    cache.get(space, callback);
  }
}

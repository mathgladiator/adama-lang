/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.domains;

import org.adamalang.common.Callback;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.cache.AsyncSharedLRUCache;
import org.adamalang.common.cache.SyncCacheLRU;

import java.util.concurrent.atomic.AtomicBoolean;

/** cache domains for faster access */
public class CachedDomainFinder implements DomainFinder {
  private final SyncCacheLRU<String, Domain> storage;
  private final AsyncSharedLRUCache<String, Domain> cache;

  public CachedDomainFinder(TimeSource timeSource, int maxDomains, long maxAge, SimpleExecutor executor, DomainFinder finder) {
    this.storage = new SyncCacheLRU<>(timeSource, 0, maxDomains, 1024 * maxDomains, maxAge, (name, record) -> {
    });
    this.cache = new AsyncSharedLRUCache<>(executor, storage, finder::find);
  }

  public void startSweeping(AtomicBoolean alive, int periodMinimumMs, int periodMaximumMs) {
    this.cache.startSweeping(alive, periodMinimumMs, periodMaximumMs);
  }

  @Override
  public void find(String domain, Callback<Domain> callback) {
    cache.get(domain, callback);
  }
}

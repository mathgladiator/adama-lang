package org.adamalang.runtime.sys.domains;

import org.adamalang.common.Callback;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.cache.AsyncSharedLRUCache;
import org.adamalang.common.cache.SyncCacheLRU;

/** cache domains for faster access */
public class CachedDomainFinder implements DomainFinder {
  private final SyncCacheLRU<String, Domain> storage;
  private final AsyncSharedLRUCache<String, Domain> cache;

  public CachedDomainFinder(TimeSource timeSource, int maxDomains, long maxAge, SimpleExecutor executor, DomainFinder finder) {
    this.storage = new SyncCacheLRU<>(timeSource, 0, maxDomains, 1024 * maxDomains, maxAge, (name, record) -> {
    });
    this.cache = new AsyncSharedLRUCache<>(executor, storage, finder::find);
  }

  @Override
  public void find(String domain, Callback<Domain> callback) {
    cache.get(domain, callback);
  }
}

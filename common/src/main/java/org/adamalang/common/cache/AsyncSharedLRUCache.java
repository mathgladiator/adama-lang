/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.cache;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

/** wrap a SyncCacheLRU */
public class AsyncSharedLRUCache<D, R extends Measurable> {
  public final SimpleExecutor executor;
  public final SyncCacheLRU<D, R> cache;
  public final BiConsumer<D, Callback<R>> resolver;
  public final HashMap<D, ArrayList<Callback<R>>> inflight;

  public AsyncSharedLRUCache(SimpleExecutor executor, SyncCacheLRU<D, R> cache, BiConsumer<D, Callback<R>> resolver) {
    this.executor = executor;
    this.cache = cache;
    this.resolver = resolver;
    this.inflight = new HashMap<>();
  }

  public void get(D key, Callback<R> callback) {
    executor.execute(new NamedRunnable("ascache-get") {
      @Override
      public void execute() throws Exception {
        R cachedValue = cache.get(key);
        if (cachedValue != null) {
          callback.success(cachedValue);
          return;
        }

        ArrayList<Callback<R>> recent = inflight.get(key);
        if (recent == null) {
          recent = new ArrayList<>();
          inflight.put(key, recent);
          resolver.accept(key, new Callback<R>() {
            @Override
            public void success(R value) {
              executor.execute(new NamedRunnable("ascache-success") {
                @Override
                public void execute() throws Exception {
                  cache.add(key, value);
                  for (Callback<R> cb : inflight.remove(key)) {
                    cb.success(value);
                  }
                }
              });
            }

            @Override
            public void failure(ErrorCodeException ex) {
              executor.execute(new NamedRunnable("ascache-failure") {
                @Override
                public void execute() throws Exception {
                  for (Callback<R> cb : inflight.remove(key)) {
                    cb.failure(ex);
                  }
                }
              });
            }
          });
        }
        recent.add(callback);
      }
    });
  }

  public void forceEvictionFromCacheNoDownstreamEviction(D key) {
    executor.execute(new NamedRunnable("evict") {
      @Override
      public void execute() throws Exception {
        cache.forceEvictionFromCacheNoDownstreamEviction(key);
      }
    });
  }
}

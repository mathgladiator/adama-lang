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
package org.adamalang.common.cache;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/** wrap a SyncCacheLRU */
public class AsyncSharedLRUCache<D, R extends Measurable> {
  public final SimpleExecutor executor;
  public final SyncCacheLRU<D, R> cache;
  public final BiConsumer<D, Callback<R>> resolver;
  public final HashMap<D, ArrayList<Callback<R>>> inflight;
  private final Random rng;

  public AsyncSharedLRUCache(SimpleExecutor executor, SyncCacheLRU<D, R> cache, BiConsumer<D, Callback<R>> resolver) {
    this.executor = executor;
    this.cache = cache;
    this.resolver = resolver;
    this.inflight = new HashMap<>();
    this.rng = new Random();
  }

  public void startSweeping(AtomicBoolean alive, int periodMinimumMs, int periodMaximumMs) {
    final int periodRange = Math.max(periodMaximumMs - periodMinimumMs, 10);
    executor.schedule(new NamedRunnable("async") {
      @Override
      public void execute() throws Exception {
        cache.sweep();
        if (alive.get()) {
          executor.schedule(this, periodMinimumMs + rng.nextInt(periodRange));
        }
      }
    }, periodMinimumMs);
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
                  if (value != null) {
                    cache.add(key, value);
                  }
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

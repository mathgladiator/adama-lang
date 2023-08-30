/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.pool;

import org.adamalang.common.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/** An async pool designed for asynchronous connection management */
public class AsyncPool<R, S> {
  private final SimpleExecutor executor;
  private final TimeSource time;
  private final int maxLifetimeMilliseconds;
  private final int maxUsageCount;
  private final HashMap<R, Pool<RefS>> pools;
  private final PoolActions<R, S> actions;
  private final int maxPoolSize;
  private final int errorCodePoolTooLarge;

  public AsyncPool(SimpleExecutor executor, TimeSource time, int maxLifetimeMilliseconds, int maxUsageCount, int maxPoolSize, int errorCodePoolTooLarge, PoolActions<R, S> actions) {
    this.executor = executor;
    this.time = time;
    this.maxLifetimeMilliseconds = maxLifetimeMilliseconds;
    this.maxUsageCount = maxUsageCount;
    this.pools = new HashMap<>();
    this.actions = actions;
    this.maxPoolSize = maxPoolSize;
    this.errorCodePoolTooLarge = errorCodePoolTooLarge;
  }

  public void scheduleSweeping(AtomicBoolean alive) {
    executor.schedule(new NamedRunnable("sweep") {
      @Override
      public void execute() throws Exception {
        sweepInExecutor();
        if (alive.get()) {
          executor.schedule(this, maxLifetimeMilliseconds);
        }
      }
    }, maxLifetimeMilliseconds);
  }

  protected int sweepInExecutor() {
    int cleaned = 0;
    long now = time.nowMilliseconds();
    Iterator<Map.Entry<R, Pool<RefS>>> itMap = pools.entrySet().iterator();
    while (itMap.hasNext()) {
      Map.Entry<R, Pool<RefS>> entry = itMap.next();
      Pool<RefS> pool = entry.getValue();
      Iterator<RefS> itValue = pool.iterator();
      while (itValue.hasNext()) {
        RefS candidate = itValue.next();
        long age = now - candidate.created;
        if (age >= maxLifetimeMilliseconds) {
          pool.bumpDown();
          itValue.remove();
          actions.destroy(candidate.item);
          cleaned++;
        }
      }
      if (pool.size() == 0) {
        itMap.remove();
      }
    }
    return cleaned;
  }

  public void get(R request, Callback<PoolItem<S>> callback) {
    executor.execute(new NamedRunnable("async-pool") {
      @Override
      public void execute() throws Exception {
        // get the pool for the reuqest
        Pool<RefS> pool = poolOfWhileInExecutor(request);
        // start removing items from the pool
        RefS item;
        while ((item = pool.next()) != null) {
          long age = time.nowMilliseconds() - item.created;
          // if the item is young enough, then we found it
          if (age < maxLifetimeMilliseconds) {
            callback.success(item);
            return;
          } else {
            // otherwise, the item is tool old so we terminate it and move on to the next item
            pool.bumpDown();
            actions.destroy(item.item);
          }
        }
        // the pool is too big, so reject the request outright
        if (pool.size() >= maxPoolSize) {
          callback.failure(new ErrorCodeException(errorCodePoolTooLarge));
          return;
        }
        // while in the executor, inform the pool that we are creating a new item, so account for it.
        pool.bumpUp();
        actions.create(request, new Callback<S>() {
          @Override
          public void success(S value) {
            // it was a happy thing, so tell the client (it is accounted for within the counter)
            callback.success(new RefS(pool, value));
          }

          @Override
          public void failure(ErrorCodeException ex) {
            executor.execute(new NamedRunnable("init-pool-failure") {
              @Override
              public void execute() throws Exception {
                // we must jump back into the executor to account for the loss of the item's potential
                pool.bumpDown();
                callback.failure(ex);
              }
            });
          }
        });
      }
    });
  }

  private Pool<RefS> poolOfWhileInExecutor(R request) {
    Pool<RefS> pool = pools.get(request);
    if (pool != null) {
      return pool;
    }
    pool = new Pool<>();
    pools.put(request, pool);
    return pool;
  }

  /** an item within the pool */
  private class RefS implements PoolItem<S> {
    private final Pool<RefS> pool; // the pool the item came from
    private final S item; // the item being tracked
    private final long created; // when we created the item
    private int count; // the number of times we have used it

    public RefS(Pool<RefS> pool, S item) {
      this.pool = pool;
      this.item = item;
      this.created = time.nowMilliseconds();
      this.count = 0;
    }

    @Override
    public S item() {
      return this.item;
    }

    @Override
    public void signalFailure() {
      executor.execute(new NamedRunnable("failure-async-pool") {
        @Override
        public void execute() throws Exception {
          pool.bumpDown();
          actions.destroy(item);
        }
      });
    }

    @Override
    public void returnToPool() {
      executor.execute(new NamedRunnable("return-async-pool") {
        @Override
        public void execute() throws Exception {
          count++;
          if (count >= maxUsageCount) {
            pool.bumpDown();
            actions.destroy(item);
          } else {
            pool.add(RefS.this);
          }
        }
      });
    }
  }
}

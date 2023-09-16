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

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.web.contracts.HttpHandler;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/** a very dumb temporal cache; no science */
public class HttpResultCache {
  public class Item {
    private final long at;
    private final HttpHandler.HttpResult result;

    public Item(long at, HttpHandler.HttpResult result) {
      this.at = at;
      this.result = result;
    }
  }

  private final TimeSource time;
  private final ConcurrentHashMap<String, Item> cache;

  public HttpResultCache(TimeSource time) {
    this.time = time;
    this.cache = new ConcurrentHashMap<>();
  }

  public BiConsumer<Integer, HttpHandler.HttpResult> inject(String key) {
    return (ttl, result) -> {
      cache.put(key, new Item(time.nowMilliseconds() + ttl, result));
    };
  }

  public HttpHandler.HttpResult get(String key) {
    Item item = cache.get(key);
    if (item == null) {
      return null;
    }

    if (item.at <= time.nowMilliseconds()) {
      cache.remove(key);
      return null;
    }
    return item.result;
  }

  public int sweep() {
    int count = 0;
    long now = time.nowMilliseconds();
    Iterator<Item> it = cache.values().iterator();
    while (it.hasNext()) {
      if (it.next().at <= now) {
        it.remove();
        count++;
      }
    }
    return count;
  }

  public static void sweeper(SimpleExecutor executor, AtomicBoolean alive, HttpResultCache cache, int minSweepMs, int maxSweepMs) {
    final Random rng = new Random();
    executor.schedule(new NamedRunnable("sweep cache") {
      @Override
      public void execute() throws Exception {
        if (alive.get()) {
          cache.sweep();
          executor.schedule(this, rng.nextInt(maxSweepMs - minSweepMs) + minSweepMs);
        }
      }
    }, rng.nextInt(maxSweepMs - minSweepMs) + minSweepMs);
  }
}

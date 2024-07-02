/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import org.adamalang.common.TimeSource;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/** This is traditional Least Recently Used cache where items in the cached are measured (i.e. memory size or storage bytes) and expire based on time. This class is not thread safe. */
public class SyncCacheLRU<D, R extends Measurable> {
  private final TimeSource time;
  private final LinkedHashMap<D, CacheEntry<R>> cache;
  private final long ageMillisecondLimit;
  private final BiConsumer<D, R> evict;
  private long measure;

  /**
   * @param time the time source for knowing what time it is
   * @param minSize the minimum number of items to hold within a cache (useful when a big item comes in and evicts... everything
   * @param maxSize the maximum number of items to hold in the cache
   * @param measureLimit the maximum measure for the cache to take on (i.e. what physical limit do we care about)
   * @param ageMillisecondLimit the maximum age of an item within the cache
   * @param evict an event when a key is evicted
   */
  public SyncCacheLRU(TimeSource time, int minSize, int maxSize, long measureLimit, long ageMillisecondLimit, BiConsumer<D, R> evict) {
    this.time = time;
    this.ageMillisecondLimit = ageMillisecondLimit;
    this.evict = evict;
    this.cache = new LinkedHashMap<>(maxSize, 0.75f, true) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<D, CacheEntry<R>> eldest) {
        // this ensures a minimum number of items in the cache; this useful to prevent an entire cache blowout due to a large item
        if (size() <= minSize) {
          return false;
        }
        // measure the age of the item
        long age = time.nowMilliseconds() - eldest.getValue().timestamp;
        // evict the eldest on size, too big, and too old
        if (size() > maxSize || measure > measureLimit || age > ageMillisecondLimit) {
          removed(eldest);
          return true;
        }
        return false;
      }
    };
  }

  /** internal: an item was removed for some reason */
  private void removed(Map.Entry<D, CacheEntry<R>> entry) {
    this.measure -= entry.getValue().item.measure();
    evict.accept(entry.getKey(), entry.getValue().item);
  }

  /** add an item to the cache; will trigger evictions */
  public void add(D key, R item) {
    CacheEntry<R> entry = new CacheEntry<>(item, time.nowMilliseconds());
    this.measure += item.measure();
    cache.put(key, entry);
  }

  /** get an item from the cache */
  public R get(D key) {
    CacheEntry<R> entry = cache.get(key);
    if (entry == null) {
      return null;
    }
    return entry.item;
  }

  /** sweep the items in the cache to evict items that are too old */
  public void sweep() {
    Iterator<Map.Entry<D, CacheEntry<R>>> it = cache.entrySet().iterator();
    long now = time.nowMilliseconds();
    while (it.hasNext()) {
      Map.Entry<D, CacheEntry<R>> entry = it.next();
      long age = now - entry.getValue().timestamp;
      if (age >= ageMillisecondLimit) {
        removed(entry);
        it.remove();
      }
    }
  }

  /** how big is the cache; the sum of all items measured */
  public long measure() {
    return measure;
  }

  /** how many items are in the cache */
  public int size() {
    return cache.size();
  }

  public void forceEvictionFromCacheNoDownstreamEviction(D key) {
    CacheEntry<R> value = cache.remove(key);
    if (value != null) {
      this.measure -= value.item.measure();
    }
  }

  public static <D, R extends Measurable> BiConsumer<D, R> MAKE_NO_OP() {
    return (x, y) -> {};
  }
}

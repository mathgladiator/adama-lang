/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.cache;

import org.adamalang.common.TimeSource;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/** This is traditional Least Recently Used cache where items in the cached are measured (i.e. memory size or storage bytes) and expire based on time. This class is not thread safe. */
public class SyncCacheLRU<D, R extends Measurable> {
  private final TimeSource time;
  private final LinkedHashMap<D, CacheEntry<R>> cache;
  private final long ageMillisecondLimit;
  private final Consumer<D> evict;
  private long measure;

  /**
   *
   * @param time the time source for knowing what time it is
   * @param minSize the minimum number of items to hold within a cache (useful when a big item comes in and evicts... everything
   * @param maxSize the maximum number of items to hold in the cache
   * @param measureLimit the maximum measure for the cache to take on (i.e. what physical limit do we care about)
   * @param ageMillisecondLimit the maximum age of an item within the cache
   * @param evict an event when a key is evicted
   */
  public SyncCacheLRU(TimeSource time, int minSize, int maxSize, long measureLimit, long ageMillisecondLimit, Consumer<D> evict) {
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

  /** internal: an item was removed for some reason */
  private void removed(Map.Entry<D, CacheEntry<R>> entry) {
    this.measure -= entry.getValue().item.measure();
    evict.accept(entry.getKey());
  }
}

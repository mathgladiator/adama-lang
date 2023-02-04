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

import org.adamalang.common.gossip.MockTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class SyncCacheLRUTests {
  @Test
  public void evict_size() {
    MockTime time = new MockTime();
    ArrayList<String> evictions = new ArrayList<>();
    SyncCacheLRU cache = new SyncCacheLRU<String, MeasuredString>(time, 1, 10, 1024, 1000, (key, val) -> evictions.add(key));

    for (int k = 0; k < 15; k++) {
      cache.add("xyz-" + k, new MeasuredString("XYZ-" + k));
    }
    Assert.assertEquals(10, cache.size());
    Assert.assertEquals(5, evictions.size());
    Assert.assertEquals("xyz-0", evictions.get(0));
    Assert.assertEquals("xyz-1", evictions.get(1));
    Assert.assertEquals("xyz-2", evictions.get(2));
    Assert.assertEquals("xyz-3", evictions.get(3));
    Assert.assertEquals("xyz-4", evictions.get(4));
    Assert.assertNull(cache.get("x"));
  }

  @Test
  public void lru() {
    MockTime time = new MockTime();
    ArrayList<String> evictions = new ArrayList<>();
    SyncCacheLRU cache = new SyncCacheLRU<String, MeasuredString>(time, 1, 10, 1024, 1000, (key, val) -> evictions.add(key));

    for (int k = 0; k < 10; k++) {
      cache.add("xyz-" + k, new MeasuredString("XYZ-" + k));
    }
    for (int k = 0; k < 5; k++) {
      cache.get("xyz-" + k);
    }
    for (int k = 10; k < 15; k++) {
      cache.add("xyz-" + k, new MeasuredString("XYZ-" + k));
    }
    Assert.assertEquals(10, cache.size());
    Assert.assertEquals(5, evictions.size());
    Assert.assertEquals("xyz-5", evictions.get(0));
    Assert.assertEquals("xyz-6", evictions.get(1));
    Assert.assertEquals("xyz-7", evictions.get(2));
    Assert.assertEquals("xyz-8", evictions.get(3));
    Assert.assertEquals("xyz-9", evictions.get(4));
    Assert.assertNull(cache.get("x"));
  }

  @Test
  public void no_min_size_too_big() {
    MockTime time = new MockTime();
    ArrayList<String> evictions = new ArrayList<>();
    SyncCacheLRU cache = new SyncCacheLRU<String, MeasuredString>(time, 0, 10, 2, 1000, (key, val) -> evictions.add(key));
    for (int k = 0; k < 10;  k++) {
      cache.add("k-" + k, new MeasuredString("0123456789"));
    }
    Assert.assertEquals(0, cache.size());
    Assert.assertNull(cache.get("x"));
  }

  @Test
  public void min_size_big_items() {
    MockTime time = new MockTime();
    ArrayList<String> evictions = new ArrayList<>();
    SyncCacheLRU cache = new SyncCacheLRU<String, MeasuredString>(time, 5, 10, 2, 1000, (key, val) -> evictions.add(key));
    for (int k = 0; k < 10;  k++) {
      cache.add("k-" + k, new MeasuredString("0123456789"));
    }
    Assert.assertEquals(5, cache.size());
    Assert.assertEquals("k-0", evictions.get(0));
    Assert.assertEquals("k-1", evictions.get(1));
    Assert.assertEquals("k-2", evictions.get(2));
    Assert.assertEquals("k-3", evictions.get(3));
    Assert.assertEquals("k-4", evictions.get(4));
    Assert.assertNull(cache.get("x"));
  }

  @Test
  public void expiry_on_insert() {
    MockTime time = new MockTime();
    ArrayList<String> evictions = new ArrayList<>();
    SyncCacheLRU cache = new SyncCacheLRU<String, MeasuredString>(time, 0, 100, 1024, 500, (key, val) -> evictions.add(key));
    for (int k = 0; k < 10;  k++) {
      cache.add("k-" + k, new MeasuredString("0123456789"));
      time.currentTime += 1000;
    }
    Assert.assertEquals(10, cache.measure());
    Assert.assertEquals(1, cache.size());
    Assert.assertNull(cache.get("x"));
  }

  @Test
  public void expiry_sweep() {
    MockTime time = new MockTime();
    ArrayList<String> evictions = new ArrayList<>();
    SyncCacheLRU cache = new SyncCacheLRU<String, MeasuredString>(time, 0, 100, 1024, 500, (key, val) -> evictions.add(key));
    for (int k = 0; k < 10;  k++) {
      cache.add("k-" + k, new MeasuredString("0123456789"));
    }
    Assert.assertEquals(10, cache.size());
    Assert.assertEquals(100, cache.measure());
    time.currentTime += 1000;
    cache.sweep();
    Assert.assertEquals(0, cache.size());
    for (int k = 0; k < 10;  k++) {
      Assert.assertEquals("k-" + k, evictions.get(k));
    }
    Assert.assertEquals(0, cache.measure());
    Assert.assertNull(cache.get("x"));
  }

}

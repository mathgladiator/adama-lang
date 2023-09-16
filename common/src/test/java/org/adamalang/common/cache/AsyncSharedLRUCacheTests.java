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
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.gossip.MockTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AsyncSharedLRUCacheTests {
  @Test
  public void herd_success() throws Exception {
    MockTime time = new MockTime();
    ArrayList<String> evictions = new ArrayList<>();
    SyncCacheLRU cache = new SyncCacheLRU<String, MeasuredString>(time, 1, 10, 1024, 1000, (key, ms) -> evictions.add(key));
    SimpleExecutor executor = SimpleExecutor.create("test");
    try {
      AtomicInteger calls = new AtomicInteger(0);
      CountDownLatch latchFinish = new CountDownLatch(1);
      AtomicReference<Callback<MeasuredString>> cbRef = new AtomicReference<>();
      AsyncSharedLRUCache<String, MeasuredString> async = new AsyncSharedLRUCache<String, MeasuredString>(executor, cache, (key, cb) -> {
        calls.incrementAndGet();
        try {
          cbRef.set(cb);
          latchFinish.countDown();
        } catch (Exception ex) {
        }
      });
      CountDownLatch allIn = new CountDownLatch(10);
      for (int k = 0; k < 10; k++) {
        Callback<MeasuredString> cb = new Callback<MeasuredString>() {
          @Override
          public void success(MeasuredString value) {
            allIn.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
          }
        };
        async.get("X", cb);
      }
      Assert.assertTrue(latchFinish.await(10000, TimeUnit.MILLISECONDS));
      cbRef.get().success(new MeasuredString("XYZ"));
      async.forceEvictionFromCacheNoDownstreamEviction("Y"); // dumb
      Assert.assertTrue(allIn.await(50000, TimeUnit.MILLISECONDS));
      Assert.assertEquals(1, calls.get());
      for (int k = 0; k < 10; k++) {
        CountDownLatch happy = new CountDownLatch(1);
        async.get("X", new Callback<MeasuredString>() {
          @Override
          public void success(MeasuredString value) {
            Assert.assertEquals("XYZ", value.str);
            happy.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
          }
        });
        Assert.assertTrue(happy.await(10000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, cache.size());
      }
    } finally {
      executor.shutdown();
    }
  }

  @Test
  public void herd_failure() throws Exception {
    MockTime time = new MockTime();
    ArrayList<String> evictions = new ArrayList<>();
    SyncCacheLRU<String, MeasuredString> cache = new SyncCacheLRU<>(time, 1, 10, 1024, 1000, (key, ms) -> evictions.add(key));
    SimpleExecutor executor = SimpleExecutor.create("test");
    try {
      AtomicInteger calls = new AtomicInteger(0);
      CountDownLatch latchStart = new CountDownLatch(1);
      CountDownLatch latchFinish = new CountDownLatch(1);
      AtomicReference<Callback<MeasuredString>> cbRef = new AtomicReference<>();
      AsyncSharedLRUCache<String, MeasuredString> async = new AsyncSharedLRUCache<String, MeasuredString>(executor, cache, (key, cb) -> {
        calls.incrementAndGet();
        try {
          latchStart.await(60000, TimeUnit.MILLISECONDS);
          cbRef.set(cb);
          latchFinish.countDown();
        } catch (Exception ex) {
        }
      });
      CountDownLatch allIn = new CountDownLatch(10);
      for (int k = 0; k < 10; k++) {
        Callback<MeasuredString> cb = new Callback<MeasuredString>() {
          @Override
          public void success(MeasuredString value) {
          }

          @Override
          public void failure(ErrorCodeException ex) {
            allIn.countDown();
          }
        };
        async.get("X", cb);
      }
      latchStart.countDown();
      Assert.assertTrue(latchFinish.await(10000, TimeUnit.MILLISECONDS));
      cbRef.get().failure(new ErrorCodeException(123));
      Assert.assertTrue(allIn.await(50000, TimeUnit.MILLISECONDS));
      Assert.assertEquals(1, calls.get());
      Assert.assertEquals(0, cache.size());
    } finally {
      executor.shutdown();
    }
  }

  @Test
  public void dontcachenull() throws Exception {
    MockTime time = new MockTime();
    ArrayList<String> evictions = new ArrayList<>();
    SyncCacheLRU<String, MeasuredString> cache = new SyncCacheLRU<>(time, 1, 10, 1024, 1000, (key, ms) -> evictions.add(key));
    SimpleExecutor executor = SimpleExecutor.create("test");
    try {
      AtomicReference<Callback<MeasuredString>> cbRef = new AtomicReference<>();
      AsyncSharedLRUCache<String, MeasuredString> async = new AsyncSharedLRUCache<String, MeasuredString>(executor, cache, (key, cb) -> {
        cb.success(null);
      });
      CountDownLatch allIn = new CountDownLatch(10);
      for (int k = 0; k < 10; k++) {
        Callback<MeasuredString> cb = new Callback<MeasuredString>() {
          @Override
          public void success(MeasuredString value) {
            Assert.assertNull(value);
            allIn.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        };
        async.get("X", cb);
      }
      Assert.assertTrue(allIn.await(50000, TimeUnit.MILLISECONDS));
      Assert.assertEquals(0, cache.size());
    } finally {
      executor.shutdown();
    }
  }
}

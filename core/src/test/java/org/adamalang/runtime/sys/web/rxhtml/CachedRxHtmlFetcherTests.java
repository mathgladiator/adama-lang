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
package org.adamalang.runtime.sys.web.rxhtml;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.rxhtml.routing.Table;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CachedRxHtmlFetcherTests {
  @Test
  public void trivial() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("exe");
    try {
      MockRxHtmlFetcher mock = new MockRxHtmlFetcher();
      CachedRxHtmlFetcher fetcher = new CachedRxHtmlFetcher(TimeSource.REAL_TIME, 1000, 5 * 1000, executor, mock);
      CountDownLatch latch = new CountDownLatch(1);
      fetcher.fetch("trivial", new Callback<Table>() {
        @Override
        public void success(Table value) {
          System.err.println("Got one");
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    } finally {
      executor.shutdown();
    }
  }

  @Test
  public void expiry() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("exe");
    try {
      MockRxHtmlFetcher mock = new MockRxHtmlFetcher();
      CountDownLatch latch = new CountDownLatch(5);
      CachedRxHtmlFetcher fetcher = new CachedRxHtmlFetcher(TimeSource.REAL_TIME, 1000, 5, executor, new RxHtmlFetcher() {
        @Override
        public void fetch(String space, Callback<Table> callback) {
          latch.countDown();
          mock.fetch(space, callback);
        }
      });
      AtomicBoolean alive = new AtomicBoolean(true);
      fetcher.startSweeping(alive, 1, 2);
      long start = System.currentTimeMillis();
      while (latch.getCount() > 0 && (System.currentTimeMillis() - start) < 2000) {
        fetcher.fetch("trivial", new Callback<Table>() {
          @Override
          public void success(Table value) {
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        Thread.sleep(10);
      }
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    } finally {
      executor.shutdown();
    }
  }
}

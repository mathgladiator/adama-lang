/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web.rxhtml;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CachedRxHtmlFetcherTests {
  @Test
  public void trivial() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("exe");
    try {
      MockRxHtmlFetcher mock = new MockRxHtmlFetcher();
      CachedRxHtmlFetcher fetcher = new CachedRxHtmlFetcher(TimeSource.REAL_TIME, 1000, 5 * 1000, executor, mock);
      CountDownLatch latch = new CountDownLatch(1);
      fetcher.fetch("trivial", new Callback<LiveSiteRxHtmlResult>() {
        @Override
        public void success(LiveSiteRxHtmlResult value) {
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
}

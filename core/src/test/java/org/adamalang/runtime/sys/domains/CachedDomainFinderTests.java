/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.domains;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CachedDomainFinderTests {
  @Test
  public void passthrough() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("x");
    try {
      MockDomainFinder mock = new MockDomainFinder() //
          .with("host", new Domain("domain", 1, "space", "key", false, "", null, 123L));
      CachedDomainFinder finder = new CachedDomainFinder(TimeSource.REAL_TIME, 100, 100000, executor, mock);
      CountDownLatch latch = new CountDownLatch(1);
      finder.find("host", new Callback<Domain>() {
        @Override
        public void success(Domain value) {
          Assert.assertEquals("domain", value.domain);
          Assert.assertEquals("space", value.space);
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
  public void max() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("x");
    try {
      int N = 50000;
      MockDomainFinder mock = new MockDomainFinder();
      for (int k = 0; k < N; k++) {
        mock.with("host-" + k, new Domain("domain", 1, "space", "key", false, "", null, 123L));
      }
      CachedDomainFinder finder = new CachedDomainFinder(TimeSource.REAL_TIME, 100, 100000, executor, mock);
      CountDownLatch latch = new CountDownLatch(20000);
      for (int k = 0; k < N; k++) {
        finder.find("host-" +k, new Callback<Domain>() {
          @Override
          public void success(Domain value) {
            Assert.assertEquals("domain", value.domain);
            Assert.assertEquals("space", value.space);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
      }
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    } finally {
      executor.shutdown();
    }
  }
}

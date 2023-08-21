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
import java.util.concurrent.atomic.AtomicBoolean;

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


  @Test
  public void expiry() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("x");
    try {
      MockDomainFinder mock = new MockDomainFinder();
      mock.with("host", new Domain("domain", 1, "space", "key", false, "", null, 123L));
      CountDownLatch latch = new CountDownLatch(5);
      CachedDomainFinder finder = new CachedDomainFinder(TimeSource.REAL_TIME, 100, 5, executor, new DomainFinder() {
        @Override
        public void find(String domain, Callback<Domain> callback) {
          System.out.println("hit");
          latch.countDown();
          mock.find(domain, callback);
        }
      });
      AtomicBoolean alive = new AtomicBoolean(true);
      finder.startSweeping(alive, 1, 2);
      long start = System.currentTimeMillis();
      while (latch.getCount() > 0 && (System.currentTimeMillis() - start) < 2000) {
        finder.find("host", new Callback<Domain>() {
          @Override
          public void success(Domain value) {
            Assert.assertEquals("domain", value.domain);
            Assert.assertEquals("space", value.space);
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        Thread.sleep(10);
      }
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
      alive.set(false);
    } finally {
      executor.shutdown();
    }
  }
}

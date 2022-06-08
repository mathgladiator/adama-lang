/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.routing.finder;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.net.client.contracts.RoutingSubscriber;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FinderServiceRouterTests {

  @Test
  public void failure_retry_go_machine() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("finder-router");
    try {
      MockFinderService finder = new MockFinderService();
      MockMachinePicker picker = new MockMachinePicker();
      FinderServiceRouter router = new FinderServiceRouter(executor, finder, picker, "test-region");
      finder.bindLocal(new Key("space", "retry-key"));
      CountDownLatch latch = new CountDownLatch(1);
      router.subscribe(new Key("space", "retry-key"), new RoutingSubscriber() {
        @Override
        public void onRegion(String region) {
        }
        @Override
        public void failure(ErrorCodeException ex) {
        }
        @Override
        public void onMachine(String machine) {
          latch.countDown();
        }
      }, (runnable) -> {
      });
      Assert.assertTrue(latch.await(2500, TimeUnit.MILLISECONDS));
    } finally {
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }

  @Test
  public void failure_retry_go_region() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("finder-router");
    try {
      MockFinderService finder = new MockFinderService();
      MockMachinePicker picker = new MockMachinePicker();
      FinderServiceRouter router = new FinderServiceRouter(executor, finder, picker, "test-region");
      finder.bindOtherRegion(new Key("space", "retry-key"));
      CountDownLatch latch = new CountDownLatch(1);
      router.subscribe(new Key("space", "retry-key"), new RoutingSubscriber() {
        @Override
        public void onRegion(String region) {
          latch.countDown();
        }
        @Override
        public void failure(ErrorCodeException ex) {
        }
        @Override
        public void onMachine(String machine) {
        }
      }, (runnable) -> {
      });
      Assert.assertTrue(latch.await(2500, TimeUnit.MILLISECONDS));
    } finally {
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }
}

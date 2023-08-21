/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.routing.finder;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.net.client.contracts.RoutingCallback;
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
      MockFinderService finder = new MockFinderService("test-machine");
      MockMachinePicker picker = new MockMachinePicker();
      FinderServiceRouter router = new FinderServiceRouter(executor, finder, picker, "test-region");
      finder.bindLocal(new Key("space", "retry-key"));
      CountDownLatch latch = new CountDownLatch(1);
      router.get(new Key("space", "retry-key"), new RoutingCallback() {
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
      MockFinderService finder = new MockFinderService("test-machine");
      MockMachinePicker picker = new MockMachinePicker();
      FinderServiceRouter router = new FinderServiceRouter(executor, finder, picker, "test-region");
      finder.bindOtherRegion(new Key("space", "retry-key"));
      CountDownLatch latch = new CountDownLatch(1);
      router.get(new Key("space", "retry-key"), new RoutingCallback() {
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
      });
      Assert.assertTrue(latch.await(2500, TimeUnit.MILLISECONDS));
    } finally {
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }
}

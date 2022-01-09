/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.billing;

import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.adamalang.runtime.sys.billing.BillingPubSub;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class BillingPubSubTests {
  @Test
  public void flow() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"a\":\"public int x; @construct { x = 100; } @connected(who) { return true; }\"},\"default\":\"a\"}",
            (t, errorCode) -> {
              t.printStackTrace();
            });
    DeploymentFactoryBase base = new DeploymentFactoryBase();
    base.deploy("space", plan);
    BillingPubSub pubsub = new BillingPubSub(base);
    {
      AtomicInteger pubs = new AtomicInteger(0);
      CountDownLatch latch = new CountDownLatch(10);
      Assert.assertEquals(0, pubsub.size());
      pubsub.subscribe((x) -> {
        latch.countDown();
        return pubs.getAndIncrement() < 5;
      });
      Assert.assertEquals(1, pubsub.size());
      Consumer<HashMap<String, PredictiveInventory.Billing>> publisher = pubsub.publisher();
      HashMap<String, PredictiveInventory.Billing> map = new HashMap<>();
      map.put("space", new PredictiveInventory.Billing(0, 1, 2, 3));
      for (int k = 0; k < 11; k++) {
        publisher.accept(map);
      }
      Assert.assertEquals(0, pubsub.size());
      latch.await(1000, TimeUnit.MILLISECONDS);
      Assert.assertEquals(6, pubs.get());
    }
    {
      AtomicInteger pubs = new AtomicInteger(0);
      Assert.assertEquals(0, pubsub.size());
      pubsub.subscribe((x) -> {
        pubs.getAndIncrement();
        return false;
      });
      Assert.assertEquals(0, pubsub.size());
      Assert.assertEquals(1, pubs.get());
      pubsub.subscribe((x) -> {
        pubs.getAndIncrement();
        return true;
      });
      Assert.assertEquals(1, pubsub.size());
      Assert.assertEquals(2, pubs.get());
    }
  }
}

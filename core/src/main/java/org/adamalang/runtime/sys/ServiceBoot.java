/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.AwaitHelper;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.deploy.Deploy;
import org.adamalang.runtime.sys.capacity.CapacityInstance;
import org.adamalang.runtime.sys.capacity.CapacityOverseer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/** a block to help a service get booted */
public class ServiceBoot {
  private static final Logger LOG = LoggerFactory.getLogger(ServiceBoot.class);

  /** [sync] pull down what capacity has been bound to this machine, and then deploy the plans */
  public static void initializeWithDeployments(String region, String machine, CapacityOverseer overseer, Deploy deploy, int deployBootTimeMilliseconds) {
    CountDownLatch latch = new CountDownLatch(1);
    overseer.listAllOnMachine(region, machine, new Callback<>() {
      @Override
      public void success(List<CapacityInstance> instances) {
        CountDownLatch deployed = new CountDownLatch(instances.size());
        for (CapacityInstance instance : instances) {
          deploy.deploy(instance.space, new Callback<Void>() {
            private final String space = instance.space;
            @Override
            public void success(Void value) {
              deployed.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
              LOG.error("failed-deploy:" + space, ex);
              deployed.countDown();
            }
          });
        }
        AwaitHelper.block(deployed, deployBootTimeMilliseconds);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOG.error("failed-capacity-listing", ex);
        latch.countDown();
      }
    });
    AwaitHelper.block(latch, deployBootTimeMilliseconds + 250);
  }
}

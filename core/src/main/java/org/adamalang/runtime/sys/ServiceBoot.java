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
package org.adamalang.runtime.sys;

import org.adamalang.common.AwaitHelper;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;
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

  /** [async-optimistic] initialize documents on the host */
  public static void startup(FinderService finder, CoreService service) {
    finder.list(new Callback<List<Key>>() {
      @Override
      public void success(List<Key> keys) {
        for (Key key : keys) {
          service.startupLoad(key);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        System.exit(-1);
      }
    });
    finder.listDeleted(new Callback<List<Key>>() {
      @Override
      public void success(List<Key> value) {
        // TODO: re-issue a delete
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
  }
}

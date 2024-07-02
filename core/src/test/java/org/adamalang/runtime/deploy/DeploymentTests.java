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
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.mocks.MockBackupService;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.mocks.MockWakeService;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.*;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.adamalang.runtime.sys.mocks.MockMetricsReporter;
import org.adamalang.translator.env.RuntimeEnvironment;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DeploymentTests {
  @Test
  public void transition() throws Exception {
    DeploymentPlan plan1 =
        new DeploymentPlan(
            "{\"versions\":{\"a\":\"public int x; @construct { x = 100; } @connected { return true; }\"},\"default\":\"a\"}",
            (t, errorCode) -> {
              t.printStackTrace();
            });
    DeploymentPlan plan2 =
        new DeploymentPlan(
            "{\"versions\":{\"a\":\"public int x; @construct { x = 100; } @connected { return true; }\",\"b\":\"public int x; @construct { x = 200; } @connected { return true; }\"},\"plan\":[{\"version\":\"b\",\"seed\":\"x\",\"percent\":50}],\"default\":\"a\"}",
            (t, errorCode) -> {
              t.printStackTrace();
            });
    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT, RuntimeEnvironment.Tooling);
    {
      CountDownLatch latch = new CountDownLatch(1);
      base.deploy("MySpace", plan1, new TreeMap<>(), new Callback<Void>() {
        @Override
        public void success(Void value) {
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.fail();
        }
      });
      Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
    }
    AtomicInteger count1 = new AtomicInteger(0);
    AtomicInteger count2 = new AtomicInteger(0);

    Callback<LivingDocumentFactory> shred =
        new Callback<LivingDocumentFactory>() {
          @Override
          public void success(LivingDocumentFactory factory) {
            try {
              LivingDocument doc = factory.create(null);

              MockInstantDataService dataService = new MockInstantDataService();
              DocumentThreadBase docBase =
                  new DocumentThreadBase(0, new ServiceShield(), new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), new CoreMetrics(new NoOpMetricsFactory()), SimpleExecutor.NOW, new MockTime());
              DurableLivingDocument.fresh(
                  new Key("space", "key"),
                  factory,
                  new CoreRequestContext(NtPrincipal.NO_ONE, "origin", "ip", "key"),
                  "{}",
                  null,
                  null,
                  docBase,
                  new Callback<DurableLivingDocument>() {
                    @Override
                    public void success(DurableLivingDocument value) {
                      if (dataService.getLogAt(0).contains("\"x\":200")) {
                        count2.getAndIncrement();
                      } else if (dataService.getLogAt(0).contains("\"x\":100")) {
                        count1.getAndIncrement();
                      } else {
                        Assert.fail();
                      }
                    }

                    @Override
                    public void failure(ErrorCodeException ex) {
                      Assert.fail();
                    }
                  });
            } catch (Exception ex) {
              Assert.fail();
            }
          }

          @Override
          public void failure(ErrorCodeException ex) {
            Assert.fail();
          }
        };
    for (int k = 0; k < 10; k++) {
      base.fetch(new Key("MySpace", "key" + k), shred);
    }
    Assert.assertEquals(10, count1.get());
    Assert.assertEquals(0, count2.get());
    {
      CountDownLatch latch = new CountDownLatch(1);
      base.deploy("MySpace", plan2, new TreeMap<>(), new Callback<Void>() {
        @Override
        public void success(Void value) {
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.fail();
        }
      });
      Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
    }
    for (int k = 0; k < 100; k++) {
      base.fetch(new Key("MySpace", "keyX" + k), shred);
    }
    Assert.assertEquals(61, count1.get());
    Assert.assertEquals(49, count2.get());
  }
}

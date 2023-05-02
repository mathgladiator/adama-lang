/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.*;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

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
    DeploymentFactoryBase base = new DeploymentFactoryBase();
    base.deploy("MySpace", plan1);
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
                  new DocumentThreadBase(new ServiceShield(), dataService, new CoreMetrics(new NoOpMetricsFactory()), SimpleExecutor.NOW, new MockTime());
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
    base.deploy("MySpace", plan2);
    for (int k = 0; k < 100; k++) {
      base.fetch(new Key("MySpace", "keyX" + k), shred);
    }
    Assert.assertEquals(61, count1.get());
    Assert.assertEquals(49, count2.get());
  }
}

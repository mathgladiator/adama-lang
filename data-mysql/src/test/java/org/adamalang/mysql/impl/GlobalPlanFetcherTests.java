/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.impl;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.runtime.deploy.DeploymentBundle;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GlobalPlanFetcherTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        String masterKey = MasterKey.generateMasterKey();
        GlobalPlanFetcher fetcher = new GlobalPlanFetcher(dataBase, masterKey);
        CountDownLatch latch = new CountDownLatch(2);
        fetcher.find("space", new Callback<DeploymentBundle>() {
          @Override
          public void success(DeploymentBundle value) {

          }

          @Override
          public void failure(ErrorCodeException ex) {
            latch.countDown();
          }
        });
        int spaceId = Spaces.createSpace(dataBase, 1, "spacez");
        Spaces.setPlan(dataBase, spaceId, "{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}", "hash");
        fetcher.find("spacez", new Callback<DeploymentBundle>() {
          @Override
          public void success(DeploymentBundle value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
          }
        });
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
      } finally {
        installer.uninstall();
      }
    }
  }
}

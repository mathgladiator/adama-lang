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

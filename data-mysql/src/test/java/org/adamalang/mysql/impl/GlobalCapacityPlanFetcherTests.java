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
import org.adamalang.common.template.tree.T;
import org.adamalang.mysql.*;
import org.adamalang.mysql.model.Domains;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.runtime.sys.capacity.CapacityPlan;
import org.adamalang.runtime.sys.domains.Domain;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GlobalCapacityPlanFetcherTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        GlobalCapacityPlanFetcher finder = new GlobalCapacityPlanFetcher(dataBase);
        CountDownLatch latch = new CountDownLatch(3);
        finder.fetch("space", new Callback<CapacityPlan>() {
          @Override
          public void success(CapacityPlan value) {
          }

          @Override
          public void failure(ErrorCodeException ex) {
            Assert.assertEquals(609294, ex.code);
            latch.countDown();
          }
        });
        Spaces.createSpace(dataBase, 1, "space");
        Spaces.createSpace(dataBase, 1, "space_with");
        Spaces.setCapacity(dataBase, "space_with", "{\"min\":42}");
        finder.fetch("space", new Callback<CapacityPlan>() {
          @Override
          public void success(CapacityPlan value) {
            Assert.assertEquals(3, value.minimum);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
          }
        });
        finder.fetch("space_with", new Callback<CapacityPlan>() {
          @Override
          public void success(CapacityPlan value) {
            Assert.assertEquals(42, value.minimum);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
          }
        });
        Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
      } finally {
        installer.uninstall();
      }
    }
  }
}

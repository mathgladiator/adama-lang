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
package org.adamalang.mysql.model;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.junit.Assert;
import org.junit.Test;

public class SentinelTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        try {
          Sentinel.get(dataBase, "super1");
        } catch (Exception ex) {
          Assert.assertTrue(ex instanceof ErrorCodeException);
        }
        Assert.assertEquals(0, Sentinel.countBehind(dataBase, 10000));
        Sentinel.ping(dataBase, "super1", 42);
        Assert.assertEquals(42, Sentinel.get(dataBase, "super1"));
        Sentinel.ping(dataBase, "super1", 123);
        Sentinel.ping(dataBase, "super1", 5242);
        Assert.assertEquals(5242, Sentinel.get(dataBase, "super1"));
        Assert.assertEquals(0, Sentinel.countBehind(dataBase, 100));
        Assert.assertEquals(1, Sentinel.countBehind(dataBase, 10000));
      } finally {
        installer.uninstall();
      }
    }
  }
}

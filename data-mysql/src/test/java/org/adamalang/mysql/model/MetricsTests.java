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
package org.adamalang.mysql.model;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MetricsTests {
  @Test
  public void metrics() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        Metrics.putOrUpdateDocumentMetrics(dataBase, "space", "key-1", "A");
        Metrics.putOrUpdateDocumentMetrics(dataBase, "space", "key-2", "B");
        Metrics.putOrUpdateDocumentMetrics(dataBase, "space", "key-3", "C");
        List<String> exact = Metrics.downloadMetrics(dataBase, "space", "key-1");
        Assert.assertEquals(1, exact.size());
        Assert.assertEquals("A", exact.get(0));
        List<String> prefix = Metrics.downloadMetrics(dataBase, "space", "key");
        Assert.assertEquals(3, prefix.size());
        Metrics.putOrUpdateDocumentMetrics(dataBase, "space", "key-1", "ABC");
        exact = Metrics.downloadMetrics(dataBase, "space", "key-1");
        Assert.assertEquals(1, exact.size());
        Assert.assertEquals("ABC", exact.get(0));
      } finally {
        installer.uninstall();
      }
    }
  }
}

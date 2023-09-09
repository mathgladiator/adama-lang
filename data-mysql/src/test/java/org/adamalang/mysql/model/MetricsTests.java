/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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

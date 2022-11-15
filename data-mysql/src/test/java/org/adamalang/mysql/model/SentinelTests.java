/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
        Sentinel.ping(dataBase, "super1", 42);
        Assert.assertEquals(42, Sentinel.get(dataBase, "super1"));
        Sentinel.ping(dataBase, "super1", 123);
        Sentinel.ping(dataBase, "super1", 5242);
        Assert.assertEquals(5242, Sentinel.get(dataBase, "super1"));
      } finally {
        installer.uninstall();
      }
    }
  }
}

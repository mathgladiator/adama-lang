/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.model;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.junit.Assert;
import org.junit.Test;

public class SecretsTests {
  @Test
  public void secrets() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        int key1 = Secrets.insertSecretKey(dataBase, "space", "private-key1");
        int key2 = Secrets.insertSecretKey(dataBase, "space", "private-key2");
        Assert.assertEquals("private-key1", Secrets.getPrivateKey(dataBase, "space", key1));
        Assert.assertEquals("private-key2", Secrets.getPrivateKey(dataBase, "space", key2));
      } finally {
        installer.uninstall();
      }
    }
  }
}

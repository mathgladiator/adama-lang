/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.common.keys.MasterKey;
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
        String masterKey = MasterKey.generateMasterKey();
        Secrets.getOrCreateDocumentSigningKey(dataBase, masterKey,"space", "doc-1");
        Secrets.getOrCreateDocumentSigningKey(dataBase, masterKey,"space", "doc-1");
      } finally {
        installer.uninstall();
      }
    }
  }
}

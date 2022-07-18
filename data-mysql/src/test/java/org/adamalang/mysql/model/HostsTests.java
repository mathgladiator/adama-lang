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

public class HostsTests {
  @Test
  public void hosts() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        Hosts.initializeWebHost(dataBase, "region", "machine1", "pubKey123");
        Hosts.initializeWebHost(dataBase, "region", "machine2", "pubKey42");
        Assert.assertEquals("pubKey123", Hosts.getWebHostPublicKey(dataBase, "region", "machine1"));
        Assert.assertEquals("pubKey42", Hosts.getWebHostPublicKey(dataBase, "region", "machine2"));
        Assert.assertNull(Hosts.getWebHostPublicKey(dataBase, "r", "ma"));
      } finally {
        installer.uninstall();
      }
    }
  }
}

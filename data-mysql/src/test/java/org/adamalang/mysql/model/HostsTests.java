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

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HostsTests {
  @Test
  public void hosts() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        Assert.assertEquals(0, Hosts.listHosts(dataBase, "region", "web").size());
        Hosts.initializeHost(dataBase, "region", "machine1", "web", "pubKey123");
        Assert.assertEquals(1, Hosts.listHosts(dataBase, "region", "web").size());
        Hosts.initializeHost(dataBase, "region", "machine2", "web", "pubKey42");
        Hosts.initializeHost(dataBase, "region", "machine1", "adama", "pubKeyX");
        Hosts.initializeHost(dataBase, "region", "machine2", "adama", "pubKeyY");
        List<String> results = Hosts.listHosts(dataBase, "region", "web");
        Assert.assertEquals("machine1", results.get(0));
        Assert.assertEquals("machine2", results.get(1));
        Assert.assertEquals("pubKey123", Hosts.getHostPublicKey(dataBase, "region", "machine1", "web"));
        Assert.assertEquals("pubKey42", Hosts.getHostPublicKey(dataBase, "region", "machine2", "web"));
        Assert.assertEquals("pubKeyX", Hosts.getHostPublicKey(dataBase, "region", "machine1", "adama"));
        Assert.assertEquals("pubKeyY", Hosts.getHostPublicKey(dataBase, "region", "machine2", "adama"));
        Assert.assertNull(Hosts.getHostPublicKey(dataBase, "r", "ma", "x"));
      } finally {
        installer.uninstall();
      }
    }
  }
}

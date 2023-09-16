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
        int web1 = Hosts.initializeHost(dataBase, "region", "machine1", "web", "pubKey123");
        Assert.assertEquals(1, Hosts.listHosts(dataBase, "region", "web").size());
        int web2 = Hosts.initializeHost(dataBase, "region", "machine2", "web", "pubKey42");
        int web3 = Hosts.initializeHost(dataBase, "region", "machine1", "adama", "pubKeyX");
        int web4 = Hosts.initializeHost(dataBase, "region", "machine2", "adama", "pubKeyY");
        Assert.assertEquals("machine1", Hosts.pickStableHostFromRegion(dataBase, "region", "adama", "xyz"));
        Assert.assertEquals("machine2", Hosts.pickStableHostFromRegion(dataBase, "region", "adama", "35d35"));
        List<String> results = Hosts.listHosts(dataBase, "region", "web");
        Assert.assertEquals("machine1", results.get(0));
        Assert.assertEquals("machine2", results.get(1));
        Assert.assertEquals("pubKey123", Hosts.getHostPublicKey(dataBase, web1));
        Assert.assertEquals("pubKey42", Hosts.getHostPublicKey(dataBase, web2));
        Assert.assertEquals("pubKeyX", Hosts.getHostPublicKey(dataBase, web3));
        Assert.assertEquals("pubKeyY", Hosts.getHostPublicKey(dataBase, web4));
        Assert.assertNull(Hosts.getHostPublicKey(dataBase, 1000));
      } finally {
        installer.uninstall();
      }
    }
  }
}

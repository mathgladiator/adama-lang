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

import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class SecretsTests {
  @Test
  public void secrets() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        String masterKey = MasterKey.generateMasterKey();
        int key1 = Secrets.insertSecretKey(dataBase, "space", MasterKey.encrypt(masterKey, "private-key1"));
        int key2 = Secrets.insertSecretKey(dataBase, "space", MasterKey.encrypt(masterKey, "private-key2"));
        TreeMap<Integer, PrivateKeyBundle> bundle = Secrets.getKeys(dataBase, masterKey, "space");
        Assert.assertEquals("private-key1", bundle.get(key1).getPrivateKey());
        Assert.assertEquals("private-key2", bundle.get(key2).getPrivateKey());
      } finally {
        installer.uninstall();
      }
    }
  }
}

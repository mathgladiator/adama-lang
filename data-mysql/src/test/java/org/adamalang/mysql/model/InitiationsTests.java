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
import org.adamalang.mysql.data.IdHashPairing;
import org.junit.Assert;
import org.junit.Test;

public class InitiationsTests {
  @Test
  public void initiations() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        Assert.assertEquals(1, Users.createUserId(dataBase, "x@x.com"));
        Users.addInitiationPair(dataBase, 1, "hash", System.currentTimeMillis() - 60000);
        Assert.assertEquals("hash", Users.listInitiationPairs(dataBase, 1).get(0).hash);
        Assert.assertEquals(1, Users.expireKeys(dataBase, System.currentTimeMillis()));
        Assert.assertEquals(0, Users.listInitiationPairs(dataBase, 1).size());
        Users.addInitiationPair(dataBase, 1, "hash", System.currentTimeMillis() - 60000);
        Assert.assertEquals(1, Users.listInitiationPairs(dataBase, 1).size());
        for (IdHashPairing ihp : Users.listInitiationPairs(dataBase, 1)) {
          Users.deleteInitiationPairing(dataBase, ihp.id);
        }
        Assert.assertEquals(0, Users.listInitiationPairs(dataBase, 1).size());
      } finally {
        installer.uninstall();
      }
    }
  }

}

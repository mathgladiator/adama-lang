/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
        Assert.assertEquals(1, Users.getOrCreateUserId(dataBase, "x@x.com"));
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

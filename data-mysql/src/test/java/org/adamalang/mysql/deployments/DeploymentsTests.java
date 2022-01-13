/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.deployments;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseConfigTests;
import org.adamalang.mysql.backend.BackendDataServiceInstaller;
import org.adamalang.mysql.deployments.Deployments;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class DeploymentsTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig)) {
      DeployedInstaller installer = new DeployedInstaller(dataBase);
      try {
        installer.install();
        ArrayList<Deployments.Deployment> listing = Deployments.list(dataBase, "127.0.0.1:230");
        Assert.assertEquals(0, listing.size());
        Deployments.deploy(dataBase, "space1", "127.0.0.1:230", "hash1", "plan1");
        listing = Deployments.list(dataBase, "127.0.0.1:230");
        Assert.assertEquals(1, listing.size());
        Assert.assertEquals("space1", listing.get(0).space);
        Assert.assertEquals("hash1", listing.get(0).hash);
        Assert.assertEquals("plan1", listing.get(0).plan);
        Deployments.deploy(dataBase, "space1", "127.0.0.1:230", "hash1x", "plan1x");
        listing = Deployments.list(dataBase, "127.0.0.1:230");
        Assert.assertEquals(1, listing.size());
        Assert.assertEquals("space1", listing.get(0).space);
        Assert.assertEquals("hash1x", listing.get(0).hash);
        Assert.assertEquals("plan1x", listing.get(0).plan);
        Deployments.deploy(dataBase, "space2", "127.0.0.1:230", "hash2x", "plan2x");
        listing = Deployments.list(dataBase, "127.0.0.1:230");
        Assert.assertEquals(2, listing.size());
        Assert.assertEquals("space1", listing.get(0).space);
        Assert.assertEquals("hash1x", listing.get(0).hash);
        Assert.assertEquals("plan1x", listing.get(0).plan);
        Assert.assertEquals("space2", listing.get(1).space);
        Assert.assertEquals("hash2x", listing.get(1).hash);
        Assert.assertEquals("plan2x", listing.get(1).plan);
        Deployments.undeploy(dataBase, "space1", "127.0.0.1:230");
        listing = Deployments.list(dataBase, "127.0.0.1:230");
        Assert.assertEquals(1, listing.size());
        Assert.assertEquals("space2", listing.get(0).space);
        Assert.assertEquals("hash2x", listing.get(0).hash);
        Assert.assertEquals("plan2x", listing.get(0).plan);
        Assert.assertEquals("plan2x", Deployments.get(dataBase, "127.0.0.1:230", "space2").plan);
        try {
          Deployments.get(dataBase, "127.0.0.1:230", "space1000");
          Assert.fail();
        } catch (ErrorCodeException ec) {
          Assert.assertEquals(643084, ec.code);
        }
        Assert.assertEquals(0, Deployments.list(dataBase, "127.0.0.1:123").size());
        Deployments.deploy(dataBase, "spaceX", "127.0.0.1:123", "hash1x", "plan1x");
        Deployments.deploy(dataBase, "spaceX", "127.0.0.1:124", "hash1x", "plan1x");
        Assert.assertEquals(1, Deployments.list(dataBase, "127.0.0.1:123").size());
        Assert.assertEquals(1, Deployments.list(dataBase, "127.0.0.1:124").size());
        Deployments.undeployAll(dataBase, "spaceX");
        Assert.assertEquals(0, Deployments.list(dataBase, "127.0.0.1:123").size());
        Assert.assertEquals(0, Deployments.list(dataBase, "127.0.0.1:124").size());
      } finally {
        installer.uninstall();
      }
    }
  }
}

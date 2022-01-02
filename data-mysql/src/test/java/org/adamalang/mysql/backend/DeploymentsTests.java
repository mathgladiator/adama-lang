package org.adamalang.mysql.backend;

import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseConfigTests;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class DeploymentsTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig)) {
      BackendDataServiceInstaller installer = new BackendDataServiceInstaller(dataBase);
      try {
        installer.install();
        ArrayList<Deployments.Deployment> listing = Deployments.list(dataBase, "127.0.0.1:230");
        Assert.assertEquals(0, listing.size());
        Deployments.deploy(dataBase, "space1", "127.0.0.1:230", "hash1", "plan1");
        listing = Deployments.list(dataBase, "127.0.0.1:230");;
        Assert.assertEquals(1, listing.size());
        Assert.assertEquals("space1", listing.get(0).space);
        Assert.assertEquals("hash1", listing.get(0).hash);
        Assert.assertEquals("plan1", listing.get(0).plan);
        Deployments.deploy(dataBase, "space1", "127.0.0.1:230", "hash1x", "plan1x");
        listing = Deployments.list(dataBase, "127.0.0.1:230");;
        Assert.assertEquals(1, listing.size());
        Assert.assertEquals("space1", listing.get(0).space);
        Assert.assertEquals("hash1x", listing.get(0).hash);
        Assert.assertEquals("plan1x", listing.get(0).plan);
        Deployments.deploy(dataBase, "space2", "127.0.0.1:230", "hash2x", "plan2x");
        listing = Deployments.list(dataBase, "127.0.0.1:230");;
        Assert.assertEquals(2, listing.size());
        Assert.assertEquals("space1", listing.get(0).space);
        Assert.assertEquals("hash1x", listing.get(0).hash);
        Assert.assertEquals("plan1x", listing.get(0).plan);
        Assert.assertEquals("space2", listing.get(1).space);
        Assert.assertEquals("hash2x", listing.get(1).hash);
        Assert.assertEquals("plan2x", listing.get(1).plan);
        Deployments.undeploy(dataBase, "space1", "127.0.0.1:230");
        listing = Deployments.list(dataBase, "127.0.0.1:230");;
        Assert.assertEquals(1, listing.size());
        Assert.assertEquals("space2", listing.get(0).space);
        Assert.assertEquals("hash2x", listing.get(0).hash);
        Assert.assertEquals("plan2x", listing.get(0).plan);
      } finally {
        installer.uninstall();
      }
    }
  }
}

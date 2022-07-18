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
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.data.Deployment;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeSet;

public class DeploymentsTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        ArrayList<Deployment> listing = Deployments.listSpacesOnTarget(dataBase, "127.0.0.1:230");
        Assert.assertEquals(0, listing.size());
        Deployments.deploy(dataBase, "space1", "127.0.0.1:230", "hash1", "plan1");
        listing = Deployments.listSpacesOnTarget(dataBase, "127.0.0.1:230");
        Assert.assertEquals(1, listing.size());
        Assert.assertEquals("space1", listing.get(0).space);
        Assert.assertEquals("hash1", listing.get(0).hash);
        Assert.assertEquals("plan1", listing.get(0).plan);
        Deployments.deploy(dataBase, "space1", "127.0.0.1:230", "hash1x", "plan1x");
        listing = Deployments.listSpacesOnTarget(dataBase, "127.0.0.1:230");
        Assert.assertEquals(1, listing.size());
        Assert.assertEquals("space1", listing.get(0).space);
        Assert.assertEquals("hash1x", listing.get(0).hash);
        Assert.assertEquals("plan1x", listing.get(0).plan);
        Deployments.deploy(dataBase, "space2", "127.0.0.1:230", "hash2x", "plan2x");
        listing = Deployments.listSpacesOnTarget(dataBase, "127.0.0.1:230");
        Assert.assertEquals(2, listing.size());
        Assert.assertEquals("space1", listing.get(0).space);
        Assert.assertEquals("hash1x", listing.get(0).hash);
        Assert.assertEquals("plan1x", listing.get(0).plan);
        Assert.assertEquals("space2", listing.get(1).space);
        Assert.assertEquals("hash2x", listing.get(1).hash);
        Assert.assertEquals("plan2x", listing.get(1).plan);
        Deployments.undeploy(dataBase, "space1", "127.0.0.1:230");
        listing = Deployments.listSpacesOnTarget(dataBase, "127.0.0.1:230");
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
        Assert.assertEquals(0, Deployments.listSpacesOnTarget(dataBase, "127.0.0.1:123").size());
        Deployments.deploy(dataBase, "spaceX", "127.0.0.1:123", "hash1x", "plan1x");
        Deployments.deploy(dataBase, "spaceX", "127.0.0.1:124", "hash1x", "plan1x");
        Assert.assertEquals(1, Deployments.listSpacesOnTarget(dataBase, "127.0.0.1:123").size());
        Assert.assertEquals(1, Deployments.listSpacesOnTarget(dataBase, "127.0.0.1:124").size());
        Deployments.undeployAll(dataBase, "spaceX");
        Assert.assertEquals(0, Deployments.listSpacesOnTarget(dataBase, "127.0.0.1:123").size());
        Assert.assertEquals(0, Deployments.listSpacesOnTarget(dataBase, "127.0.0.1:124").size());
        Deployments.deploy(dataBase, "space1", "127.0.0.1:230", "hash1x", "plan1x");
        Deployments.deploy(dataBase, "space2", "127.0.0.1:230", "hash1x", "plan1x");
        Deployments.deploy(dataBase, "space2", "127.0.0.1:231", "hash1x", "plan1x");
        Deployments.deploy(dataBase, "space3", "127.0.0.1:232", "hash1x", "plan1x");


        ArrayList<Deployment> deploymentsOnSpace = Deployments.listTargetsOnSpace(dataBase,"space2");
        Assert.assertEquals(2, deploymentsOnSpace.size());
        Assert.assertEquals("127.0.0.1:230", deploymentsOnSpace.get(0).target);
        Assert.assertEquals("127.0.0.1:231", deploymentsOnSpace.get(1).target);

        TreeSet<String> allTargets = Deployments.listAllTargets(dataBase);
        Assert.assertEquals(3, allTargets.size());
        Assert.assertEquals("127.0.0.1:230", allTargets.first());
        Deployments.undeployTarget(dataBase, "127.0.0.1:230");
        allTargets = Deployments.listAllTargets(dataBase);
        Assert.assertEquals(2, allTargets.size());
        Assert.assertEquals("127.0.0.1:231", allTargets.first());
        Deployments.undeployTarget(dataBase, "127.0.0.1:231");
        allTargets = Deployments.listAllTargets(dataBase);
        Assert.assertEquals(1, allTargets.size());
        Assert.assertEquals("127.0.0.1:232", allTargets.first());
        Deployments.undeployTarget(dataBase, "127.0.0.1:232");
        Assert.assertEquals(0, Deployments.listAllTargets(dataBase).size());
      } finally {
        installer.uninstall();
      }
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.data.CapacityInstance;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CapacityTests {
  @Test
  public void flow() throws Exception {
    List<CapacityInstance> capacities;
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        int x = Capacity.add(dataBase, "space", "region1", "machine1");
        Capacity.add(dataBase, "space", "region1", "machine2");
        Capacity.add(dataBase, "space", "region2", "machine3");
        Capacity.add(dataBase, "spacex", "region2", "machine4");

        Capacity.add(dataBase, "space", "region1", "machine1");
        Capacity.add(dataBase, "space", "region1", "machine2");
        Capacity.add(dataBase, "space", "region2", "machine3");
        Capacity.add(dataBase, "spacex", "region2", "machine4");


        capacities = Capacity.listAll(dataBase, "space");
        Assert.assertEquals(3, capacities.size());
        Assert.assertEquals("region1", capacities.get(0).region);
        Assert.assertEquals("region1", capacities.get(1).region);
        Assert.assertEquals("region2", capacities.get(2).region);
        Assert.assertEquals("machine1", capacities.get(0).machine);
        Assert.assertEquals("machine2", capacities.get(1).machine);
        Assert.assertEquals("machine3", capacities.get(2).machine);

        Assert.assertEquals(x, capacities.get(0).id);
        Assert.assertFalse(capacities.get(0).override);
        Capacity.setOverride(dataBase, x, true);

        capacities = Capacity.listRegion(dataBase, "space", "region1");
        Assert.assertEquals(2, capacities.size());
        Assert.assertEquals("region1", capacities.get(0).region);
        Assert.assertEquals("region1", capacities.get(1).region);
        Assert.assertEquals("machine1", capacities.get(0).machine);
        Assert.assertEquals("machine2", capacities.get(1).machine);

        capacities = Capacity.listAllOnMachine(dataBase, "region1", "machine1");
        Assert.assertEquals(1, capacities.size());
        Assert.assertEquals("space", capacities.get(0).space);
        Assert.assertEquals("region1", capacities.get(0).region);
        Assert.assertEquals("machine1", capacities.get(0).machine);

        Assert.assertEquals(x, capacities.get(0).id);
        Assert.assertTrue(capacities.get(0).override);

        Capacity.remove(dataBase, "space", "region1", "machine2");

        capacities = Capacity.listRegion(dataBase, "space", "region1");
        Assert.assertEquals(1, capacities.size());
        Assert.assertEquals("region1", capacities.get(0).region);
        Assert.assertEquals("machine1", capacities.get(0).machine);

        Capacity.removeAll(dataBase, "space");

        capacities = Capacity.listRegion(dataBase, "space", "region1");
        Assert.assertEquals(0, capacities.size());


      } finally {
        installer.uninstall();
      }
    }
  }
}

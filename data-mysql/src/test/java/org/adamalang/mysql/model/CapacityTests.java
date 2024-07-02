/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
import org.adamalang.runtime.sys.capacity.CapacityInstance;
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

        Assert.assertTrue(capacities.get(0).override);

        Capacity.remove(dataBase, "space", "region1", "machine2");

        capacities = Capacity.listRegion(dataBase, "space", "region1");
        Assert.assertEquals(1, capacities.size());
        Assert.assertEquals("region1", capacities.get(0).region);
        Assert.assertEquals("machine1", capacities.get(0).machine);

        Capacity.removeAll(dataBase, "space");

        capacities = Capacity.listRegion(dataBase, "space", "region1");
        Assert.assertEquals(0, capacities.size());

        Capacity.add(dataBase, "some-space", "new-region", "machine-x");
        Hosts.initializeHost(dataBase, "new-region", "machine-x", "adama", "public-key");
        Assert.assertEquals(1, Capacity.listRegion(dataBase, "some-space", "new-region").size());
        Assert.assertEquals(1, Hosts.listHosts(dataBase, "new-region", "adama").size());
        Hosts.decomissionHost(dataBase, "new-region", "machine-x");
        Assert.assertEquals(0, Capacity.listRegion(dataBase, "some-space", "new-region").size());
        Assert.assertEquals(0, Hosts.listHosts(dataBase, "new-region", "adama").size());
      } finally {
        installer.uninstall();
      }
    }
  }
}

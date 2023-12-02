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

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.data.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SpacesTests {
  @Test
  public void spaces() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        int alice = Users.createUserId(dataBase, "alice@x.com");
        int bob = Users.createUserId(dataBase, "bob@x.com");
        Assert.assertEquals(1, Spaces.createSpace(dataBase, alice, "space1"));
        Assert.assertEquals(1, Spaces.createSpace(dataBase, alice, "space1"));
        Spaces.setSpaceStorage(dataBase, "space1", 456);
        try {
          Spaces.getRxHtml(dataBase, 1);
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(656403, ex.code);
        }
        Spaces.setRxHtml(dataBase, 1, "<forest>");
        Assert.assertEquals("<forest>", Spaces.getRxHtml(dataBase, 1));
        Assert.assertEquals(1, Spaces.getSpaceInfo(dataBase, "space1").id);
        Assert.assertEquals("{}", Spaces.getSpaceInfo(dataBase, "space1").policy);
        Spaces.setPolicy(dataBase, 1, "{\"POLICY\":true}");
        Assert.assertEquals("{\"POLICY\":true}", Spaces.getSpaceInfo(dataBase, "space1").policy);
        Assert.assertEquals(2, Spaces.createSpace(dataBase, bob, "space2"));
        Assert.assertEquals(2, Spaces.createSpace(dataBase, bob, "space2"));
        Assert.assertEquals("{}", Spaces.getSpaceInfo(dataBase, "space2").policy);
        ArrayList<String> names = Spaces.listAllSpaceNames(dataBase);
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("space1", names.get(0));
        Assert.assertEquals("space2", names.get(1));

        ArrayList<SimpleSpaceInfo> spaces = Spaces.listAllSpaces(dataBase);
        Assert.assertEquals(2, spaces.size());
        Assert.assertEquals("space1", spaces.get(0).name);
        Assert.assertEquals("space2", spaces.get(1).name);
        Assert.assertEquals(1, spaces.get(0).id);
        Assert.assertEquals(2, spaces.get(1).id);
        Assert.assertEquals(1, spaces.get(0).owner);
        Assert.assertEquals(2, spaces.get(1).owner);

        try {
          Spaces.createSpace(dataBase, bob, "space1");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(679948, ex.code);
        }
        try {
          Spaces.createSpace(dataBase, alice, "space2");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(679948, ex.code);
        }
        Assert.assertEquals(2, Spaces.getSpaceInfo(dataBase, "space2").id);
        Assert.assertEquals("{}", Spaces.getPlan(dataBase, 1));
        Assert.assertEquals("{}", Spaces.getPlan(dataBase, 2));
        Spaces.setPlan(dataBase, 1, "{\"x\":1}", "h1");
        Spaces.setPlan(dataBase, 2, "{\"x\":2}", "h2");

        InternalDeploymentPlan iPlan = Spaces.getPlanByNameForInternalDeployment(dataBase, "space2");
        Assert.assertEquals("h2", iPlan.hash);
        Assert.assertEquals("{\"x\":2}", iPlan.plan);

        Assert.assertEquals("{\"x\":1}", Spaces.getPlan(dataBase, 1));
        Assert.assertEquals("{\"x\":2}", Spaces.getPlan(dataBase, 2));
        {
          List<SpaceListingItem> ls1 = Spaces.list(dataBase, alice, null, 5);
          List<SpaceListingItem> ls2 = Spaces.list(dataBase, bob, null, 5);
          Assert.assertEquals(1, ls1.size());
          Assert.assertEquals(1, ls2.size());
          Assert.assertEquals("space1", ls1.get(0).name);
          Assert.assertEquals("space2", ls2.get(0).name);
          Assert.assertEquals(456, ls1.get(0).storageBytes);
          Assert.assertEquals("owner", ls1.get(0).callerRole);
          Assert.assertEquals("owner", ls2.get(0).callerRole);
        }
        Spaces.setRole(dataBase, 2, alice, Role.Developer);
        List<Developer> devs = Spaces.listDevelopers(dataBase, 2);
        Assert.assertEquals(1, devs.size());
        {
          SpaceInfo spaceInfo1 = Spaces.getSpaceInfo(dataBase, "space1");
          SpaceInfo spaceInfo2 = Spaces.getSpaceInfo(dataBase, "space2");
          Assert.assertTrue(spaceInfo1.developers.contains(1));
          Assert.assertTrue(spaceInfo2.developers.contains(1));
          Assert.assertTrue(spaceInfo2.developers.contains(2));
          List<SpaceListingItem> ls1 = Spaces.list(dataBase, alice, null, 5);
          List<SpaceListingItem> ls2 = Spaces.list(dataBase, bob, null, 5);
          Assert.assertEquals(2, ls1.size());
          Assert.assertEquals(1, ls2.size());
          Assert.assertEquals("space1", ls1.get(0).name);
          Assert.assertEquals("space2", ls1.get(1).name);
          Assert.assertEquals("space2", ls2.get(0).name);
          Assert.assertEquals("owner", ls1.get(0).callerRole);
          Assert.assertEquals("developer", ls1.get(1).callerRole);
          Assert.assertEquals("owner", ls2.get(0).callerRole);
        }
        Spaces.changePrimaryOwner(dataBase, 1, alice, bob);
        {
          List<SpaceListingItem> ls1 = Spaces.list(dataBase, alice, null, 5);
          List<SpaceListingItem> ls2 = Spaces.list(dataBase, bob, null, 5);
          Assert.assertEquals(1, ls1.size());
          Assert.assertEquals(2, ls2.size());
          Assert.assertEquals("space2", ls1.get(0).name);
          Assert.assertEquals("space1", ls2.get(0).name);
          Assert.assertEquals("space2", ls2.get(1).name);
          Assert.assertEquals("developer", ls1.get(0).callerRole);
          Assert.assertEquals("owner", ls2.get(0).callerRole);
          Assert.assertEquals("owner", ls2.get(1).callerRole);
        }
        Spaces.setRole(dataBase, 2, alice, Role.None);
        {
          List<SpaceListingItem> ls1 = Spaces.list(dataBase, alice, null, 5);
          List<SpaceListingItem> ls2 = Spaces.list(dataBase, bob, null, 5);
          Assert.assertEquals(0, ls1.size());
          Assert.assertEquals(2, ls2.size());
          Assert.assertEquals("space1", ls2.get(0).name);
          Assert.assertEquals("space2", ls2.get(1).name);
        }

        try {
          Spaces.createSpace(dataBase, alice, "space1");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(679948, ex.code);
        }
        try {
          Spaces.getSpaceInfo(dataBase, "space3");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(625678, ex.code);
        }
        try {
          Spaces.getPlan(dataBase, 5);
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(609294, ex.code);
        }
        try {
          Spaces.getPlanByNameForInternalDeployment(dataBase, "nospacename");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(654341, ex.code);
        }
        {
          Assert.assertEquals(2, Spaces.list(dataBase, bob, null, 5).size());
          int space1 = Spaces.getSpaceInfo(dataBase, "space1").id;
          int space2 = Spaces.getSpaceInfo(dataBase, "space2").id;
          Spaces.delete(dataBase, space1, alice);
          Assert.assertEquals(2, Spaces.list(dataBase, bob, null, 5).size());
          ArrayList<DeletedSpace> ds = Spaces.listDeletedSpaces(dataBase);
          Assert.assertEquals(0, ds.size());
          Spaces.changePrimaryOwner(dataBase, space1, bob, 0);
          ds = Spaces.listDeletedSpaces(dataBase);
          Assert.assertEquals(1, ds.size());
          Assert.assertEquals(space1, ds.get(0).id);
          Spaces.delete(dataBase, space1, 0);
          ds = Spaces.listDeletedSpaces(dataBase);
          Assert.assertEquals(0, ds.size());
          Assert.assertEquals(1, Spaces.list(dataBase, bob, null, 5).size());
          Assert.assertEquals(3, Spaces.createSpace(dataBase, alice, "space1"));
          Spaces.delete(dataBase, space2, bob);
          Assert.assertEquals(0, Spaces.list(dataBase, bob, null, 5).size());
        }
      } finally {
        installer.uninstall();
      }
    }
  }

}

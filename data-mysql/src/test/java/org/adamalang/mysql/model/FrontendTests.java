/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.model;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.data.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FrontendTests {

  @Test
  public void health() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Assert.assertTrue(Health.pingDataBase(dataBase));
    }
  }

  @Test
  public void users() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        Assert.assertEquals(0, Users.countUsers(dataBase));
        Assert.assertEquals(1, Users.getOrCreateUserId(dataBase, "x@x.com"));
        Assert.assertEquals(1, Users.getOrCreateUserId(dataBase, "x@x.com"));
        Assert.assertEquals(1, Users.countUsers(dataBase));
        Users.validateUser(dataBase, 1);
        Users.validateUser(dataBase, 1);
        Users.addKey(dataBase, 1, "key", System.currentTimeMillis() + 1000 * 60);
        Assert.assertEquals("key", Users.listKeys(dataBase, 1).get(0));
        Users.removeAllKeys(dataBase, 1);
        Assert.assertEquals(0, Users.listKeys(dataBase, 1).size());
        Users.addKey(dataBase, 1, "key2", System.currentTimeMillis() + 1000 * 60);
        Assert.assertEquals(1, Users.listKeys(dataBase, 1).size());
        Assert.assertEquals(1, Users.expireKeys(dataBase, System.currentTimeMillis() + 10000000L));
        Assert.assertEquals(0, Users.listKeys(dataBase, 1).size());
        Users.setPasswordHash(dataBase, 1, "hash");
        Assert.assertEquals("hash", Users.getPasswordHash(dataBase, 1));
        Assert.assertEquals(500, Users.getBalance(dataBase, 1));
        Users.addToBalance(dataBase, 1, 250);
        Assert.assertEquals(750, Users.getBalance(dataBase, 1));
        try {
          Users.getPasswordHash(dataBase, 55000);
          Assert.fail();
        } catch (ErrorCodeException ece) {
          Assert.assertEquals(684039, ece.code);
        }
        try {
          Users.getBalance(dataBase, 55000);
          Assert.fail();
        } catch (ErrorCodeException ece) {
          Assert.assertEquals(605208, ece.code);
        }
        Assert.assertEquals(2, Users.getOrCreateUserId(dataBase, "xz@x.com"));
        Assert.assertEquals(2, Users.countUsers(dataBase));
      } finally {
        installer.uninstall();
      }
    }
  }

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

  @Test
  public void authorities() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        int failures = 0;
        Assert.assertEquals(0, Authorities.list(dataBase, 1).size());
        Authorities.createAuthority(dataBase, 1, "auth_space_1");
        try {
          Authorities.createAuthority(dataBase, 1, "auth_space_1");
          Assert.fail();
        } catch (ErrorCodeException ece) {
          failures++;
          Assert.assertEquals(601088, ece.code);
        }
        {
          ArrayList<String> listing = Authorities.list(dataBase, 1);
          Assert.assertEquals(1, listing.size());
          Assert.assertEquals("auth_space_1", listing.get(0));
        }
        Authorities.setKeystore(dataBase, 1, "auth_space_1", "{\"x\":1}");
        Assert.assertEquals("{\"x\":1}", Authorities.getKeystoreInternal(dataBase, "auth_space_1"));
        Assert.assertEquals("{\"x\":1}", Authorities.getKeystorePublic(dataBase, 1, "auth_space_1"));
        try {
          Authorities.getKeystorePublic(dataBase, 2, "auth_space_1");
          Assert.fail();
        } catch (ErrorCodeException ece) {
          failures++;
          Assert.assertEquals(626691, ece.code);
        }
        Authorities.deleteAuthority(dataBase, 1, "auth_space_1");
        Assert.assertEquals(0, Authorities.list(dataBase, 1).size());
        try {
          Authorities.getKeystoreInternal(dataBase, "auth_space_1");
          Assert.fail();
        } catch (ErrorCodeException ece) {
          failures++;
          Assert.assertEquals(643072, ece.code);
        }
        try {
          Authorities.getKeystorePublic(dataBase, 1, "auth_space_1");
          Assert.fail();
        } catch (ErrorCodeException ece) {
          failures++;
          Assert.assertEquals(626691, ece.code);
        }
        try {
          Authorities.setKeystore(dataBase, 1, "auth_space_1", "{\"x\":1}");
          Assert.fail();
        } catch (ErrorCodeException ece) {
          failures++;
          Assert.assertEquals(634880, ece.code);
        }
        try {
          Authorities.deleteAuthority(dataBase, 1, "auth_space_1");
          Assert.fail();
        } catch (ErrorCodeException ece) {
          failures++;
          Assert.assertEquals(654339, ece.code);
        }
        Authorities.createAuthority(dataBase, 1, "auth_space_1");
        Authorities.setKeystore(dataBase, 1, "auth_space_1", "{\"x\":2}");
        Assert.assertEquals("{\"x\":2}", Authorities.getKeystoreInternal(dataBase, "auth_space_1"));
        Authorities.setKeystore(dataBase, 1, "auth_space_1", "{\"x\":3}");
        Assert.assertEquals("{\"x\":3}", Authorities.getKeystoreInternal(dataBase, "auth_space_1"));
        {
          ArrayList<String> listing = Authorities.list(dataBase, 1);
          Assert.assertEquals(1, listing.size());
          Assert.assertEquals("auth_space_1", listing.get(0));
        }
        Authorities.changeOwner(dataBase, "auth_space_1", 1, 2);
        Assert.assertEquals(0, Authorities.list(dataBase, 1).size());
        {
          ArrayList<String> listing = Authorities.list(dataBase, 2);
          Assert.assertEquals(1, listing.size());
          Assert.assertEquals("auth_space_1", listing.get(0));
        }
        try {
          Authorities.changeOwner(dataBase, "auth_space_1", 4, 3);
          Assert.fail();
        } catch (ErrorCodeException ece) {
          failures++;
          Assert.assertEquals(662528, ece.code);
        }
        Assert.assertEquals(7, failures);

      } finally {
        installer.uninstall();
      }
    }
  }

  @Test
  public void spaces() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        Assert.assertNull(Spaces.getLatestBillingHourCode(dataBase));
        int alice = Users.getOrCreateUserId(dataBase, "alice@x.com");
        int bob = Users.getOrCreateUserId(dataBase, "bob@x.com");
        Assert.assertEquals(1, Spaces.createSpace(dataBase, alice, "space1"));
        Assert.assertEquals(1, Spaces.createSpace(dataBase, alice, "space1"));
        Assert.assertEquals(1, Spaces.getSpaceInfo(dataBase, "space1").id);
        Assert.assertEquals(2, Spaces.createSpace(dataBase, bob, "space2"));
        Assert.assertEquals(2, Spaces.createSpace(dataBase, bob, "space2"));
        Assert.assertEquals(0, (int) Spaces.getLatestBillingHourCode(dataBase));
        ArrayList<String> names = Spaces.listAllSpaceNames(dataBase);
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("space1", names.get(0));
        Assert.assertEquals("space2", names.get(1));
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
          Assert.assertEquals("owner", ls1.get(0).callerRole);
          Assert.assertEquals("owner", ls2.get(0).callerRole);
        }
        Spaces.setRole(dataBase, 2, alice, Role.Developer);
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
          int space1 = Spaces.getSpaceInfo(dataBase,"space1").id;
          Spaces.delete(dataBase, space1, alice);
          Assert.assertEquals(2, Spaces.list(dataBase, bob, null, 5).size());
          Spaces.delete(dataBase, space1, bob);
          Assert.assertEquals(1, Spaces.list(dataBase, bob, null, 5).size());
          Assert.assertEquals(6, Spaces.createSpace(dataBase, alice, "space1"));
        }
      } finally {
        installer.uninstall();
      }
    }
  }

  @Test
  public void billing() {
    Assert.assertEquals(0, Billing.usageValueOfZeroIfNotPresentOrNull(Json.parseJsonObject("{}"), "x"));
    Assert.assertEquals(123, Billing.usageValueOfZeroIfNotPresentOrNull(Json.parseJsonObject("{\"x\":123}"), "x"));
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.data.SystemUsageInventoryRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class AuthoritiesTests {
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
        HashMap<Integer, SystemUsageInventoryRecord> records = Inventory.inventorySystemUsage(dataBase);
        Assert.assertEquals(1, records.get(1).authorities);
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
}

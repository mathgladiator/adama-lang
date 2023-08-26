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
import org.junit.Assert;
import org.junit.Test;

public class UserTests {

  @Test
  public void users() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        Assert.assertEquals(0, Users.countUsers(dataBase));
        Assert.assertEquals(1, Users.createUserId(dataBase, "x@x.com"));
        Assert.assertEquals(1, Users.getUserId(dataBase, "x@x.com"));
        Assert.assertEquals("{}", Users.getPaymentInfo(dataBase, 1));
        Assert.assertTrue(Users.setPaymentInfo(dataBase, 1, ""));
        Assert.assertEquals("{}", Users.getPaymentInfo(dataBase, 1));
        Assert.assertTrue(Users.setPaymentInfo(dataBase, 1, "XYZ"));
        Assert.assertEquals("XYZ", Users.getPaymentInfo(dataBase, 1));
        Assert.assertEquals(1, Users.countUsers(dataBase));
        Assert.assertEquals("{}", Users.getProfile(dataBase, 1));
        try {
          Users.getProfile(dataBase, 50);
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(674832, ex.code);
        }
        Users.setProfileIf(dataBase, 1, "{\"name\":\"ninja\"}", "{}");
        Assert.assertEquals("{\"name\":\"ninja\"}", Users.getProfile(dataBase, 1));
        Users.setProfileIf(dataBase, 1, "{\"name\":\"w00t\"}", "{\"name\":\"ninja\"}");
        Assert.assertEquals("{\"name\":\"w00t\"}", Users.getProfile(dataBase, 1));
        try {
          Users.setProfileIf(dataBase, 1, "{\"name\":\"w00t\"}", "{\"name\":\"ninja\"}");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(634899, ex.code);
        }
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
        Assert.assertEquals(2, Users.createUserId(dataBase, "xz@x.com"));
        Assert.assertEquals(2, Users.countUsers(dataBase));
      } finally {
        installer.uninstall();
      }
    }
  }

}

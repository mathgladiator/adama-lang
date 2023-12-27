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
package org.adamalang.services;

import org.adamalang.common.ConfigObject;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.common.keys.PublicPrivateKeyPartnership;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.config.ProductionServiceConfig;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.Installer;
import org.adamalang.mysql.model.Secrets;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.TreeMap;

public class ProductionServiceConfigTests {
  public static DataBaseConfig getLocalIntegrationConfig() throws Exception {
    File file = new File("test.mysql.json");
    Assume.assumeTrue(file.exists());
    return new DataBaseConfig(new ConfigObject(Json.parseJsonObject(Files.readString(file.toPath()))));
  }


  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        HashMap<String, Object> configMap1 = new HashMap<>();
        String masterKey = MasterKey.generateMasterKey();

        final TreeMap<Integer, PrivateKeyBundle> keys;
        {
          KeyPair serverKey = PublicPrivateKeyPartnership.genKeyPair();
          KeyPair clientKey = PublicPrivateKeyPartnership.genKeyPair();

          int keyId = Secrets.insertSecretKey(dataBase, "space", MasterKey.encrypt(masterKey, PublicPrivateKeyPartnership.privateKeyOf(serverKey)));
          String publicKeyForClient = PublicPrivateKeyPartnership.publicKeyOf(serverKey);

          byte[] clientSecret = PublicPrivateKeyPartnership.secretFrom(PublicPrivateKeyPartnership.keyPairFrom(publicKeyForClient, PublicPrivateKeyPartnership.privateKeyOf(clientKey)));
          String cipher = PublicPrivateKeyPartnership.encrypt(clientSecret, "plain-text-secret");

          configMap1.put("secret", keyId + ";" + PublicPrivateKeyPartnership.publicKeyOf(clientKey) + ";" + cipher);
          configMap1.put("secret_fail5", keyId + ";x;" + cipher);
          keys = Secrets.getKeys(dataBase, masterKey, "space");
        }
        configMap1.put("secret_fail1", ";");
        configMap1.put("secret_fail2", "x;z;z");
        configMap1.put("secret_fail3", 3.2);
        configMap1.put("secret_fail4", "100;z;z");

        configMap1.put("int", 3);
        ProductionServiceConfig config1 = new ProductionServiceConfig("space", configMap1, keys);
        Assert.assertEquals("plain-text-secret", config1.getDecryptedSecret("secret"));

        try {
          config1.getDecryptedSecret("secret_fail1");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(786441, ex.code);
        }
        try {
          config1.getDecryptedSecret("secret_fail2");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(716812, ex.code);
        }
        try {
          config1.getDecryptedSecret("secret_fail3");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(723982, ex.code);
        }
        try {
          config1.getDecryptedSecret("secret_fail4");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(785601, ex.code);
        }
        try {
          config1.getDecryptedSecret("secret_fail5");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(782348, ex.code);
        }
        try {
          config1.getInteger("secret_fail2", 0);
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(798735, ex.code);
        }
        try {
          config1.getInteger("secret_fail3", 0);
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(777231, ex.code);
        }
        Assert.assertEquals(3, config1.getInteger("int", -1));
        Assert.assertEquals(100, config1.getInteger("int_not_there", 100));
        Assert.assertEquals("x;z;z", config1.getString("secret_fail2", "xyz"));
        Assert.assertEquals("xyz", config1.getString("not_found", "xyz"));
        try {
          config1.getString("secret_fail3", "0");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(701452, ex.code);
        }
        try {
          config1.getString("not_found", null);
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(786442, ex.code);
        }
      } finally {
        installer.uninstall();
      }
    }
  }

}

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
package org.adamalang.mysql;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class DataBaseConfigTests {

  @Test
  public void missing_role() {
    try {
      new DataBaseConfig(new ConfigObject(Json.parseJsonObject("{}")));
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex instanceof NullPointerException);
      Assert.assertTrue(ex.getMessage().contains("role"));
    }
  }

  @Test
  public void missing_jdbc() {
    try {
      new DataBaseConfig(new ConfigObject(Json.parseJsonObject("{\"db\":{}}")));
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex instanceof NullPointerException);
      Assert.assertTrue(ex.getMessage().contains("jdbc_url"));
    }
  }

  @Test
  public void missing_user() {
    try {
      new DataBaseConfig(new ConfigObject(Json.parseJsonObject("{\"db\":{\"jdbc-url\":\"1\"}}")));
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex instanceof NullPointerException);
      Assert.assertTrue(ex.getMessage().contains("user"));
    }
  }

  @Test
  public void missing_password() {
    try {
      new DataBaseConfig(new ConfigObject(Json.parseJsonObject("{\"db\":{\"jdbc-url\":\"1\",\"user\":\"2\"}}")));
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex instanceof NullPointerException);
      Assert.assertTrue(ex.getMessage().contains("password"));
    }
  }

  @Test
  public void missing_database_name() {
    try {
      new DataBaseConfig(new ConfigObject(Json.parseJsonObject("{\"db\":{\"jdbc-url\":\"1\",\"user\":\"2\",\"password\":\"3\"}}")));
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex instanceof NullPointerException);
      Assert.assertTrue(ex.getMessage().contains("database_name"));
    }
  }

  @Test
  public void ok() throws Exception {
    DataBaseConfig c = new DataBaseConfig(new ConfigObject(Json.parseJsonObject("{\"db\":{\"jdbc-url\":\"1\",\"user\":\"2\",\"password\":\"3\",\"database-name\":\"4\"}}")));
    Assert.assertEquals("1", c.jdbcUrl);
    Assert.assertEquals("2", c.user);
    Assert.assertEquals("3", c.password);
    Assert.assertEquals("4", c.databaseName);
  }

  @Test
  public void skipAndOk() throws Exception {
    DataBaseConfig c = new DataBaseConfig(new ConfigObject(Json.parseJsonObject("{\"db\":{\"jdbc-url\":\"1\",\"user\":\"2\",\"password\":\"3\",\"database-name\":\"4\",\"z\":42},\"z\":123}")));
    Assert.assertEquals("1", c.jdbcUrl);
    Assert.assertEquals("2", c.user);
    Assert.assertEquals("3", c.password);
    Assert.assertEquals("4", c.databaseName);
  }

  @Test
  public void skipAndNotOk() throws Exception {
    try {
      new DataBaseConfig(new ConfigObject(Json.parseJsonObject("{\"x\":{\"jdbc-url\":\"1\",\"user\":\"2\",\"password\":\"3\",\"database-name\":\"4\",\"z\":42},\"any\":123}")));
      Assert.fail();
    } catch (NullPointerException ex) {
      Assert.assertTrue(ex instanceof RuntimeException);
      Assert.assertTrue(ex.getMessage().contains("role was not found"));
    }
  }

  @Test
  public void localIntegration() throws Exception {
    DataBaseConfig dataBaseConfig = getLocalIntegrationConfig();
    dataBaseConfig.createHikariDataSource().close();
  }

  public static DataBaseConfig getLocalIntegrationConfig() throws Exception {
    File file = new File("test.mysql.json");
    Assume.assumeTrue(file.exists());
    return new DataBaseConfig(new ConfigObject(Json.parseJsonObject(Files.readString(file.toPath()))));
  }

  public static DataBaseConfig getLocalIntegrationConfigWithDifferentName(String newName) throws Exception {
    File file = new File("test.mysql.json");
    Assume.assumeTrue(file.exists());
    ObjectNode config = Json.parseJsonObject(Files.readString(file.toPath()));
    ((ObjectNode) config.get("any")).put("database-name", newName);
    return new DataBaseConfig(new ConfigObject(config));
  }
}

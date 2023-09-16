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

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.contracts.MigrationStatus;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.mysql.model.Users;
import org.junit.Assert;
import org.junit.Test;

public class MigrateTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfigFrom = DataBaseConfigTests.getLocalIntegrationConfig();
    DataBaseConfig dataBaseConfigTo = DataBaseConfigTests.getLocalIntegrationConfigWithDifferentName(dataBaseConfigFrom.databaseName + "_dst");
    System.err.println("FROM:" + dataBaseConfigFrom.databaseName);
    System.err.println("  TO:" + dataBaseConfigTo.databaseName);
    try (DataBase dataBaseFrom = new DataBase(dataBaseConfigFrom, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installFrom = new Installer(dataBaseFrom);
      installFrom.install();
      try {
        int userIdAlice = Users.createUserId(dataBaseFrom, "alice@x.com");
        int userIdBob = Users.createUserId(dataBaseFrom, "bob@x.com");
        int userIdCarol = Users.createUserId(dataBaseFrom, "carol@x.com");
        int spaceB = Spaces.createSpace(dataBaseFrom, userIdBob, "space-b");
        Users.setProfileIf(dataBaseFrom, userIdAlice, "New Profile", "");
        try (DataBase dataBaseTo = new DataBase(dataBaseConfigTo, new DataBaseMetrics(new NoOpMetricsFactory()))) {
          Installer installerTo = new Installer(dataBaseTo);
          installerTo.install();
          try {
            Migrate.copy(dataBaseFrom, dataBaseTo, new MigrationStatus() {
              @Override
              public void table(String name) {
                System.err.println("TABLE:" + name);
              }
            });
            Assert.assertEquals("New Profile", Users.getProfile(dataBaseTo, userIdAlice));
          } finally {
            installerTo.uninstall();
          }
        }
      } finally {
        installFrom.uninstall();
      }
    }
  }
}

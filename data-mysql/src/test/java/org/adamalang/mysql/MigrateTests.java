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
        int userIdAlice = Users.getOrCreateUserId(dataBaseFrom, "alice@x.com");
        int userIdBob = Users.getOrCreateUserId(dataBaseFrom, "bob@x.com");
        int userIdCarol = Users.getOrCreateUserId(dataBaseFrom, "carol@x.com");
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

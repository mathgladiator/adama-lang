/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseTests {
  @Test
  public void failure_coverage() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory(), "noop"))) {
      Connection connection = dataBase.pool.getConnection();
      boolean ex = false;
      try {
        DataBase.execute(connection, "INSERT");
        Assert.fail();
      } catch (SQLException sqlex) {
        ex = true;
      } finally {
        connection.close();
      }
      Assert.assertTrue(ex);
    }
  }
}

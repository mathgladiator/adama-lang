package org.adamalang.mysql;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseTests {
    @Test
    public void failure_coverage() throws Exception {
        BaseConfig baseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
        try (DataBase dataBase = new DataBase(baseConfig)) {
            Connection connection = dataBase.pool.getConnection();
            try {
                DataBase.execute(connection, "INSERT");
            } catch (SQLException ex) {
                Assert.assertTrue(ex.getMessage().contains("You have an error in your SQL syntax"));
            } finally {
                connection.close();
            }
        }
    }
}

package org.adamalang.mysql;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class BaseTests {
    @Test
    public void failure_coverage() throws Exception {
        BaseConfig baseConfig = BaseConfigTests.getLocalIntegrationConfig();
        try (Base base = new Base(baseConfig)) {
            Connection connection = base.pool.getConnection();
            try {
                Base.execute(connection, "INSERT");
            } catch (SQLException ex) {
                Assert.assertTrue(ex.getMessage().contains("You have an error in your SQL syntax"));
            } finally {
                connection.close();
            }
        }
    }
}

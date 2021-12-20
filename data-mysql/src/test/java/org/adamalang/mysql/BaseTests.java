package org.adamalang.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class BaseTests {
    @Test
    public void failure_coverage() throws Exception {
        Config config = ConfigTests.getLocalIntegrationConfig();
        ComboPooledDataSource pool = config.createComboPooledDataSource();
        try {
            Base base = new Base(pool, "failures_1");
            Connection connection = pool.getConnection();
            try {
                Base.execute(connection, "INSERT");
            } catch (SQLException ex) {
                Assert.assertTrue(ex.getMessage().contains("You have an error in your SQL syntax"));
            } finally {
                connection.close();
            }

        } finally {
            pool.close();
        }
    }
}

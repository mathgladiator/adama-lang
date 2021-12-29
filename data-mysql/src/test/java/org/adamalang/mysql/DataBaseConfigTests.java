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

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class DataBaseConfigTests {

    @Test
    public void missing_role() {
        try {
            new BaseConfig("{}", "x");
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof NullPointerException);
            Assert.assertTrue(ex.getMessage().contains("role"));
        }
    }

    @Test
    public void missing_jdbc() {
        try {
            new BaseConfig("{\"x\":{}}", "x");
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof NullPointerException);
            Assert.assertTrue(ex.getMessage().contains("jdbc_url"));
        }
    }

    @Test
    public void missing_user() {
        try {
            new BaseConfig("{\"x\":{\"jdbc_url\":\"1\"}}", "x");
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof NullPointerException);
            Assert.assertTrue(ex.getMessage().contains("user"));
        }
    }

    @Test
    public void missing_password() {
        try {
            new BaseConfig("{\"x\":{\"jdbc_url\":\"1\",\"user\":\"2\"}}", "x");
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof NullPointerException);
            Assert.assertTrue(ex.getMessage().contains("password"));
        }
    }

    @Test
    public void missing_database_name() {
        try {
            new BaseConfig("{\"x\":{\"jdbc_url\":\"1\",\"user\":\"2\",\"password\":\"3\"}}", "x");
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof NullPointerException);
            Assert.assertTrue(ex.getMessage().contains("database_name"));
        }
    }

    @Test
    public void ok() throws Exception {
        BaseConfig c = new BaseConfig("{\"x\":{\"jdbc_url\":\"1\",\"user\":\"2\",\"password\":\"3\",\"database_name\":\"4\"}}", "x");
        Assert.assertEquals("1", c.jdbcUrl);
        Assert.assertEquals("2", c.user);
        Assert.assertEquals("3", c.password);
        Assert.assertEquals("4", c.databaseName);
    }

    public static BaseConfig getLocalIntegrationConfig() throws Exception {
        return new BaseConfig(new File("test.mysql.json"), "any");
    }

    @Test
    public void localIntegration() throws Exception {
        BaseConfig baseConfig = getLocalIntegrationConfig();
        baseConfig.createComboPooledDataSource().close();
    }

}

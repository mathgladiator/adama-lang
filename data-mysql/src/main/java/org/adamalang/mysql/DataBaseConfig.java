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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.adamalang.common.ConfigObject;

/** defines the config for the mysql data service */
public class DataBaseConfig {
  public final String jdbcUrl;
  public final String user;
  public final String password;
  public final String databaseName;
  public final int maxStatements;
  public final int maxStatementsPerConnection;
  public final int maxPoolSize;
  public final int minPoolSize;
  public final int initialPoolSize;

  public DataBaseConfig(ConfigObject config) {
    ConfigObject roleConfig = config.childSearchMustExist("role was not found", "db", "any");
    this.jdbcUrl = roleConfig.strOfButCrash("jdbc_url", "jdbc_url was not present in config");
    this.user = roleConfig.strOfButCrash("user", "user was not present in config");
    this.password = roleConfig.strOfButCrash("password", "password was not present in config");
    this.databaseName = roleConfig.strOfButCrash("database_name", "database_name was not present in config");
    this.maxStatements = roleConfig.intOf("max_statements", 0);
    this.maxStatementsPerConnection = roleConfig.intOf("max_statements_per_connection", 0);
    this.maxPoolSize = roleConfig.intOf("max_pool_size", 4);
    this.minPoolSize = roleConfig.intOf("min_pool_size", 2);
    this.initialPoolSize = roleConfig.intOf("initial_pool_size", 2);
  }

  public ComboPooledDataSource createComboPooledDataSource() throws Exception {
    ComboPooledDataSource pool = new ComboPooledDataSource();
    pool.setDriverClass("com.mysql.cj.jdbc.Driver"); // loads the jdbc driver
    pool.setJdbcUrl(jdbcUrl);
    pool.setUser(user);
    pool.setPassword(password);
    pool.setMaxStatements(maxStatements);
    pool.setMaxStatementsPerConnection(maxStatementsPerConnection);
    pool.setMaxPoolSize(maxPoolSize);
    pool.setMinPoolSize(minPoolSize);
    pool.setInitialPoolSize(initialPoolSize);
    return pool;
  }
}

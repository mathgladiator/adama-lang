/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.adamalang.common.ConfigObject;

/** defines the config for the mysql data service */
public class DataBaseConfig {
  public final String jdbcUrl;
  public final String user;
  public final String password;
  public final String databaseName;
  /*
  public final int maxStatements;
  public final int maxStatementsPerConnection;
  public final int maxPoolSize;
  public final int minPoolSize;
  public final int initialPoolSize;
  */

  public DataBaseConfig(ConfigObject config) {
    ConfigObject roleConfig = config.childSearchMustExist("role was not found", "db", "any");
    this.jdbcUrl = roleConfig.strOfButCrash("jdbc-url", "jdbc_url was not present in config");
    this.user = roleConfig.strOfButCrash("user", "user was not present in config");
    this.password = roleConfig.strOfButCrash("password", "password was not present in config");
    this.databaseName = roleConfig.strOfButCrash("database-name", "database_name was not present in config");

    /*
    this.maxStatements = roleConfig.intOf("max_statements", 0);
    this.maxStatementsPerConnection = roleConfig.intOf("max_statements_per_connection", 0);
    this.maxPoolSize = roleConfig.intOf("max_pool_size", 4);
    this.minPoolSize = roleConfig.intOf("min_pool_size", 2);
    this.initialPoolSize = roleConfig.intOf("initial_pool_size", 2);
    */
  }

  public HikariDataSource createHikariDataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(user);
    config.setPassword(password);
    return new HikariDataSource(config);
  }
}

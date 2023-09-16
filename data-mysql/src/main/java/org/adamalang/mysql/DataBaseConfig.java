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

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.deployments;

import org.adamalang.mysql.DataBase;

import java.sql.Connection;

public class DeployedInstaller {
  public final DataBase dataBase;

  public DeployedInstaller(DataBase dataBase) {
    this.dataBase = dataBase;
  }

  public void install() throws Exception {
    String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS `" + dataBase.databaseName + "`";

    String createDeployedTableSQL = new StringBuilder() //
                                                        .append("CREATE TABLE IF NOT EXISTS `").append(dataBase.databaseName).append("`.`deployed` (") //
                                                        .append("  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,") //
                                                        .append("  `space` VARCHAR(128) NOT NULL,") //
                                                        .append("  `target` VARCHAR(256) NOT NULL,") //
                                                        .append("  `hash` VARCHAR(256) NOT NULL,") //
                                                        .append("  `plan` LONGTEXT NOT NULL,") //
                                                        .append("  PRIMARY KEY (`id`))") //
                                                        .append(" ENGINE = InnoDB") //
                                                        .append(" DEFAULT CHARACTER SET = utf8mb4;") //
                                                        .toString();
    Connection connection = dataBase.pool.getConnection();
    try {
      DataBase.execute(connection, createDatabaseSQL);
      DataBase.execute(connection, createDeployedTableSQL);
    } finally {
      connection.close();
    }
  }

  public void uninstall() throws Exception {
    Connection connection = dataBase.pool.getConnection();
    try {
      DataBase.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(dataBase.databaseName).append("`.`deployed`;").toString());
      DataBase.execute(connection, new StringBuilder("DROP DATABASE IF EXISTS `").append(dataBase.databaseName).append("`;").toString());
    } finally {
      connection.close();
    }
  }
}

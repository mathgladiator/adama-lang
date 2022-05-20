/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.finder;

import org.adamalang.mysql.DataBase;

import java.sql.Connection;

/** how to find where a key is located via MySQL */
public class FinderInstaller {
  public final DataBase dataBase;

  public FinderInstaller(DataBase dataBase) {
    this.dataBase = dataBase;
  }

  public void install() throws Exception {
    String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS `" + dataBase.databaseName + "`";

    String createDirectoryTableSQL = new StringBuilder() //
        .append("CREATE TABLE IF NOT EXISTS `").append(dataBase.databaseName).append("`.`directory` (") //
        .append("  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,") //
        .append("  `space` VARCHAR(128) NOT NULL,") //
        .append("  `key` VARCHAR(512) NOT NULL,") //
        .append("  `created` DATETIME DEFAULT CURRENT_TIMESTAMP,") //
        .append("  `updated` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,") //
        .append("  `head_seq` INT(4) UNSIGNED NOT NULL,") //
        .append("  `active` BOOLEAN NOT NULL,") //
        .append("  `type` INT(2) UNSIGNED NOT NULL,") //
        .append("  `region` VARCHAR(64) NOT NULL,") //
        .append("  `machine` VARCHAR(512) NOT NULL,") //
        .append("  `archive` VARCHAR(512) NOT NULL,") //
        .append("  `delta_bytes` BIGINT UNSIGNED NOT NULL,") //
        .append("  `asset_bytes` BIGINT UNSIGNED NOT NULL,") //
        .append("  PRIMARY KEY (`id`),") //
        .append("  UNIQUE  `u` (`space`, `key`))") //
        .append(" ENGINE = InnoDB") //
        .append(" DEFAULT CHARACTER SET = utf8mb4;") //
        .toString();

    Connection connection = dataBase.pool.getConnection();
    try {
      DataBase.execute(connection, createDatabaseSQL);
      DataBase.execute(connection, createDirectoryTableSQL);
    } finally {
      connection.close();
    }
  }

  public void uninstall() throws Exception {
    Connection connection = dataBase.pool.getConnection();
    try {
      DataBase.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(dataBase.databaseName).append("`.`directory`;").toString());
      DataBase.execute(connection, new StringBuilder("DROP DATABASE IF EXISTS `").append(dataBase.databaseName).append("`;").toString());
    } finally {
      connection.close();
    }
  }
}

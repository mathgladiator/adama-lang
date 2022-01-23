/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.backend;

import org.adamalang.mysql.DataBase;

import java.sql.Connection;

/** handy-dany installer to setup the tables */
public class BackendDataServiceInstaller {
  public final DataBase dataBase;

  public BackendDataServiceInstaller(DataBase dataBase) {
    this.dataBase = dataBase;
  }

  public void install() throws Exception {
    String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS `" + dataBase.databaseName + "`";

    String createIndexTableSQL = new StringBuilder() //
                                                     .append("CREATE TABLE IF NOT EXISTS `").append(dataBase.databaseName).append("`.`index` (") //
                                                     .append("  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,") //
                                                     .append("  `space` VARCHAR(128) NOT NULL,") //
                                                     .append("  `key` VARCHAR(512) NOT NULL,") //
                                                     .append("  `created` DATETIME DEFAULT CURRENT_TIMESTAMP,") //
                                                     .append("  `updated` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,") //
                                                     .append("  `head_seq` INT(4) UNSIGNED NOT NULL,") //
                                                     .append("  `invalidate` BOOLEAN NOT NULL,") //
                                                     .append("  `when` DATETIME NOT NULL,") //
                                                     .append("  PRIMARY KEY (`id`),") //
                                                     .append("  UNIQUE  `u` (`space`, `key`))") //
                                                     .append(" ENGINE = InnoDB") //
                                                     .append(" DEFAULT CHARACTER SET = utf8mb4;") //
                                                     .toString();

    String createDeltasTableSQL = new StringBuilder() //
                                                      .append("CREATE TABLE IF NOT EXISTS `").append(dataBase.databaseName).append("`.`deltas` (") //
                                                      .append("  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,") //
                                                      .append("  `parent` INT(4) UNSIGNED NOT NULL,") //
                                                      .append("  `seq_begin` INT(4) UNSIGNED NOT NULL,") //
                                                      .append("  `seq_end` INT(4) UNSIGNED NOT NULL,") //
                                                      .append("  `who_agent` VARCHAR(64) NULL,") //
                                                      .append("  `who_authority` VARCHAR(64) NULL,") //
                                                      .append("  `request` LONGTEXT NULL,") //
                                                      .append("  `redo` LONGTEXT NOT NULL,") //
                                                      .append("  `undo` LONGTEXT NOT NULL,") //
                                                      .append("  `history_ptr` VARCHAR(64) NOT NULL,") //
                                                      .append("  PRIMARY KEY (`id`),") //
                                                      .append("  INDEX `s` (`parent` ASC, `seq_begin` ASC, `seq_end` ASC) VISIBLE)") //
                                                      .append(" ENGINE = InnoDB") //
                                                      .append(" DEFAULT CHARACTER SET = utf8mb4;") //
                                                      .toString();

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
      DataBase.execute(connection, createIndexTableSQL);
      DataBase.execute(connection, createDeltasTableSQL);
      DataBase.execute(connection, createDeployedTableSQL);
    } finally {
      connection.close();
    }
  }

  public void uninstall() throws Exception {
    Connection connection = dataBase.pool.getConnection();
    try {
      DataBase.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(dataBase.databaseName).append("`.`deployed`;").toString());
      DataBase.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(dataBase.databaseName).append("`.`deltas`;").toString());
      DataBase.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(dataBase.databaseName).append("`.`index`;").toString());
      DataBase.execute(connection, new StringBuilder("DROP DATABASE `").append(dataBase.databaseName).append("`;").toString());
    } finally {
      connection.close();
    }
  }
}

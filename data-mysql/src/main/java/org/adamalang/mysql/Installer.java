/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql;

import java.sql.Connection;

/** The installer for the database */
public class Installer {
  public final DataBase dataBase;

  public Installer(DataBase dataBase) {
    this.dataBase = dataBase;
  }

  public void install() throws Exception {
    String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS `" + dataBase.databaseName + "`";

    String createCapacityTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`capacity` (" + //
            "  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `space` VARCHAR(128) NOT NULL," + //
            "  `region` VARCHAR(256) NOT NULL," + //
            "  `machine` VARCHAR(256) NOT NULL," + //
            "  `override` BOOLEAN DEFAULT FALSE," + //
            "  PRIMARY KEY (`id`)," + //
            "  INDEX `s` (`space` ASC)," + //
            "  INDEX `r` (`region` ASC)," + //
            "  UNIQUE `srm` (`space`,`region`,`machine`))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createDirectoryTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`directory` (" + //
            "  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `space` VARCHAR(128) NOT NULL," + //
            "  `key` VARCHAR(512) NOT NULL," + //
            "  `created` DATETIME DEFAULT CURRENT_TIMESTAMP," + //
            "  `updated` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," + //
            "  `head_seq` INT(4) UNSIGNED NOT NULL," + //
            "  `need_gc` BOOLEAN DEFAULT TRUE," + //
            "  `type` INT(2) UNSIGNED NOT NULL," + //
            "  `region` VARCHAR(64) NOT NULL," + //
            "  `machine` VARCHAR(512) NOT NULL," + //
            "  `archive` VARCHAR(512) NOT NULL," + //
            "  `deleted` BOOLEAN DEFAULT FALSE," + //
            "  `delta_bytes` BIGINT UNSIGNED NOT NULL," + //
            "  `asset_bytes` BIGINT UNSIGNED NOT NULL," + //
            "  PRIMARY KEY (`id`)," + //
            "  UNIQUE `u` (`space`, `key`)," + //
            "  INDEX `gc` (`need_gc`))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createDocumentSecrets = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`document_secrets` (" + //
            "  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `space` VARCHAR(128) NOT NULL," + //
            "  `key` VARCHAR(512) NOT NULL," + //
            "  `name` VARCHAR(64) NOT NULL," + //
            "  `secret` VARCHAR(2048) NOT NULL," + //
            "  PRIMARY KEY (`id`)," + //
            "  UNIQUE `u` (`space`, `key`, `name`))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createEmailsTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`emails` (" + //
            "  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `email` VARCHAR(128) NOT NULL," + //
            "  `profile` TEXT," + //
            "  `password` TEXT NOT NULL," + //
            "  `balance` INT(4) DEFAULT 0," + //
            "  `credit_carry_limit` INT(4) DEFAULT -500," + //
            "  `payment_info_json` TEXT," + //
            "  `created` DATETIME DEFAULT CURRENT_TIMESTAMP," + //
            "  `validations` INT(4) UNSIGNED NOT NULL," + //
            "  `last_validated` DATETIME NULL," + //
            "  PRIMARY KEY (`id`)," + //
            "  UNIQUE  `u` (`email`))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createInitiationsTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`initiations` (" + //
            "  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `user` INT(4) UNSIGNED NOT NULL," + //
            "  `hash` TEXT NOT NULL," + //
            "  `created` DATETIME DEFAULT CURRENT_TIMESTAMP," + //
            "  `expires` DATETIME," + //
            "  PRIMARY KEY (`id`)," + //
            "  INDEX `u` (`user` ASC))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createAccessKeysTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`email_keys` (" + //
            "  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `user` INT(4) UNSIGNED NOT NULL," + //
            "  `public_key` TEXT NOT NULL," + //
            "  `created` DATETIME DEFAULT CURRENT_TIMESTAMP," + //
            "  `expires` DATETIME," + //
            "  PRIMARY KEY (`id`)," + //
            "  INDEX `u` (`user` ASC))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createSpaceTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`spaces` (" + //
            "  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `owner` INT(4) UNSIGNED NOT NULL," + //
            "  `name` VARCHAR(128) NOT NULL," + //
            "  `enabled` BOOLEAN DEFAULT TRUE," + //
            "  `storage_bytes` BIGINT DEFAULT 0," + //
            "  `unbilled_storage_bytes_hours` BIGINT DEFAULT 0," + // TOKILL
            "  `unbilled_bandwidth_hours` BIGINT DEFAULT 0," + // TOKILL
            "  `unbilled_first_party_service_calls` INT(4) DEFAULT 0," + // TOKILL
            "  `unbilled_third_party_service_calls` INT(4) DEFAULT 0," + // TOKILL
            "  `latest_billing_hour` INT(4) UNSIGNED DEFAULT 0," + //
            "  `plan` MEDIUMTEXT NOT NULL," + // MOVE to IDE document?
            "  `rxhtml` MEDIUMTEXT," + // MOVE to IDE document?
            "  `hash` VARCHAR(256) NOT NULL," + //
            "  `created` DATETIME DEFAULT CURRENT_TIMESTAMP," + //
            "  `updated` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," + //
            "  PRIMARY KEY (`id`)," + //
            "  UNIQUE `u` (`name`)," + //
            "  INDEX `c` (`owner`))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createGrantTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`grants` (" + //
            "  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `space` INT(4) UNSIGNED NOT NULL," + //
            "  `user` INT(4) UNSIGNED NOT NULL," + //
            "  `role` INT(1) UNSIGNED NOT NULL," + //
            "  PRIMARY KEY (`id`)," + //
            "  INDEX  `u` (`space`, `user` ASC))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createAuthoritiesTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`authorities` (" + //
            "  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `owner` INT(4) UNSIGNED NOT NULL," + //
            "  `authority` VARCHAR(64) NOT NULL," + //
            "  `keystore` TEXT NOT NULL," + //
            "  `created` DATETIME DEFAULT CURRENT_TIMESTAMP," + //
            "  PRIMARY KEY (`id`)," + //
            "  INDEX `o` (`owner`)," + //
            "  UNIQUE `s` (`authority`)," + //
            "  INDEX `c` (`created` DESC))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createHostsTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`hosts` (" + //
            "  `id` INT(6) UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `role` VARCHAR(16) NOT NULL," + //
            "  `region` VARCHAR(64) NOT NULL," + //
            "  `machine` VARCHAR(512) NOT NULL," + //
            "  `public_key` LONGTEXT NOT NULL," + "  `created` DATETIME DEFAULT CURRENT_TIMESTAMP," + //
            "  PRIMARY KEY (`id`)," + //
            "  INDEX `m` (`machine`))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createSecretsTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`secrets` (" + //
            "  `id` INT(6) UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `space` VARCHAR(128) NOT NULL," + //
            "  `encrypted_private_key` LONGTEXT NOT NULL," + "  PRIMARY KEY (`id`)," + //
            "  INDEX `space` (`space`))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createDomainsTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`domains` (" + //
            "  `id` INT(6) UNSIGNED NOT NULL AUTO_INCREMENT," + //
            "  `owner` INT(4) UNSIGNED NOT NULL," + //
            "  `space` VARCHAR(128) NOT NULL," + //
            "  `key` VARCHAR(128)," + //
            "  `route` BOOLEAN DEFAULT FALSE," + //
            "  `domain` VARCHAR(254) NOT NULL," + //
            "  `certificate` LONGTEXT NOT NULL," + //
            "  `automatic` BOOLEAN DEFAULT FALSE," + //
            "  `automatic_timestamp` BIGINT UNSIGNED DEFAULT 0," + //
            "  `created` DATETIME DEFAULT CURRENT_TIMESTAMP," + //
            "  `updated` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," + //
            "  PRIMARY KEY (`id`)," + //
            "  UNIQUE `d` (`domain`)," + //
            "  INDEX `o` (`owner`)," + //
            "  INDEX `s` (`space`))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    String createSentinelTableSQL = //
        "CREATE TABLE IF NOT EXISTS `" + dataBase.databaseName + "`.`sentinel` (" + //
            "  `aspect` VARCHAR(128) NOT NULL," + //
            "  `timestamp` BIGINT UNSIGNED DEFAULT 0," + //
            "  PRIMARY KEY (`aspect`))" + //
            " ENGINE = InnoDB" + //
            " DEFAULT CHARACTER SET = utf8mb4;" //
        ;

    Connection connection = dataBase.pool.getConnection();
    try {
      DataBase.execute(connection, createDatabaseSQL);
      DataBase.execute(connection, createCapacityTableSQL);
      DataBase.execute(connection, createDirectoryTableSQL);
      DataBase.execute(connection, createDocumentSecrets);
      DataBase.execute(connection, createInitiationsTableSQL);
      DataBase.execute(connection, createEmailsTableSQL);
      DataBase.execute(connection, createAccessKeysTableSQL);
      DataBase.execute(connection, createSpaceTableSQL);
      DataBase.execute(connection, createGrantTableSQL);
      DataBase.execute(connection, createAuthoritiesTableSQL);
      DataBase.execute(connection, createHostsTableSQL);
      DataBase.execute(connection, createSecretsTableSQL);
      DataBase.execute(connection, createDomainsTableSQL);
      DataBase.execute(connection, createSentinelTableSQL);
    } finally {
      connection.close();
    }
  }

  public void uninstall() throws Exception {
    Connection connection = dataBase.pool.getConnection();
    try {
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`emails`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`initiations`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`email_keys`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`spaces`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`grants`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`authorities`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`directory`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`document_secrets`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`capacity`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`hosts`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`secrets`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`domains`;");
      DataBase.execute(connection, "DROP TABLE IF EXISTS `" + dataBase.databaseName + "`.`sentinel`;");
      DataBase.execute(connection, "DROP DATABASE IF EXISTS `" + dataBase.databaseName + "`;");
    } finally {
      connection.close();
    }
  }
}

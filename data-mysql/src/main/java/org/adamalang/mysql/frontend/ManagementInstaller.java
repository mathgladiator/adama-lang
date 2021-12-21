package org.adamalang.mysql.frontend;

import org.adamalang.mysql.Base;

import java.sql.Connection;

public class ManagementInstaller {
    public final Base base;

    public ManagementInstaller(Base base) {
        this.base = base;
    }

    public void install() throws Exception {
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS `" + base.databaseName + "`";

        String createEmailsTableSQL = new StringBuilder() //
            .append("CREATE TABLE IF NOT EXISTS `" + base.databaseName + "`.`emails` (") //
            .append("  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,") //
            .append("  `email` VARCHAR(128) NOT NULL,") //
            .append("  PRIMARY KEY (`id`),") //
            .append("  UNIQUE  `u` (`email`))") //
            .append(" ENGINE = InnoDB") //
            .append(" DEFAULT CHARACTER SET = utf8;") //
            .toString();

        String createAccessKeysTableSQL = new StringBuilder() //
            .append("CREATE TABLE IF NOT EXISTS `" + base.databaseName + "`.`email_keys` (") //
            .append("  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,") //
            .append("  `user` INT(4) UNSIGNED NOT NULL,") //
            .append("  `public_key` TEXT NOT NULL,") //
            .append("  `created` DATETIME DEFAULT CURRENT_TIMESTAMP,") //
            .append("  `expires` DATETIME,") //
            .append("  PRIMARY KEY (`id`),") //
            .append("  INDEX `u` (`user` ASC))") //
            .append(" ENGINE = InnoDB") //
            .append(" DEFAULT CHARACTER SET = utf8;") //
            .toString();

        String createSpaceTableSQL = new StringBuilder() //
            .append("CREATE TABLE IF NOT EXISTS `" + base.databaseName + "`.`spaces` (") //
            .append("  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,") //
            .append("  `owner` INT(4) UNSIGNED NOT NULL,") //
            .append("  `name` VARCHAR(128) NOT NULL,") //
            .append("  `billing` VARCHAR(16) NOT NULL,") //
            .append("  `plan` TEXT NOT NULL,") //
            .append("  `created` DATETIME DEFAULT CURRENT_TIMESTAMP,") //
            .append("  `updated` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,") //
            .append("  PRIMARY KEY (`id`),") //
            .append("  UNIQUE `u` (`name`),") //
            .append("  INDEX `c` (`owner`))") //
            .append(" ENGINE = InnoDB") //
            .append(" DEFAULT CHARACTER SET = utf8;") //
            .toString();

        String createGrantTableSQL  = new StringBuilder() //
            .append("CREATE TABLE IF NOT EXISTS `" + base.databaseName + "`.`grants` (") //
            .append("  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,") //
            .append("  `space` INT(4) UNSIGNED NOT NULL,") //
            .append("  `user` INT(4) UNSIGNED NOT NULL,") //
            .append("  `role` INT(1) UNSIGNED NOT NULL,") //
            .append("  PRIMARY KEY (`id`),") //
            .append("  INDEX  `u` (`space`, `user` ASC))") //
            .append(" ENGINE = InnoDB") //
            .append(" DEFAULT CHARACTER SET = utf8;") //
            .toString();

        String createAuthoritiesTableSQL = new StringBuilder() //
            .append("CREATE TABLE IF NOT EXISTS `" + base.databaseName + "`.`authorities` (") //
            .append("  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,") //
            .append("  `owner` INT(4) UNSIGNED NOT NULL,") //
            .append("  `authority` VARCHAR(64) NOT NULL,") //
            .append("  `keystore` TEXT NOT NULL,") //
            .append("  `created` DATETIME DEFAULT CURRENT_TIMESTAMP,") //
            .append("  PRIMARY KEY (`id`),") //
            .append("  INDEX `o` (`owner`),") //
            .append("  UNIQUE `s` (`authority`),") //
            .append("  INDEX `c` (`created` DESC))") //
            .append(" ENGINE = InnoDB") //
            .append(" DEFAULT CHARACTER SET = utf8;") //
            .toString();

        Connection connection = base.pool.getConnection();
        try {
            Base.execute(connection, createDatabaseSQL);
            Base.execute(connection, createEmailsTableSQL);
            Base.execute(connection, createAccessKeysTableSQL);
            Base.execute(connection, createSpaceTableSQL);
            Base.execute(connection, createGrantTableSQL);
            Base.execute(connection, createAuthoritiesTableSQL);
        } finally {
            connection.close();
        }
    }

    public void uninstall() throws Exception {
        Connection connection = base.pool.getConnection();
        try {
            Base.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(base.databaseName).append("`.`emails`;").toString());
            Base.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(base.databaseName).append("`.`email_keys`;").toString());
            Base.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(base.databaseName).append("`.`spaces`;").toString());
            Base.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(base.databaseName).append("`.`grants`;").toString());
            Base.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(base.databaseName).append("`.`authorities`;").toString());
            Base.execute(connection, new StringBuilder("DROP DATABASE `").append(base.databaseName).append("`;").toString());
        } finally {
            connection.close();
        }
    }
}

package org.adamalang.mysql;

import java.sql.Connection;

/** handy-dany installer to setup the tables */
public class DataServiceInstaller {
    public final Base base;

    public DataServiceInstaller(Base base) {
        this.base = base;
    }

    public void install() throws Exception {
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS `" + base.databaseName + "`";

        String createIndexTableSQL = new StringBuilder() //
            .append("CREATE TABLE IF NOT EXISTS `").append(base.databaseName).append("`.`index` (") //
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
            .append(" DEFAULT CHARACTER SET = utf8;") //
            .toString();

        String createDeltasTableSQL = new StringBuilder() //
            .append("CREATE TABLE IF NOT EXISTS `").append(base.databaseName).append("`.`deltas` (") //
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
            .append(" DEFAULT CHARACTER SET = utf8;") //
            .toString();

        Connection connection = base.pool.getConnection();
        try {
            Base.execute(connection, createDatabaseSQL);
            Base.execute(connection, createIndexTableSQL);
            Base.execute(connection, createDeltasTableSQL);
        } finally {
            connection.close();
        }
    }

    public void uninstall() throws Exception {
        Connection connection = base.pool.getConnection();
        try {
            Base.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(base.databaseName).append("`.`deltas`;").toString());
            Base.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(base.databaseName).append("`.`index`;").toString());
            Base.execute(connection, new StringBuilder("DROP DATABASE `").append(base.databaseName).append("`;").toString());
        } finally {
            connection.close();
        }
    }
}

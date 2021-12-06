package org.adamalang.data.mysql;

import java.sql.Connection;

public class Installer {
    public final MySqlBase base;
    public final String databaseName;

    public Installer(MySqlBase base, String databaseName) {
        this.base = base;
        this.databaseName = databaseName;
    }

    public void install() throws Exception {
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS `" + databaseName + "`";

        StringBuilder createIndexTableBuilder = new StringBuilder();
        createIndexTableBuilder.append("CREATE TABLE IF NOT EXISTS `" + databaseName + "`.`index` (");
        createIndexTableBuilder.append("  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,");
        // these require validation on the API side, so this... hrmm
        createIndexTableBuilder.append("  `space` VARCHAR(128) NOT NULL,");
        createIndexTableBuilder.append("  `key` VARCHAR(256) NOT NULL,");
        createIndexTableBuilder.append("  `created` DATETIME DEFAULT CURRENT_TIMESTAMP,");
        createIndexTableBuilder.append("  `updated` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,");
        createIndexTableBuilder.append("  `head_seq` INT(4) UNSIGNED NOT NULL,");
        createIndexTableBuilder.append("  `invalidate` BOOLEAN NOT NULL,");
        createIndexTableBuilder.append("  `when` DATETIME NOT NULL,");
        createIndexTableBuilder.append("  PRIMARY KEY (`id`),");
        createIndexTableBuilder.append("  UNIQUE  `u` (`space`, `key`))");
        createIndexTableBuilder.append(" ENGINE = InnoDB");
        createIndexTableBuilder.append(" DEFAULT CHARACTER SET = utf8;");
        String createIndexTableSQL = createIndexTableBuilder.toString();

        StringBuilder createDeltasTableBuilder = new StringBuilder();
        createDeltasTableBuilder.append("CREATE TABLE IF NOT EXISTS `" + databaseName + "`.`deltas` (");
        createDeltasTableBuilder.append("  `id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,");
        createDeltasTableBuilder.append("  `parent` INT(4) UNSIGNED NOT NULL,");
        createDeltasTableBuilder.append("  `seq_begin` INT(4) UNSIGNED NOT NULL,");
        createDeltasTableBuilder.append("  `seq_end` INT(4) UNSIGNED NOT NULL,");
        createDeltasTableBuilder.append("  `who_agent` VARCHAR(64) NULL,");
        createDeltasTableBuilder.append("  `who_authority` VARCHAR(64) NULL,");
        createDeltasTableBuilder.append("  `request` LONGTEXT NULL,");
        createDeltasTableBuilder.append("  `redo` LONGTEXT NOT NULL,");
        createDeltasTableBuilder.append("  `undo` LONGTEXT NOT NULL,");
        createDeltasTableBuilder.append("  `history_ptr` VARCHAR(64) NOT NULL,");
        createDeltasTableBuilder.append("  PRIMARY KEY (`id`),");
        createDeltasTableBuilder.append("  INDEX `s` (`parent` ASC, `seq_begin` ASC, `seq_end` ASC) VISIBLE)");
        createDeltasTableBuilder.append(" ENGINE = InnoDB");
        createDeltasTableBuilder.append(" DEFAULT CHARACTER SET = utf8;");
        String createDeltasTableSQL = createDeltasTableBuilder.toString();


        Connection connection = base.pool.getConnection();
        try {
            MySqlBase.execute(connection, createDatabaseSQL);
            MySqlBase.execute(connection, createIndexTableSQL);
            MySqlBase.execute(connection, createDeltasTableSQL);
        } finally {
            connection.close();
        }
    }

    public void uninstall() throws Exception {
        Connection connection = base.pool.getConnection();
        try {
            MySqlBase.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(databaseName).append("`.`deltas`;").toString());
            MySqlBase.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(databaseName).append("`.`index`;").toString());
            MySqlBase.execute(connection, new StringBuilder("DROP DATABASE `").append(databaseName).append("`;").toString());
        } finally {
            connection.close();
        }
    }
}

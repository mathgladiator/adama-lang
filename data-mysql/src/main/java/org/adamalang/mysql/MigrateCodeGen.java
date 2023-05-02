/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql;

import org.adamalang.common.ConfigObject;
import org.adamalang.common.DefaultCopyright;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** This a useful tool to replicate database (which is not operational nor financial) */
public class MigrateCodeGen {

  private static HashMap<String, String> lookups(String... kvps) {
    HashMap<String, String> map = new HashMap<>();
    for (int k = 0; k + 1 < kvps.length; k++) {
      map.put(kvps[k], kvps[k + 1]);
    }
    return map;
  }

  private static void makeCopy(DataBase dataBase, String tableName, StringBuilder java, boolean index, HashMap<String, String> translations) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "DESCRIBE `" + dataBase.databaseName + "`.`" + tableName + "`";
      ArrayList<String> fieldsRead = new ArrayList<>();
      ArrayList<String> fieldsInsert = new ArrayList<>();
      ArrayList<String> questionMarks = new ArrayList<>();
      ArrayList<String> copyLine = new ArrayList<>();
      AtomicBoolean hasId = new AtomicBoolean(false);
      AtomicInteger fieldIdRead = new AtomicInteger(0);
      AtomicInteger fieldIdWrite = new AtomicInteger(0);
      AtomicInteger fieldId = new AtomicInteger(-1);
      DataBase.walk(connection, (rs) -> {
        fieldIdRead.incrementAndGet();
        fieldsRead.add("`" + rs.getString(1) + "`");
        if ("id".equals(rs.getString(1))) {
          hasId.set(true);
          if (index) {
            fieldId.set(fieldIdRead.get());;
          }
        } else {
          fieldIdWrite.incrementAndGet();
          fieldsInsert.add("`" + rs.getString(1) + "`");
          questionMarks.add("?");

          String translate = translations.get(rs.getString(1));
          if (translate != null) {
            copyLine.add("_ins.setInt(" + fieldIdWrite.get() + ", _index_" + translate + ".get(rs.getInt(" + fieldIdRead.get() + ")))");
          } else {
            String fieldType = rs.getString(2);
            if (fieldType.startsWith("varchar")) {
              fieldType = "varchar";
            }
            switch (fieldType) {
              case "int":
              case "int unsigned":
              case "tinyint(1)":
                copyLine.add("_ins.setInt(" + fieldIdWrite.get() + ", rs.getInt(" + fieldIdRead.get() + "))");
                break;
              case "bigint":
              case "bigint unsigned":
                copyLine.add("_ins.setLong(" + fieldIdWrite.get() + ", rs.getLong(" + fieldIdRead.get() + "))");
                break;
              case "varchar":
              case "text":
              case "longtext":
                copyLine.add("_ins.setString(" + fieldIdWrite.get() + ", rs.getString(" + fieldIdRead.get() + "))");
                break;
              case "datetime":
                copyLine.add("_ins.setDate(" + fieldIdWrite.get() + ", rs.getDate(" + fieldIdRead.get() + "))");
                break;
              default:
                copyLine.add("// NO IDEA:" + rs.getString(1) + "::" + rs.getString(2));
            }
          }
        }
        // 1 field
        // 2 type
      }, sql);
      String sqlWalk = "\"SELECT " + String.join(", ", fieldsRead) + " FROM `\" + from.databaseName + \"`.`" + tableName + "`" + (hasId.get() ? " ORDER BY `id`" : "") + "\"";
      String sqlInsert = "\"INSERT INTO `\" + to.databaseName + \"`.`" + tableName + "` (" +  String.join(", ", fieldsInsert) + ") VALUES ("+String.join(", ", questionMarks)+")\"";

      String tab = "      ";
      if (index) {
        java.append(tab + "HashMap<Integer, Integer> _index_" + tableName + " = new HashMap<>();\n");
      }
      java.append(tab + "{\n");
      java.append(tab + "  status.table(\"" + tableName + "\");\n");
      java.append(tab + "  String _walk = " + sqlWalk + ";\n");
      java.append(tab + "  String _insert = " + sqlInsert + ";\n");
      java.append(tab + "  DataBase.walk(_from, (rs) -> {\n");
      java.append(tab + "    try (PreparedStatement _ins = _to.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS)) {\n");
      for (String ln : copyLine) {
        java.append(tab + "      " + ln + ";\n");
      }
      java.append(tab + "      _ins.execute();\n");
      // statement.execute();
      //          return DataBase.getInsertId(statement);
      if (index) {
        java.append(tab + "      _index_" + tableName + ".put(rs.getInt(" + fieldId.get() + "), DataBase.getInsertId(_ins));\n");
        //
      }
      java.append(tab + "    }\n");
      java.append(tab + "  }, _walk);\n");
      java.append(tab + "}\n");

      return null;
    });
  }
  public static void main(String[] args) throws Exception {
    String path = "data-mysql/src/main/java/org/adamalang/mysql/Migrate.java";
    DataBaseConfig config = new DataBaseConfig(new ConfigObject(Json.parseJsonObject(Files.readString(new File("data-mysql/test.mysql.json").toPath()))));
    DataBase dataBase = new DataBase(config, new DataBaseMetrics(new NoOpMetricsFactory()));
    Installer installer = new Installer(dataBase);
    try {
      installer.install();
      StringBuilder java = new StringBuilder();
      java.append(DefaultCopyright.COPYRIGHT_FILE_PREFIX);
      java.append("package org.adamalang.mysql;\n\n");
      java.append("import org.adamalang.mysql.contracts.MigrationStatus;\n\n");
      java.append("import java.sql.Connection;\n");
      java.append("import java.sql.PreparedStatement;\n");
      java.append("import java.sql.Statement;\n");
      java.append("import java.util.HashMap;\n\n");
      java.append("public class Migrate {\n");
      java.append("  public static void copy(DataBase from, DataBase to, MigrationStatus status) throws Exception {\n");
      java.append("    try (Connection _from = from.pool.getConnection()) {\n");
      java.append("      try (Connection _to = to.pool.getConnection()) {\n");
      makeCopy(dataBase, "directory", java, false, lookups());
      makeCopy(dataBase, "emails", java, true, lookups());
      makeCopy(dataBase, "initiations", java, false, lookups("user", "emails"));
      makeCopy(dataBase, "email_keys", java, false, lookups("user", "emails"));
      makeCopy(dataBase, "spaces", java, true, lookups("owner", "emails"));
      makeCopy(dataBase, "grants", java, false, lookups("user", "emails", "space", "spaces"));
      makeCopy(dataBase, "authorities", java, false, lookups("owner", "emails"));
      makeCopy(dataBase, "secrets", java, true, lookups());
      makeCopy(dataBase, "domains", java, false, lookups("owner", "emails"));
      java.append("      }\n");
      java.append("    }\n");
      java.append("  }\n");
      java.append("}\n");
      Files.writeString(new File(path).toPath(), java);
    } finally {
      installer.uninstall();
      dataBase.close();
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.contracts.SQLConsumer;
import org.adamalang.mysql.data.DocumentIndex;
import org.adamalang.mysql.data.GCTask;
import org.adamalang.runtime.data.LocationType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/** operations on the directory */
public class FinderOperations {

  public static HashMap<String, Long> inventoryStorage(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      HashMap<String, Long> bytes = new HashMap<>();
      SQLConsumer add = (rs) -> {
        String space = rs.getString(1);
        long controlStorage = rs.getLong(2);
        Long prior = bytes.get(space);
        if (prior == null) {
          bytes.put(space, controlStorage);
        } else {
          bytes.put(space, prior + controlStorage);
        }
      };
      String sqlData = "SELECT `space`, SUM(delta_bytes) + SUM(asset_bytes) as `bytes` FROM `" + dataBase.databaseName + "`.`directory` WHERE `space` != 'ide' GROUP BY `space`";
      DataBase.walk(connection, add, sqlData);
      String sqlStaticAssets = "SELECT `key`,SUM(delta_bytes) + SUM(asset_bytes) as `bytes` FROM `" + dataBase.databaseName + "`.`directory` WHERE `space` = 'ide' GROUP BY `key`";
      DataBase.walk(connection, add, sqlStaticAssets);
      String sqlControl = "SELECT `name`, IF(`plan` IS NULL, 0, LENGTH(`plan`)) + IF(`rxhtml` IS NULL, 0, LENGTH(`rxhtml`)) as `bytes` FROM `" + dataBase.databaseName + "`.`spaces`;";
      DataBase.walk(connection, add, sqlControl);
      return bytes;
    });
  }

  public static boolean exists(DataBase dataBase, long id) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `space`, `key` FROM `" + dataBase.databaseName + "`.`directory` WHERE `id`=" + id;
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            return true;
          }
          return false;
        }
      }
    });
  }

  public static ArrayList<DocumentIndex> listAll(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `space`, `key`, `created`, `updated`, `head_seq`, `archive` FROM `" + dataBase.databaseName + "`.`directory`";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<DocumentIndex> keys = new ArrayList<>();
          while (rs.next()) {
            keys.add(new DocumentIndex(rs.getString(1), rs.getString(2), rs.getDate(3).toString(), rs.getDate(4).toString(), rs.getInt(5), rs.getString(6)));
          }
          return keys;
        }
      }
    });
  }

  public static ArrayList<DocumentIndex> list(DataBase dataBase, String space, String marker, int limit) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `key`, `created`, `updated`, `head_seq` FROM `" + dataBase.databaseName + //
          "`.`directory` WHERE `space`=? AND `key`>? LIMIT " + Math.max(Math.min(limit, 1000), 1);
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        statement.setString(2, marker == null ? "" : marker);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<DocumentIndex> keys = new ArrayList<>();
          while (rs.next()) {
            keys.add(new DocumentIndex(space, rs.getString(1), rs.getDate(2).toString(), rs.getDate(3).toString(), rs.getInt(4), null));
          }
          return keys;
        }
      }
    });
  }

  /** walk the database producing a list of documents that need to be GC'd */
  public static ArrayList<GCTask> produceGCTasks(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `id`, `space`, `key`, `head_seq`, `archive` FROM `" + dataBase.databaseName + //
          "`.`directory` WHERE `need_gc`=TRUE AND `type`=" + LocationType.Archive.type + " LIMIT 100";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<GCTask> tasks = new ArrayList<>();
          while (rs.next()) {
            tasks.add(new GCTask(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5)));
          }
          return tasks;
        }
      }
    });
  }

  public static boolean validateTask(DataBase dataBase, GCTask task) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `id`, `space`, `key`, `head_seq` FROM `" + dataBase.databaseName + //
          "`.`directory` WHERE `id`=" + task.id + " AND `head_seq`=" + task.seq + " AND `need_gc`=TRUE AND `type`=" + LocationType.Archive.type;
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            return true;
          }
          return false;
        }
      }
    });
  }

  public static boolean lowerTask(DataBase dataBase, GCTask task) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "UPDATE `" + dataBase.databaseName + //
          "`.`directory` SET `need_gc`=FALSE WHERE `id`=" + task.id + " AND `head_seq`=" + task.seq + " AND `need_gc`=TRUE AND `type`=" + LocationType.Archive.type;
      return DataBase.executeUpdate(connection, sql) == 1;
    });
  }
}

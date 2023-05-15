/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.model;

import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.DocumentIndex;
import org.adamalang.mysql.data.GCTask;
import org.adamalang.runtime.data.FinderService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

/** operations on the directory */
public class FinderOperations {

  public static HashMap<String, Long> inventoryStorage(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      HashMap<String, Long> bytes = new HashMap<>();
      String sql = "SELECT `space`, SUM(delta_bytes), SUM(asset_bytes) FROM `" + dataBase.databaseName + //
          "`.`directory` GROUP BY `space`";
      DataBase.walk(connection, (rs) -> {
        bytes.put(rs.getString(1), rs.getLong(2) + rs.getLong(3));
      }, sql);
      return bytes;
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
          "`.`directory` WHERE `need_gc`=TRUE AND `type`=" + FinderService.Location.Archive.type +
          " LIMIT 100";
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
          "`.`directory` WHERE `id`="+task.id+" AND `head_seq`=" + task.seq + " AND `need_gc`=TRUE AND `type`=" + FinderService.Location.Archive.type;
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
          "`.`directory` SET `need_gc`=FALSE WHERE `id`="+task.id+" AND `head_seq`=" + task.seq + " AND `need_gc`=TRUE AND `type`=" + FinderService.Location.Archive.type;
      return DataBase.executeUpdate(connection, sql) == 1;
    });
  }
}

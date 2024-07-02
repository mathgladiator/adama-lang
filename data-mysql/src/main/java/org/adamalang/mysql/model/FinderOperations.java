/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.mysql.model;

import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.DocumentIndex;
import org.adamalang.mysql.data.GCTask;
import org.adamalang.runtime.data.LocationType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/** operations on the directory */
public class FinderOperations {

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
      String sql = "SELECT `space`, `key`, `created`, `updated`, `head_seq`, `archive`, `last_backup` FROM `" + dataBase.databaseName + "`.`directory`";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<DocumentIndex> keys = new ArrayList<>();
          while (rs.next()) {
            keys.add(new DocumentIndex(rs.getString(1), rs.getString(2), rs.getDate(3).toString(), rs.getDate(4).toString(), rs.getInt(5), rs.getString(6), rs.getString(7)));
          }
          return keys;
        }
      }
    });
  }

  public static ArrayList<DocumentIndex> list(DataBase dataBase, String space, String marker, int limit) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `key`, `created`, `updated`, `head_seq`, `last_backup` FROM `" + dataBase.databaseName + //
          "`.`directory` WHERE `space`=? AND `key`>? LIMIT " + Math.max(Math.min(limit, 1000), 1);
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        statement.setString(2, marker == null ? "" : marker);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<DocumentIndex> keys = new ArrayList<>();
          while (rs.next()) {
            keys.add(new DocumentIndex(space, rs.getString(1), rs.getDate(2).toString(), rs.getDate(3).toString(), rs.getInt(4), null, rs.getString(5)));
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

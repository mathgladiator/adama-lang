/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Metrics {

  public static void putOrUpdateDocumentMetrics(DataBase dataBase, String space, String key, String metrics) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sqlUpdate = "UPDATE `" + dataBase.databaseName + "`.`metrics` SET `metrics`=? WHERE `space`=? AND `key`=?";
      try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
        statement.setString(1, metrics);
        statement.setString(2, space);
        statement.setString(3, key);
        if (statement.executeUpdate() == 1) {
          return null;
        }
      }
      String sqlInsert = "INSERT INTO `" + dataBase.databaseName + "`.`metrics` (`space`, `key`, `metrics`) VALUES (?,?, ?)";
      try (PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, space);
        statement.setString(2, key);
        statement.setString(3, metrics);
        statement.execute();
      }
      return null;
    });
  }

  public static List<String> downloadMetrics(DataBase dataBase, String space, String prefix) throws Exception {
    return dataBase.transactSimple((connection) -> {
      ArrayList<String> metrics = new ArrayList<>();
      String sqlUpdate = "SELECT `metrics` FROM `" + dataBase.databaseName + "`.`metrics` WHERE `space`=? AND LEFT(`key`," + prefix.length() + ")=?";
      try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
        statement.setString(1, space);
        statement.setString(2, prefix);
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            metrics.add(rs.getString(1));
          }
        }
      }
      return metrics;
    });
  }
}

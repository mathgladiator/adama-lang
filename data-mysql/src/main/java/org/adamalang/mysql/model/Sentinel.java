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

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Sentinel {

  public static boolean ping(DataBase dataBase, String aspect, long timestimp) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sqlUpdate = "UPDATE `" + dataBase.databaseName + "`.`sentinel` SET `timestamp`=? WHERE `aspect`=?";
      try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
        statement.setLong(1, timestimp);
        statement.setString(2, aspect);
        if (statement.executeUpdate() == 1) {
          return true;
        }
      }

      String sqlInsert = "INSERT INTO `" + dataBase.databaseName + "`.`sentinel` (`aspect`, `timestamp`) VALUES (?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
        statement.setString(1, aspect);
        statement.setLong(2, timestimp);
        statement.execute();
        return true;
      }
    });
  }

  public static long get(DataBase dataBase, String aspect) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `timestamp` FROM `" + dataBase.databaseName + "`.`sentinel` WHERE `aspect`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, aspect);
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            return rs.getLong(1);
          }
        }
      }
      throw new ErrorCodeException(ErrorCodes.MYSQL_FAILED_FINDING_SENTINEL_ASPECT);
    });
  }

  public static int countBehind(DataBase dataBase, long limit) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT COUNT(`timestamp`) FROM `" + dataBase.databaseName + "`.`sentinel` WHERE `timestamp`<?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setLong(1, limit);
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            return rs.getInt(1);
          }
        }
      }
      throw new ErrorCodeException(ErrorCodes.MYSQL_FAILED_FINDING_SENTINEL_COUNT);
    });
  }
}

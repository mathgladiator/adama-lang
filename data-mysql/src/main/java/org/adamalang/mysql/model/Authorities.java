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

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

public class Authorities {

  public static int createAuthority(DataBase dataBase, int ownerId, String authority) throws Exception {
    return dataBase.transactSimple((connection) -> {
      try {
        String sql = "INSERT INTO `" + dataBase.databaseName + "`.`authorities` (`owner`, `authority`, `keystore`) VALUES (?,?,'{}')";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          statement.setInt(1, ownerId);
          statement.setString(2, authority);
          statement.execute();
          return DataBase.getInsertId(statement);
        }
      } catch (SQLIntegrityConstraintViolationException notUnique) {
        throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_ALREADY_EXISTS);
      }
    });
  }

  public static void setKeystore(DataBase dataBase, int ownerId, String authority, String keystore) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "UPDATE `" + dataBase.databaseName + "`.`authorities` SET `keystore`=? WHERE `owner`=? AND authority=?";
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, keystore);
        statement.setInt(2, ownerId);
        statement.setString(3, authority);
        if (statement.executeUpdate() != 1) {
          throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_SET_NOT_FOUND_OR_WRONG_OWNER);
        }
      }
      return null;
    });
  }

  public static ArrayList<String> list(DataBase dataBase, int ownerId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      ArrayList<String> results = new ArrayList<>();
      String sql = "SELECT `authority` FROM `" + dataBase.databaseName + "`.`authorities` WHERE owner=" + ownerId + " ORDER BY `authority` ASC";
      DataBase.walk(connection, (rs) -> {
        results.add(rs.getString(1));
      }, sql);
      return results;
    });
  }

  public static void changeOwner(DataBase dataBase, String authority, int oldOwnerId, int newOwnerId) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "UPDATE `" + dataBase.databaseName + "`.`authorities` SET `owner`=? WHERE `owner`=? AND authority=?";
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setInt(1, newOwnerId);
        statement.setInt(2, oldOwnerId);
        statement.setString(3, authority);
        if (statement.executeUpdate() != 1) {
          throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_CHANGE_OWNER_NOT_FOUND_OR_INCORRECT);
        }
      }
      return null;
    });
  }

  public static String getKeystoreInternal(DataBase dataBase, String authority) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `keystore` FROM `" + dataBase.databaseName + "`.`authorities` WHERE authority=?";
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, authority);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          return rs.getString(1);
        } else {
          throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_GET_NOT_FOUND_INTERNAL);
        }
      }
    });
  }

  public static String getKeystorePublic(DataBase dataBase, int owner, String authority) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `keystore` FROM `" + dataBase.databaseName + "`.`authorities` WHERE authority=? AND `owner`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, authority);
        statement.setInt(2, owner);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          return rs.getString(1);
        } else {
          throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_GET_NOT_FOUND_PUBLIC);
        }
      }
    });
  }

  public static void deleteAuthority(DataBase dataBase, int ownerId, String authority) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "DELETE FROM `" + dataBase.databaseName + "`.`authorities` WHERE `owner`=? AND authority=?";
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setInt(1, ownerId);
        statement.setString(2, authority);
        if (statement.executeUpdate() != 1) {
          throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_DELETE_NOT_FOUND_OR_INCORRECT);
        }
      }
      return null;
    });
  }
}

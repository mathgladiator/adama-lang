/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.frontend;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;

import java.sql.*;
import java.util.ArrayList;

public class Authorities {

  public static int createAuthority(DataBase dataBase, int ownerId, String authority)
      throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql =
          new StringBuilder()
              .append("INSERT INTO `")
              .append(dataBase.databaseName)
              .append("`.`authorities` (`owner`, `authority`, `keystore`) VALUES (?,?,'{}')")
              .toString();
      try (PreparedStatement statement =
          connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setInt(1, ownerId);
        statement.setString(2, authority);
        statement.execute();
        return DataBase.getInsertId(statement);
      }
    } catch (SQLIntegrityConstraintViolationException notUnique) {
      throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_ALREADY_EXISTS);
    }
  }

  public static void setKeystore(DataBase dataBase, int ownerId, String authority, String keystore)
      throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql =
          new StringBuilder()
              .append("UPDATE `")
              .append(dataBase.databaseName)
              .append("`.`authorities` SET `keystore`=? WHERE `owner`=? AND authority=?")
              .toString();
      try (PreparedStatement statement =
          connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, keystore);
        statement.setInt(2, ownerId);
        statement.setString(3, authority);
        if (statement.executeUpdate() != 1) {
          throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_SET_NOT_FOUNDOR_WRONG_OWNER);
        }
      }
    }
  }

  public static ArrayList<String> list(DataBase dataBase, int ownerId) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      ArrayList<String> results = new ArrayList<>();
      String sql =
          new StringBuilder()
              .append("SELECT `authority` FROM `")
              .append(dataBase.databaseName)
              .append("`.`authorities` WHERE owner=")
              .append(ownerId)
              .append(" ORDER BY `authority` ASC")
              .toString();
      DataBase.walk(
          connection,
          (rs) -> {
            results.add(rs.getString(1));
          },
          sql);
      return results;
    }
  }

  public static void changeOwner(
      DataBase dataBase, String authority, int oldOwnerId, int newOwnerId) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql =
          new StringBuilder()
              .append("UPDATE `")
              .append(dataBase.databaseName)
              .append("`.`authorities` SET `owner`=? WHERE `owner`=? AND authority=?")
              .toString();
      try (PreparedStatement statement =
          connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setInt(1, newOwnerId);
        statement.setInt(2, oldOwnerId);
        statement.setString(3, authority);
        if (statement.executeUpdate() != 1) {
          throw new ErrorCodeException(
              ErrorCodes.FRONTEND_AUTHORITY_CHANGE_OWNER_NOT_FOUND_OR_INCORRECT);
        }
      }
    }
  }

  public static String getKeystoreInternal(DataBase dataBase, String authority) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql =
          new StringBuilder()
              .append("SELECT `keystore` FROM `")
              .append(dataBase.databaseName)
              .append("`.`authorities` WHERE authority=?")
              .toString();
      try (PreparedStatement statement =
               connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, authority);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          return rs.getString(1);
        } else {
          throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_GET_NOT_FOUND_INTERNAL);
        }
      }
    }
  }

  public static String getKeystorePublic(DataBase dataBase, int owner, String authority) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql =
          new StringBuilder()
              .append("SELECT `keystore` FROM `")
              .append(dataBase.databaseName)
              .append("`.`authorities` WHERE authority=? AND `owner`=?")
              .toString();
      try (PreparedStatement statement =
               connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, authority);
        statement.setInt(2, owner);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          return rs.getString(1);
        } else {
          throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_GET_NOT_FOUND_PUBLIC);
        }
      }
    }
  }



  public static void deleteAuthority(DataBase dataBase, int ownerId, String authority)
      throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql =
          new StringBuilder()
              .append("DELETE FROM `")
              .append(dataBase.databaseName)
              .append("`.`authorities` WHERE `owner`=? AND authority=?")
              .toString();
      try (PreparedStatement statement =
          connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setInt(1, ownerId);
        statement.setString(2, authority);
        if (statement.executeUpdate() != 1) {
          throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_DELETE_NOT_FOUND_OR_INCORRECT);
        }
      }
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.model;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.IdHashPairing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Users {
  public static int getOrCreateUserId(DataBase dataBase, String email) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      {
        String sql = new StringBuilder("SELECT `id` FROM `").append(dataBase.databaseName).append("`.`emails` WHERE email=?").toString();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, email);
          try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
              return rs.getInt(1);
            }
          }
        }
      }

      {
        String sql = new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`emails` (`email`, `balance`, `password`, `validations`) VALUES (?, 500, '', 0)").toString();
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          statement.setString(1, email);
          statement.execute();
          return DataBase.getInsertId(statement);
        }
      }
    }
  }

  public static int countUsers(DataBase dataBase) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder("SELECT COUNT(`id`) FROM `").append(dataBase.databaseName).append("`.`emails`").toString();
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return rs.getInt(1);
          }
        }
      }
      throw new ErrorCodeException(ErrorCodes.USER_FAILED_TO_COUNT);
    }
  }

  public static void setPasswordHash(DataBase dataBase, int userId, String pwHash) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`emails` SET `password` = ? WHERE `id`=").append(userId).toString();
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, pwHash);
        statement.execute();
      }
    }
  }

  public static String getPasswordHash(DataBase dataBase, int userId) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      {
        String sql = new StringBuilder("SELECT `password` FROM `").append(dataBase.databaseName).append("`.`emails` WHERE `id`=").append(userId).toString();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
              return rs.getString(1);
            } else {
              throw new ErrorCodeException(ErrorCodes.USER_NOT_FOUND_GET_PASSWORD);
            }
          }
        }
      }
    }
  }

  public static int getBalance(DataBase dataBase, int userId) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      {
        String sql = new StringBuilder("SELECT `balance` FROM `").append(dataBase.databaseName).append("`.`emails` WHERE `id`=").append(userId).toString();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
              return rs.getInt(1);
            } else {
              throw new ErrorCodeException(ErrorCodes.USER_NOT_FOUND_GET_BALANCE);
            }
          }
        }
      }
    }
  }

  public static void addToBalance(DataBase dataBase, int userId, int balanceToAdd) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sqlAddToBalance = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`emails` SET `balance` = `balance` + ").append(balanceToAdd).append(" WHERE `id`=").append(userId).toString();
      DataBase.execute(connection, sqlAddToBalance);
      String sqlReEnableSpaces = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`spaces` SET `enabled`=TRUE WHERE `owner`=").append(userId).toString();
      DataBase.execute(connection, sqlReEnableSpaces);
    }
  }

  public static void disableSweep(DataBase dataBase) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sqlFindErrantCustomers = new StringBuilder().append("SELECT `id` FROM `").append(dataBase.databaseName).append("`.`emails` WHERE `balance` < `credit_carry_limit`").toString();
      DataBase.walk(connection, (rs) -> {
        String sqlReEnableSpaces = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`spaces` SET `enabled`=FALSE WHERE `owner`=").append(rs.getInt(1)).toString();
        DataBase.execute(connection, sqlReEnableSpaces);
      }, sqlFindErrantCustomers);
    }
  }

  public static void validateUser(DataBase dataBase, int userId) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`emails` SET `validations` = `validations` + 1, `last_validated`=? WHERE `id`=").append(userId).toString();
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, DataBase.dateTimeOf(System.currentTimeMillis()));
        statement.execute();
      }
    }
  }

  public static List<String> listKeys(DataBase dataBase, int userId) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      ArrayList<String> keys = new ArrayList<>();
      String sql = new StringBuilder().append("SELECT `public_key` FROM `").append(dataBase.databaseName).append("`.`email_keys` WHERE `user`=").append(userId).toString();
      DataBase.walk(connection, (rs) -> {
        keys.add(rs.getString(1));
      }, sql);
      return keys;
    }
  }

  public static void addKey(DataBase dataBase, int userId, String publicKey, long expires) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`email_keys` (`user`,`public_key`,`expires`) VALUES (?,?,?)").toString();
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, userId);
        statement.setString(2, publicKey);
        statement.setString(3, DataBase.dateTimeOf(expires));
        statement.execute();
        return;
      }
    }
  }

  public static int expireKeys(DataBase dataBase, long now) throws Exception {
    int keysDeleted = 0;
    try (Connection connection = dataBase.pool.getConnection()) {
      {
        String sql = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`email_keys` WHERE `expires` < ?").toString();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, DataBase.dateTimeOf(now));
          keysDeleted += statement.executeUpdate();
        }
      }
      {
        String sql = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`initiations` WHERE `expires` < ?").toString();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, DataBase.dateTimeOf(now));
          keysDeleted += statement.executeUpdate();
        }
      }
    }
    return keysDeleted;
  }

  public static int removeAllKeys(DataBase dataBase, int userId) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`email_keys` WHERE `user`=?").toString();
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, userId);
        return statement.executeUpdate();
      }
    }
  }

  public static void addInitiationPair(DataBase dataBase, int userId, String hash, long expires) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`initiations` (`user`,`hash`,`expires`) VALUES (?,?,?)").toString();
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, userId);
        statement.setString(2, hash);
        statement.setString(3, DataBase.dateTimeOf(expires));
        statement.execute();
        return;
      }
    }
  }

  public static List<IdHashPairing> listInitiationPairs(DataBase dataBase, int userId) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      ArrayList<IdHashPairing> pairs = new ArrayList<>();
      String sql = new StringBuilder().append("SELECT `id`,`hash` FROM `").append(dataBase.databaseName).append("`.`initiations` WHERE `user`=").append(userId).toString();
      DataBase.walk(connection, (rs) -> {
        pairs.add(new IdHashPairing(rs.getInt(1), rs.getString(2)));
      }, sql);
      return pairs;
    }
  }

  public static void deleteInitiationPairing(DataBase dataBase, int pairId) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`initiations` WHERE `id`=").append(pairId).toString();
      DataBase.execute(connection, sql);
    }
  }
}

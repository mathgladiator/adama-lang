/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
        String sql = "SELECT `id` FROM `" + dataBase.databaseName + "`.`emails` WHERE email=?";
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
        String sql = "INSERT INTO `" + dataBase.databaseName + "`.`emails` (`email`, `balance`, `password`, `validations`, `payment_info_json`) VALUES (?, 500, '', 0, '{}')";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          statement.setString(1, email);
          statement.execute();
          return DataBase.getInsertId(statement);
        }
      }
    }
  }

  public static String getPaymentInfo(DataBase dataBase, int userId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `payment_info_json` FROM `" + dataBase.databaseName + "`.`emails` WHERE `id`=" + userId;
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            String result = rs.getString(1);
            if (null == result || "".equals(result)) {
              return "{}";
            }
            return result;
          }
        }
      }
      return null;
    });
  }

  public static boolean setPaymentInfo(DataBase dataBase, int userId, String paymentInfo) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "UPDATE `" + dataBase.databaseName + "`.`emails` SET `payment_info_json`=? WHERE `id`=" + userId;
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, paymentInfo);
        return statement.executeUpdate() == 1;
      }
    });
  }

  public static int countUsers(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT COUNT(`id`) FROM `" + dataBase.databaseName + "`.`emails`";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return rs.getInt(1);
          }
        }
      }
      throw new ErrorCodeException(ErrorCodes.USER_FAILED_TO_COUNT);
    });
  }

  public static void setPasswordHash(DataBase dataBase, int userId, String pwHash) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "UPDATE `" + dataBase.databaseName + "`.`emails` SET `password` = ? WHERE `id`=" + userId;
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, pwHash);
        statement.execute();
      }
      return null;
    });
  }

  public static String getPasswordHash(DataBase dataBase, int userId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      {
        String sql = "SELECT `password` FROM `" + dataBase.databaseName + "`.`emails` WHERE `id`=" + userId;
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
    });
  }

  public static String getProfile(DataBase dataBase, int userId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      {
        String sql = "SELECT `profile` FROM `" + dataBase.databaseName + "`.`emails` WHERE `id`=" + userId;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
              String result = rs.getString(1);
              if (result == null) {
                return "{}";
              }
              return result;
            } else {
              throw new ErrorCodeException(ErrorCodes.USER_NOT_FOUND_GET_PROFILE);
            }
          }
        }
      }
    });
  }

  public static void setProfileIf(DataBase dataBase, int userId, String profile, String oldProfile) throws Exception {
    dataBase.transactSimple((connection) -> {
      {
        String sql = "UPDATE `" + dataBase.databaseName + "`.`emails` SET `profile` = ? WHERE `id`=" + userId + " AND (`profile` IS NULL OR `profile`=?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          statement.setString(1, profile);
          statement.setString(2, oldProfile);
          if (statement.executeUpdate() != 1) {
            throw new ErrorCodeException(ErrorCodes.USER_FAILED_TO_SET_PROFILE);
          }
        }
      }
      return null;
    });
  }

  public static int getBalance(DataBase dataBase, int userId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      {
        String sql = "SELECT `balance` FROM `" + dataBase.databaseName + "`.`emails` WHERE `id`=" + userId;
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
    });
  }

  public static void addToBalance(DataBase dataBase, int userId, int balanceToAdd) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sqlAddToBalance = "UPDATE `" + dataBase.databaseName + "`.`emails` SET `balance` = `balance` + " + balanceToAdd + " WHERE `id`=" + userId;
      DataBase.execute(connection, sqlAddToBalance);
      String sqlReEnableSpaces = "UPDATE `" + dataBase.databaseName + "`.`spaces` SET `enabled`=TRUE WHERE `owner`=" + userId;
      DataBase.execute(connection, sqlReEnableSpaces);
      return null;
    });
  }

  public static void disableSweep(DataBase dataBase) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sqlFindErrantCustomers = "SELECT `id` FROM `" + dataBase.databaseName + "`.`emails` WHERE `balance` < `credit_carry_limit`";
      DataBase.walk(connection, (rs) -> {
        String sqlReEnableSpaces = "UPDATE `" + dataBase.databaseName + "`.`spaces` SET `enabled`=FALSE WHERE `owner`=" + rs.getInt(1);
        DataBase.execute(connection, sqlReEnableSpaces);
      }, sqlFindErrantCustomers);
      return null;
    });
  }

  public static void validateUser(DataBase dataBase, int userId) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "UPDATE `" + dataBase.databaseName + "`.`emails` SET `validations` = `validations` + 1, `last_validated`=? WHERE `id`=" + userId;
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, DataBase.dateTimeOf(System.currentTimeMillis()));
        statement.execute();
      }
      return null;
    });
  }

  public static List<String> listKeys(DataBase dataBase, int userId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      ArrayList<String> keys = new ArrayList<>();
      String sql = "SELECT `public_key` FROM `" + dataBase.databaseName + "`.`email_keys` WHERE `user`=" + userId;
      DataBase.walk(connection, (rs) -> {
        keys.add(rs.getString(1));
      }, sql);
      return keys;
    });
  }

  public static void addKey(DataBase dataBase, int userId, String publicKey, long expires) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "INSERT INTO `" + dataBase.databaseName + "`.`email_keys` (`user`,`public_key`,`expires`) VALUES (?,?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, userId);
        statement.setString(2, publicKey);
        statement.setString(3, DataBase.dateTimeOf(expires));
        statement.execute();
      }
      return null;
    });
  }

  public static int expireKeys(DataBase dataBase, long now) throws Exception {
    return dataBase.transactSimple((connection) -> {
      int keysDeleted = 0;
      {
        String sql = "DELETE FROM `" + dataBase.databaseName + "`.`email_keys` WHERE `expires` < ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, DataBase.dateTimeOf(now));
          keysDeleted += statement.executeUpdate();
        }
      }
      {
        String sql = "DELETE FROM `" + dataBase.databaseName + "`.`initiations` WHERE `expires` < ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, DataBase.dateTimeOf(now));
          keysDeleted += statement.executeUpdate();
        }
      }
      return keysDeleted;
    });
  }

  public static int removeAllKeys(DataBase dataBase, int userId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "DELETE FROM `" + dataBase.databaseName + "`.`email_keys` WHERE `user`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, userId);
        return statement.executeUpdate();
      }
    });
  }

  public static void addInitiationPair(DataBase dataBase, int userId, String hash, long expires) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "INSERT INTO `" + dataBase.databaseName + "`.`initiations` (`user`,`hash`,`expires`) VALUES (?,?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, userId);
        statement.setString(2, hash);
        statement.setString(3, DataBase.dateTimeOf(expires));
        statement.execute();
      }
      return null;
    });
  }

  public static List<IdHashPairing> listInitiationPairs(DataBase dataBase, int userId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      ArrayList<IdHashPairing> pairs = new ArrayList<>();
      String sql = "SELECT `id`,`hash` FROM `" + dataBase.databaseName + "`.`initiations` WHERE `user`=" + userId;
      DataBase.walk(connection, (rs) -> {
        pairs.add(new IdHashPairing(rs.getInt(1), rs.getString(2)));
      }, sql);
      return pairs;
    });
  }

  public static void deleteInitiationPairing(DataBase dataBase, int pairId) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "DELETE FROM `" + dataBase.databaseName + "`.`initiations` WHERE `id`=" + pairId;
      DataBase.execute(connection, sql);
      return null;
    });
  }

}

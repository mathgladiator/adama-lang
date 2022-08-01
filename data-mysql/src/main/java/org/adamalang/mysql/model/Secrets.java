/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.model;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Secrets {
  /** insert a secret key */
  public static int insertSecretKey(DataBase dataBase, String space, String privateKeyEncrypted) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = "INSERT INTO `" + dataBase.databaseName + "`.`secrets` (`space`, `encrypted_private_key`) VALUES (?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, space);
        statement.setString(2, privateKeyEncrypted);
        statement.execute();
        return DataBase.getInsertId(statement);
      }
    }
  }

  /** get the private key by an id */
  public static String getPrivateKey(DataBase dataBase, String space, int keyId) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = "SELECT `encrypted_private_key` FROM `" + dataBase.databaseName + "`.`secrets` WHERE `id`=? AND `space`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, keyId);
        statement.setString(2, space);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return rs.getString(1);
          }
        }
      }
    }
    throw new ErrorCodeException(ErrorCodes.MYSQL_FAILED_FINDING_SECRET_KEY);
  }
}

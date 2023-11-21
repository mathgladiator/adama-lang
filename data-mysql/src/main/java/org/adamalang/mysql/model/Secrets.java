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
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.common.keys.SigningKeyPair;
import org.adamalang.mysql.DataBase;

import java.security.PublicKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.TreeMap;

public class Secrets {
  /** insert a secret key */
  public static int insertSecretKey(DataBase dataBase, String space, String privateKeyEncrypted) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "INSERT INTO `" + dataBase.databaseName + "`.`secrets` (`space`, `encrypted_private_key`) VALUES (?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, space);
        statement.setString(2, privateKeyEncrypted);
        statement.execute();
        return DataBase.getInsertId(statement);
      }
    });
  }

  /** get the private key by an id */
  @Deprecated
  public static String getPrivateKey(DataBase dataBase, String space, int keyId) throws Exception {
    return dataBase.transactSimple((connection) -> {
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
      throw new ErrorCodeException(ErrorCodes.MYSQL_FAILED_FINDING_SECRET_KEY);
    });
  }

  public static TreeMap<Integer, PrivateKeyBundle> getKeys(DataBase dataBase, String masterKey, String space) throws Exception {
    return dataBase.transactSimple((connection) -> {
      TreeMap<Integer, PrivateKeyBundle> keys = new TreeMap<>();
      String sql = "SELECT `id`, `encrypted_private_key` FROM `" + dataBase.databaseName + "`.`secrets` WHERE `space`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            keys.put(rs.getInt(1), PrivateKeyBundle.fromDisk(rs.getString(2), masterKey));
          }
        }
      }
      return keys;
    });
  }
}

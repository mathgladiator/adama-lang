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
import org.adamalang.common.keys.VAPIDFactory;
import org.adamalang.common.keys.VAPIDPublicPrivateKeyPair;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.sys.domains.Domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

public class Domains {

  public static VAPIDPublicPrivateKeyPair getOrCreateVapidKeyPair(DataBase dataBase, String domain, VAPIDFactory factory) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sqlGet = "SELECT `public_key`, `private_key`  FROM `" + dataBase.databaseName + "`.`vapid` WHERE `domain`=?";
      try (PreparedStatement statement = connection.prepareStatement(sqlGet)) {
        statement.setString(1, domain);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return new VAPIDPublicPrivateKeyPair(rs.getString(1), rs.getString(2));
          }
        }
      }
      VAPIDPublicPrivateKeyPair created = factory.generateKeyPair();
      String sql = "INSERT INTO `" + dataBase.databaseName + "`.`vapid` (`domain`, `public_key`, `private_key`) VALUES (?,?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, domain);
        statement.setString(2, created.publicKeyBase64);
        statement.setString(3, created.privateKeyBase64);
        statement.execute();
      } catch (SQLIntegrityConstraintViolationException sicve) {
      }

      try (PreparedStatement statement = connection.prepareStatement(sqlGet)) {
        statement.setString(1, domain);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return new VAPIDPublicPrivateKeyPair(rs.getString(1), rs.getString(2));
          }
        }
      }
    }
    throw new ErrorCodeException(ErrorCodes.VAPID_NOT_FOUND_FOR_DOMAIN);
  }

  public static boolean map(DataBase dataBase, int owner, String domain, String space, String key, boolean route, String certificate) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = "INSERT INTO `" + dataBase.databaseName + "`.`domains` (`owner`, `space`, `key`, `route`, `domain`, `certificate`, `automatic`, `automatic_timestamp`) VALUES (?,?,?,?,?,?,?,0)";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, owner);
        statement.setString(2, space);
        statement.setString(3, key);
        statement.setBoolean(4, route);
        statement.setString(5, domain);
        statement.setString(6, certificate != null ? certificate : "");
        statement.setBoolean(7, certificate == null);
        statement.execute();
        return true;
      } catch (SQLIntegrityConstraintViolationException sicve) {
        if (certificate != null) {
          String sqlUpdate = "UPDATE `" + dataBase.databaseName + "`.`domains` SET `space`=?, `key`=?, `route`=?, `certificate`=?, `automatic`=FALSE, `automatic_timestamp`=0 WHERE `owner`=? AND `domain`=?";
          try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
            statement.setString(1, space);
            statement.setString(2, key);
            statement.setBoolean(3, route);
            statement.setString(4, certificate != null ? certificate : "");
            statement.setInt(5, owner);
            statement.setString(6, domain);
            return statement.executeUpdate() == 1;
          }
        } else {
          String sqlUpdate = "UPDATE `" + dataBase.databaseName + "`.`domains` SET `space`=?, `key`=?, `route`=?, `automatic`=TRUE WHERE `owner`=? AND `domain`=?";
          try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
            statement.setString(1, space);
            statement.setString(2, key);
            statement.setBoolean(3, route);
            statement.setInt(4, owner);
            statement.setString(5, domain);
            return statement.executeUpdate() == 1;
          }
        }
      }
    }
  }

  public static boolean unmap(DataBase dataBase, int owner, String domain) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = "DELETE FROM `" + dataBase.databaseName + "`.`domains` WHERE `owner`=? AND `domain`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, owner);
        statement.setString(2, domain);
        return statement.executeUpdate() == 1;
      }
    }
  }

  public static int deleteSpace(DataBase dataBase, String space) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = "DELETE FROM `" + dataBase.databaseName + "`.`domains` WHERE `space`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        return statement.executeUpdate();
      }
    }
  }

  public static Domain get(DataBase dataBase, String domain) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = "SELECT `domain`, `owner`, `space`, `key`, `route`, `certificate`,`updated`, `automatic_timestamp`  FROM `" + dataBase.databaseName + "`.`domains` WHERE `domain`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, domain);
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            return domainOf(rs);
          }
        }
      }
    }
    return null;
  }

  private static Domain domainOf(ResultSet rs) throws Exception {
    String cert = rs.getString(6);
    if (cert.equals("")) {
      cert = null;
    }
    return new Domain(
        rs.getString(1), //
        rs.getInt(2), //
        rs.getString(3), //
        rs.getString(4), //
        rs.getBoolean(5),
        cert, //
        rs.getDate(7), //
        rs.getLong(8));
  }

  public static ArrayList<Domain> list(DataBase dataBase, int owner) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `domain`, `owner`, `space`, `key`, `route`, `certificate`,`updated`, `automatic_timestamp` FROM `" + dataBase.databaseName + "`.`domains` WHERE `owner` =?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, owner);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<Domain> domains = new ArrayList<>();
          while (rs.next()) {
            domains.add(domainOf(rs));
          }
          return domains;
        }
      }
    });
  }

  public static ArrayList<Domain> listBySpace(DataBase dataBase, String space) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `domain`, `owner`, `space`, `key`, `route`, `certificate`,`updated`, `automatic_timestamp` FROM `" + dataBase.databaseName + "`.`domains` WHERE `space` =?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<Domain> domains = new ArrayList<>();
          while (rs.next()) {
            domains.add(domainOf(rs));
          }
          return domains;
        }
      }
    });
  }

  public static boolean superSetAutoCert(DataBase dataBase, String domain, String certificate, long timestampNext) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sqlUpdate = "UPDATE `" + dataBase.databaseName + "`.`domains` SET `certificate`=?, `automatic`=TRUE, `automatic_timestamp`=? WHERE `domain`=? AND `automatic`";
      try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
        statement.setString(1, certificate);
        statement.setLong(2, timestampNext);
        statement.setString(3, domain);
        return statement.executeUpdate() == 1;
      }
    });
  }

  public static ArrayList<Domain> superListAutoDomains(DataBase dataBase, long timestamp) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `domain`, `owner`, `space`, `key`, `route`, `certificate`,`updated`, `automatic_timestamp` FROM `" + dataBase.databaseName + "`.`domains` WHERE `automatic` AND `automatic_timestamp`<?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setLong(1, timestamp);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<Domain> domains = new ArrayList<>();
          while (rs.next()) {
            domains.add(domainOf(rs));
          }
          return domains;
        }
      }
    });
  }
}

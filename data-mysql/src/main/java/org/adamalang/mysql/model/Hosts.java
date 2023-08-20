/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.mysql.DataBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Hosts {
  /** initialize the public key for a web host; this host has a private key that it will use to sign keys */
  public static int initializeHost(DataBase dataBase, String region, String machine, String role, String publicKey) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sqlInsert = "INSERT INTO `" + dataBase.databaseName + "`.`hosts` (`region`, `machine`, `role`, `public_key`) VALUES (?,?,?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, region);
        statement.setString(2, machine);
        statement.setString(3, role);
        statement.setString(4, publicKey);
        statement.execute();
        return DataBase.getInsertId(statement);
      }
    });
  }

  public static List<String> listHosts(DataBase dataBase, String region, String role) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT DISTINCT `machine` FROM `" + dataBase.databaseName + "`.`hosts` WHERE `region`=? AND `role`=? ORDER BY `machine`";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, region);
        statement.setString(2, role);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<String> hosts = new ArrayList<>();
          while (rs.next()) {
            hosts.add(rs.getString(1));
          }
          return hosts;
        }
      }
    });
  }

  /** get the public key for a machine within a region */
  public static String getHostPublicKey(DataBase dataBase, int keyId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `public_key` FROM `" + dataBase.databaseName + "`.`hosts` WHERE `id`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, keyId);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return rs.getString(1);
          }
        }
      }
      return null;
    });
  }

  public static String pickStableHostFromRegion(DataBase dataBase, String region, String role, String keyToHashWithMachine) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `machine`, md5(concat(`machine`,?)) as rdz FROM (SELECT DISTINCT `machine` FROM `" + dataBase.databaseName + "`.`hosts` WHERE `region`=? AND `role`=?) AS `D` ORDER BY rdz ASC LIMIT 1;";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, keyToHashWithMachine);
        statement.setString(2, region);
        statement.setString(3, role);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return rs.getString(1);
          }
          return null;
        }
      }
    });
  }

  public static String pickNewHostForSpace(DataBase dataBase, String region, String role, String space) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `machine`, md5(concat(`machine`,?)) as rdz FROM (SELECT DISTINCT `machine` FROM `" + dataBase.databaseName + "`.`hosts` WHERE `region`=? AND `role`=? AND (NOT EXISTS (SELECT true FROM `" + dataBase.databaseName + "`.`capacity` WHERE `region`=`hosts`.`region` AND `machine`=`hosts`.`machine` AND `space`=?)) ) AS `D` ORDER BY rdz ASC LIMIT 1;";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        statement.setString(2, region);
        statement.setString(3, role);
        statement.setString(4, space);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return rs.getString(1);
          }
          return null;
        }
      }
    });
  }
}

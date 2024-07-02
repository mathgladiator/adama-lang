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

  public static void decomissionHost(DataBase dataBase, String region, String machine) throws Exception {
    dataBase.transactSimple((connection) -> {
      try {
        String deleteFromCapacity = "DELETE FROM `" + dataBase.databaseName + "`.`capacity` WHERE `region`=? AND `machine`=?";
        try (PreparedStatement statement = connection.prepareStatement(deleteFromCapacity)) {
          statement.setString(1, region);
          statement.setString(2, machine);
          statement.execute();
        }
      } finally {
        String deleteFromHosts = "DELETE FROM `" + dataBase.databaseName + "`.`hosts` WHERE `region`=? AND `machine`=?";
        try (PreparedStatement statement = connection.prepareStatement(deleteFromHosts)) {
          statement.setString(1, region);
          statement.setString(2, machine);
          statement.execute();
        }
      }
      return true;
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

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

import org.adamalang.mysql.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Hosts {
  /** initialize the public key for a web host; this host has a private key that it will use to sign keys */
  public static void initializeHost(DataBase dataBase, String region, String machine, String role, String publicKey) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sqlDelete = "DELETE FROM `" + dataBase.databaseName + "`.`hosts` WHERE `region`=? AND `machine`=? AND `role`=?";
      try (PreparedStatement statement = connection.prepareStatement(sqlDelete)) {
        statement.setString(1, region);
        statement.setString(2, machine);
        statement.setString(3, role);
        statement.execute();
      }
      String sqlInsert = "INSERT INTO `" + dataBase.databaseName + "`.`hosts` (`region`, `machine`, `role`, `public_key`) VALUES (?,?,?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
        statement.setString(1, region);
        statement.setString(2, machine);
        statement.setString(3, role);
        statement.setString(4, publicKey);
        statement.execute();
      }
    }
  }

  /** get the public key for a machine within a region */
  public static String getHostPublicKey(DataBase dataBase, String region, String machine, String role) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = "SELECT `public_key` FROM `" + dataBase.databaseName + "`.`hosts` WHERE `region`=? AND `machine`=? AND `role`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, region);
        statement.setString(2, machine);
        statement.setString(3, role);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return rs.getString(1);
          }
        }
      }
    }
    return null;
  }
}

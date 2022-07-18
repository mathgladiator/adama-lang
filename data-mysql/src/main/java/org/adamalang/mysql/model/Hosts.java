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

import org.adamalang.mysql.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Hosts {
  /** initialize the public key for a web host; this host has a private key that it will use to sign keys */
  public static void initializeWebHost(DataBase dataBase, String region, String machine, String publicKey) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sqlDelete = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`web_hosts` WHERE `region`=? AND `machine`=?").toString();
      try (PreparedStatement statement = connection.prepareStatement(sqlDelete)) {
        statement.setString(1, region);
        statement.setString(2, machine);
        statement.execute();
      }
      String sqlInsert = new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`web_hosts` (`region`, `machine`, `public_key`) VALUES (?,?,?)").toString();
      try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
        statement.setString(1, region);
        statement.setString(2, machine);
        statement.setString(3, publicKey);
        statement.execute();
      }
    }
  }

  /** get the public key for a machine within a region */
  public static String getWebHostPublicKey(DataBase dataBase, String region, String machine) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("SELECT `public_key` FROM `").append(dataBase.databaseName).append("`.`web_hosts` WHERE `region`=? AND `machine`=?").toString();
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, region);
        statement.setString(2, machine);
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

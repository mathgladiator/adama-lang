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
import org.adamalang.mysql.data.Domain;

import java.sql.*;
import java.util.ArrayList;

public class Domains {
  public static boolean map(DataBase dataBase, int owner, String domain, String space, String certificate) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = "INSERT INTO `" + dataBase.databaseName + "`.`domains` (`owner`, `space`, `domain`, `certificate`,`automatic`) VALUES (?,?,?,?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, owner);
        statement.setString(2, space);
        statement.setString(3, domain);
        statement.setString(4, certificate != null ? certificate : "{}");
        statement.setBoolean(5, certificate == null);
        statement.execute();
        return true;
      } catch (SQLIntegrityConstraintViolationException sicve) {
        if (certificate != null) {
          String sqlUpdate = "UPDATE `" + dataBase.databaseName + "`.`domains` SET `space`=?, `certificate`=?, `automatic`=FALSE WHERE `owner`=? AND `domain`=?";
          try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
            statement.setString(1, space);
            statement.setString(2, certificate != null ? certificate : "{}");
            statement.setInt(3, owner);
            statement.setString(4, domain);
            return statement.executeUpdate() == 1;
          }
        } else {
          String sqlUpdate = "UPDATE `" + dataBase.databaseName + "`.`domains` SET `space`=? WHERE `owner`=? AND `domain`=?";
          try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
            statement.setString(1, space);
            statement.setInt(2, owner);
            statement.setString(3, domain);
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
      String sql = "SELECT `owner`, `space`, `certificate`,`updated` FROM `" + dataBase.databaseName + "`.`domains` WHERE `domain`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, domain);
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            return new Domain(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4));
          }
        }
      }
    }
    return null;
  }

  public static boolean superSetAutoCert(DataBase dataBase, String domain, String certificate) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sqlUpdate = "UPDATE `" + dataBase.databaseName + "`.`domains` SET `certificate`=?, `automatic`=TRUE WHERE `domain`=? AND `automatic`";
      try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
        statement.setString(1, certificate);
        statement.setString(2, domain);
        return statement.executeUpdate() == 1;
      }
    }
  }

  public static ArrayList<Domain> superListAutoDomains(DataBase dataBase) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = "SELECT `owner`, `space`, `certificate`,`updated` FROM `" + dataBase.databaseName + "`.`domains` WHERE `automatic`";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<Domain> domains = new ArrayList<>();
          while (rs.next()) {
            domains.add(new Domain(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4)));
          }
          return domains;
        }
      }
    }
  }
}

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
import org.adamalang.runtime.ops.CapacityInstance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Capacity {

  // Add the given machine to the capacity table
  public static Integer add(DataBase dataBase, String space, String region, String machine) throws Exception {
    return dataBase.transactSimple((connection) -> {
      try {
        String sql = "INSERT INTO `" + dataBase.databaseName + "`.`capacity` (`space`, `region`, `machine`) VALUES (?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          statement.setString(1, space);
          statement.setString(2, region);
          statement.setString(3, machine);
          statement.execute();
          return DataBase.getInsertId(statement);
        }
      } catch (SQLIntegrityConstraintViolationException sicve) {
        dataBase.metrics.capacity_duplicate.run();
        return null;
      }
    });
  }

  public static void removeAll(DataBase dataBase, String space) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "DELETE FROM `" + dataBase.databaseName + "`.`capacity` WHERE `space`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        statement.execute();
      }
      return null;
    });
  }

  public static void setOverride(DataBase dataBase, int id, boolean value) throws Exception {
    dataBase.transactSimple((connection) -> {
      DataBase.execute(connection, "UPDATE `" + dataBase.databaseName + "`.`capacity` SET `override`=" + (value ? "TRUE" : "FALSE") + " WHERE `id`=" + id);
      return null;
    });
  }

  // list all the capacity for the given space
  public static List<CapacityInstance> listAll(DataBase dataBase, String space) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `id`, `region`, `machine`, `override` FROM `" + dataBase.databaseName + "`.`capacity` WHERE `space`=? ORDER BY `region`, `machine`";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<CapacityInstance> listing = new ArrayList<>();
          while (rs.next()) {
            listing.add(new CapacityInstance(space, rs.getString(2), rs.getString(3), rs.getBoolean(4)));
          }
          return listing;
        }
      }
    });
  }

  // list all the capacity for the given space
  public static List<CapacityInstance> listAllOnMachine(DataBase dataBase, String region, String machine) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `id`, `space`, `override` FROM `" + dataBase.databaseName + "`.`capacity` WHERE `region`=? AND `machine`=? ORDER BY `region`, `machine`";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, region);
        statement.setString(2, machine);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<CapacityInstance> listing = new ArrayList<>();
          while (rs.next()) {
            listing.add(new CapacityInstance(rs.getString(2), region, machine, rs.getBoolean(3)));
          }
          return listing;
        }
      }
    });
  }

  // list all the capacity for the given space within the given region
  public static List<CapacityInstance> listRegion(DataBase dataBase, String space, String region) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `id`, `machine`, `override` FROM `" + dataBase.databaseName + "`.`capacity` WHERE `space`=? AND `region`=? ORDER BY `region`, `machine`";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        statement.setString(2, region);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<CapacityInstance> listing = new ArrayList<>();
          while (rs.next()) {
            listing.add(new CapacityInstance(space, region, rs.getString(2), rs.getBoolean(3)));
          }
          return listing;
        }
      }
    });
  }

  /** remote the given capacity from the given space */
  public static void remove(DataBase dataBase, String space, String region, String machine) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "DELETE FROM `" + dataBase.databaseName + "`.`capacity` WHERE `space`=? AND `region`=? AND `machine`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        statement.setString(2, region);
        statement.setString(3, machine);
        statement.execute();
      }
      return null;
    });
  }
}

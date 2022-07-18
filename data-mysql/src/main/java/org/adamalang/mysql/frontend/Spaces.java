/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.frontend;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.InternalDeploymentPlan;
import org.adamalang.mysql.data.Role;
import org.adamalang.mysql.data.SpaceInfo;
import org.adamalang.mysql.data.SpaceListingItem;

import java.sql.*;
import java.util.*;

public class Spaces {
  public static HashMap<String, Long> collectUnbilledStorage(DataBase dataBase) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      HashMap<String, Long> byteHours = new HashMap<>();
      String sql = new StringBuilder("SELECT `name`, `unbilled_storage_bytes_hours`  FROM `").append(dataBase.databaseName).append("`.`spaces` WHERE `unbilled_storage_bytes_hours` > 0").toString();
      DataBase.walk(connection, (rs) -> {
        byteHours.put(rs.getString(1), rs.getLong(2));
      }, sql);
      return byteHours;
    }
  }

  public static Integer getLatestBillingHourCode(DataBase dataBase) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sqlTestWater = new StringBuilder().append("SELECT `latest_billing_hour` FROM `").append(dataBase.databaseName).append("`.`spaces` ORDER BY `latest_billing_hour` DESC LIMIT 1").toString();
      try (PreparedStatement statement = connection.prepareStatement(sqlTestWater)) {
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          return rs.getInt(1);
        } else {
          return null;
        }
      }
    }
  }

  public static int createSpace(DataBase dataBase, int userId, String space) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sqlTestWater = new StringBuilder().append("SELECT `owner`, `id` FROM `").append(dataBase.databaseName).append("`.`spaces` WHERE `name`=?").toString();
      try (PreparedStatement statement = connection.prepareStatement(sqlTestWater)) {
        statement.setString(1, space);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          if (rs.getInt(1) == userId) {
            return rs.getInt(2);
          }
        }
      }
      String sql = new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`spaces` (`owner`, `name`, `plan`, `hash`) VALUES (?,?,'{}', '')").toString();
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setInt(1, userId);
        statement.setString(2, space);
        statement.execute();
        return DataBase.getInsertId(statement);
      }
    } catch (SQLIntegrityConstraintViolationException notUnique) {
      throw new ErrorCodeException(ErrorCodes.FRONTEND_SPACE_ALREADY_EXISTS);
    }
  }

  public static SpaceInfo getSpaceInfo(DataBase dataBase, String space) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder("SELECT `id`,`owner`,`enabled`,`storage_bytes` FROM `").append(dataBase.databaseName).append("`.`spaces` WHERE name=?").toString();
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            Set<Integer> developers = new HashSet<>();
            int owner = rs.getInt(2);
            developers.add(owner);
            String sqlGrants = new StringBuilder("SELECT `user`,`role` FROM `").append(dataBase.databaseName).append("`.`grants` WHERE `space`=").append(rs.getInt(1)).toString();
            DataBase.walk(connection, (g) -> {
              switch (g.getInt(2)) {
                case 0x01: // Role.Developer
                  developers.add(g.getInt(1));
                  break;
              }
            }, sqlGrants);
            return new SpaceInfo(rs.getInt(1), owner, developers, rs.getBoolean(3), rs.getLong(2));
          }
          throw new ErrorCodeException(ErrorCodes.FRONTEND_SPACE_DOESNT_EXIST);
        }
      }
    }
  }

  public static void setPlan(DataBase dataBase, int spaceId, String plan, String hash) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`spaces` SET `plan`=?, `hash`=? WHERE `id`=").append(spaceId).append(" LIMIT 1").toString();
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, plan);
        statement.setString(2, hash);
        statement.execute();
      }
    }
  }

  public static String getPlan(DataBase dataBase, int spaceId) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder("SELECT `plan` FROM `").append(dataBase.databaseName).append("`.`spaces` WHERE id=").append(spaceId).toString();
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return rs.getString(1);
          }
          throw new ErrorCodeException(ErrorCodes.FRONTEND_PLAN_DOESNT_EXIST);
        }
      }
    }
  }

  public static InternalDeploymentPlan getPlanByNameForInternalDeployment(DataBase dataBase, String spaceName) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder("SELECT `plan`, `hash` FROM `").append(dataBase.databaseName).append("`.`spaces` WHERE `name`=?").toString();
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, spaceName);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return new InternalDeploymentPlan(rs.getString(1), rs.getString(2));
          }
          throw new ErrorCodeException(ErrorCodes.FRONTEND_INTERNAL_PLAN_DOESNT_EXIST);
        }
      }
    }
  }

  public static List<SpaceListingItem> list(DataBase dataBase, int userId, String marker, int limit) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      // select * from a LEFT OUTER JOIN b on a.a = b.b;
      String sql = new StringBuilder("SELECT `s`.`name`,`s`.`owner`,`s`.`created`,`s`.`enabled`,`s`.`storage_bytes` FROM `").append(dataBase.databaseName) //
          .append("`.`spaces` as `s` LEFT OUTER JOIN `").append(dataBase.databaseName).append("`.`grants` as `g` ON `s`.`id` = `g`.`space`") //
          .append(" WHERE (`s`.owner=").append(userId).append(" OR `g`.`user`=").append(userId).append(") AND `s`.`name`>? ORDER BY `s`.`name` ASC LIMIT ").append(limit).toString();
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, marker == null ? "" : marker);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<SpaceListingItem> names = new ArrayList<>();
          while (rs.next()) {
            boolean isOwner = rs.getInt(2) == userId;
            names.add(new SpaceListingItem(rs.getString(1), isOwner ? "owner" : "developer", rs.getDate(3).toString(), rs.getBoolean(4), rs.getLong(5)));
          }
          return names;
        }
      }
    }
  }

  public static ArrayList<String> listAllSpaceNames(DataBase dataBase) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder("SELECT `name` FROM `").append(dataBase.databaseName) //
          .append("`.`spaces` ORDER BY `id` ASC").toString();
      ArrayList<String> results = new ArrayList<>();
      DataBase.walk(connection, (rs) -> {
        results.add(rs.getString(1));
      }, sql);
      return results;
    }
  }

  public static boolean changePrimaryOwner(DataBase dataBase, int spaceId, int oldOwner, int newOwner) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`spaces` SET `owner`=").append(newOwner).append(" WHERE `id`=").append(spaceId).append(" AND `owner`=").append(oldOwner).append(" LIMIT 1").toString();
      return DataBase.executeUpdate(connection, sql) > 0;
    }
  }

  public static boolean delete(DataBase dataBase, int spaceId, int owner) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`spaces` WHERE `id`=").append(spaceId).append(" AND `owner`=").append(owner).toString();
      return DataBase.executeUpdate(connection, sql) > 0;
    }
  }

  public static void setRole(DataBase dataBase, int spaceId, int userId, Role role) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      {
        DataBase.execute(connection, new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`grants` WHERE `space`=").append(spaceId).append(" AND `user`=").append(userId).toString());
      }
      if (role != Role.None) {
        DataBase.execute(connection, new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`grants` (`space`, `user`, `role`) VALUES (").append(spaceId).append(",").append(userId).append(",").append(role.role).append(")").toString());
      }
    }
  }

}

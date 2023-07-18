/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.*;

public class Spaces {
  public static HashMap<String, UnbilledResources> collectUnbilledResources(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      HashMap<String, UnbilledResources> byteHours = new HashMap<>();
      String sql = "SELECT `name`, `unbilled_storage_bytes_hours`, `unbilled_bandwidth_hours`, `unbilled_first_party_service_calls`, `unbilled_third_party_service_calls`  FROM `" + dataBase.databaseName + "`.`spaces`";
      DataBase.walk(connection, (rs) -> {
        byteHours.put(rs.getString(1), new UnbilledResources(rs.getLong(2), rs.getLong(3), rs.getLong(4), rs.getLong(5)));
      }, sql);
      return byteHours;
    });
  }

  public static Integer getLatestBillingHourCode(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sqlTestWater = "SELECT `latest_billing_hour` FROM `" + dataBase.databaseName + "`.`spaces` ORDER BY `latest_billing_hour` DESC LIMIT 1";
      try (PreparedStatement statement = connection.prepareStatement(sqlTestWater)) {
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          return rs.getInt(1);
        } else {
          return null;
        }
      }
    });
  }

  public static int createSpace(DataBase dataBase, int userId, String space) throws Exception {
    return dataBase.transactSimple((connection) -> {
      try {
        String sqlTestWater = "SELECT `owner`, `id` FROM `" + dataBase.databaseName + "`.`spaces` WHERE `name`=?";
        try (PreparedStatement statement = connection.prepareStatement(sqlTestWater)) {
          statement.setString(1, space);
          ResultSet rs = statement.executeQuery();
          if (rs.next()) {
            if (rs.getInt(1) == userId) {
              return rs.getInt(2);
            }
            throw new ErrorCodeException(ErrorCodes.FRONTEND_SPACE_ALREADY_EXISTS);
          }
        }
        String sql = "INSERT INTO `" + dataBase.databaseName + "`.`spaces` (`owner`, `name`, `plan`, `hash`) VALUES (?,?,'{}', '')";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          statement.setInt(1, userId);
          statement.setString(2, space);
          statement.execute();
          return DataBase.getInsertId(statement);
        }
      } catch (SQLIntegrityConstraintViolationException notUnique) {
        throw new ErrorCodeException(ErrorCodes.FRONTEND_SPACE_ALREADY_EXISTS);
      }
    });
  }

  public static SpaceInfo getSpaceInfo(DataBase dataBase, String space) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `id`,`owner`,`enabled`,`storage_bytes` FROM `" + dataBase.databaseName + "`.`spaces` WHERE name=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            Set<Integer> developers = new HashSet<>();
            int owner = rs.getInt(2);
            developers.add(owner);
            String sqlGrants = "SELECT `user`,`role` FROM `" + dataBase.databaseName + "`.`grants` WHERE `space`=" + rs.getInt(1);
            DataBase.walk(connection, (g) -> {
              if (g.getInt(2) == 0x01) { // Role.Developer
                developers.add(g.getInt(1));
              }
            }, sqlGrants);
            return new SpaceInfo(rs.getInt(1), owner, developers, rs.getBoolean(3), rs.getLong(2));
          }
          throw new ErrorCodeException(ErrorCodes.FRONTEND_SPACE_DOESNT_EXIST);
        }
      }
    });
  }

  public static void setPlan(DataBase dataBase, int spaceId, String plan, String hash) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "UPDATE `" + dataBase.databaseName + "`.`spaces` SET `plan`=?, `hash`=? WHERE `id`=" + spaceId + " LIMIT 1";
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, plan);
        statement.setString(2, hash);
        statement.execute();
      }
      return null;
    });
  }

  public static String getPlan(DataBase dataBase, int spaceId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `plan` FROM `" + dataBase.databaseName + "`.`spaces` WHERE id=" + spaceId;
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return rs.getString(1);
          }
          throw new ErrorCodeException(ErrorCodes.FRONTEND_PLAN_DOESNT_EXIST);
        }
      }
    });
  }

  public static void setRxHtml(DataBase dataBase, int spaceId, String rxhtml) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "UPDATE `" + dataBase.databaseName + "`.`spaces` SET `rxhtml`=? WHERE `id`=" + spaceId + " LIMIT 1";
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, rxhtml);
        statement.execute();
      }
      return null;
    });
  }

  public static String getRxHtml(DataBase dataBase, int spaceId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `rxhtml` FROM `" + dataBase.databaseName + "`.`spaces` WHERE id=" + spaceId;
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            String rxhtml = rs.getString(1);
            if (rxhtml != null) {
              return rxhtml;
            }
          }
          throw new ErrorCodeException(ErrorCodes.FRONTEND_RXHTML_DOESNT_EXIST);
        }
      }
    });
  }

  public static InternalDeploymentPlan getPlanByNameForInternalDeployment(DataBase dataBase, String spaceName) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `plan`, `hash` FROM `" + dataBase.databaseName + "`.`spaces` WHERE `name`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, spaceName);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return new InternalDeploymentPlan(rs.getString(1), rs.getString(2));
          }
          throw new ErrorCodeException(ErrorCodes.FRONTEND_INTERNAL_PLAN_DOESNT_EXIST);
        }
      }
    });
  }

  public static List<SpaceListingItem> list(DataBase dataBase, int userId, String marker, int limit) throws Exception {
    return dataBase.transactSimple((connection) -> {
      // select * from a LEFT OUTER JOIN b on a.a = b.b;
      String sql = "SELECT DISTINCT `s`.`name`,`s`.`owner`,`s`.`created`,`s`.`enabled`,`s`.`storage_bytes` FROM `" + dataBase.databaseName + //
          "`.`spaces` as `s` LEFT OUTER JOIN `" + dataBase.databaseName + "`.`grants` as `g` ON `s`.`id` = `g`.`space`" + //
          " WHERE (`s`.owner=" + userId + " OR `g`.`user`=" + userId + ") AND `s`.`name`>? ORDER BY `s`.`name` ASC LIMIT " + limit;
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
    });
  }

  public static ArrayList<String> listAllSpaceNames(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `name` FROM `" + dataBase.databaseName + //
          "`.`spaces` ORDER BY `id` ASC";
      ArrayList<String> results = new ArrayList<>();
      DataBase.walk(connection, (rs) -> {
        results.add(rs.getString(1));
      }, sql);
      return results;
    });
  }

  public static ArrayList<DeletedSpace> listDeletedSpaces(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `id`,`name` FROM `" + dataBase.databaseName + "`.`spaces` WHERE `owner`=0";
      ArrayList<DeletedSpace> results = new ArrayList<>();
      DataBase.walk(connection, (rs) -> {
        results.add(new DeletedSpace(rs.getInt(1), rs.getString(2)));
      }, sql);
      return results;
    });
  }

  public static boolean changePrimaryOwner(DataBase dataBase, int spaceId, int oldOwner, int newOwner) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "UPDATE `" + dataBase.databaseName + "`.`spaces` SET `owner`=" + newOwner + " WHERE `id`=" + spaceId + " AND `owner`=" + oldOwner + " LIMIT 1";
      return DataBase.executeUpdate(connection, sql) > 0;
    });
  }

  public static boolean delete(DataBase dataBase, int spaceId, int owner) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "DELETE FROM `" + dataBase.databaseName + "`.`spaces` WHERE `id`=" + spaceId + " AND `owner`=" + owner;
      return DataBase.executeUpdate(connection, sql) > 0;
    });
  }

  public static void setRole(DataBase dataBase, int spaceId, int userId, Role role) throws Exception {
    dataBase.transactSimple((connection) -> {
      {
        DataBase.execute(connection, "DELETE FROM `" + dataBase.databaseName + "`.`grants` WHERE `space`=" + spaceId + " AND `user`=" + userId);
      }
      if (role != Role.None) {
        DataBase.execute(connection, "INSERT INTO `" + dataBase.databaseName + "`.`grants` (`space`, `user`, `role`) VALUES (" + spaceId + "," + userId + "," + role.role + ")");
      }
      return null;
    });
  }

  public static List<Developer> listDevelopers(DataBase dataBase, int spaceId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      ArrayList<Developer> developers = new ArrayList<>();
      String sql = "SELECT `email`, `role` FROM `" + dataBase.databaseName + "`.`grants` LEFT JOIN `" + dataBase.databaseName + "`.`emails` ON `emails`.`id` = `grants`.`user` WHERE `space`=" + spaceId;
      DataBase.walk(connection, (rs) -> {
        try {
          developers.add(new Developer(rs.getString(1), Role.from(rs.getInt(2)).toString().toLowerCase(Locale.ENGLISH)));
        } catch (ErrorCodeException ex) {
          throw new RuntimeException(ex);
        }
      }, sql);
      return developers;
    });
  }

}

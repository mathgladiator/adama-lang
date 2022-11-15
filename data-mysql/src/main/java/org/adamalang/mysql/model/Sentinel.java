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

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Sentinel {

  public static boolean ping(DataBase dataBase, String aspect, long timestimp) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sqlUpdate = "UPDATE `" + dataBase.databaseName + "`.`sentinel` SET `timestamp`=? WHERE `aspect`=?";
      try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
        statement.setLong(1, timestimp);
        statement.setString(2, aspect);
        if (statement.executeUpdate() == 1) {
          return true;
        }
      }

      String sqlInsert = "INSERT INTO `" + dataBase.databaseName + "`.`sentinel` (`aspect`, `timestamp`) VALUES (?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
        statement.setString(1, aspect);
        statement.setLong(2, timestimp);
        statement.execute();
        return true;
      }
    });
  }

  public static long get(DataBase dataBase, String aspect) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `timestamp` FROM `" + dataBase.databaseName + "`.`sentinel` WHERE `aspect`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, aspect);
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            return rs.getLong(1);
          }
        }
      }
      throw new ErrorCodeException(ErrorCodes.MYSQL_FAILED_FINDING_SENTINEL_ASPECT);
    });
  }

  public static int countBehind(DataBase dataBase, long limit) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT COUNT(`timestamp`) FROM `" + dataBase.databaseName + "`.`sentinel` WHERE `timestamp`<?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setLong(1, limit);
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            return rs.getInt(1);
          }
        }
      }
      throw new ErrorCodeException(ErrorCodes.MYSQL_FAILED_FINDING_SENTINEL_COUNT);
    });
  }
}

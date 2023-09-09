/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.common.keys.SigningKeyPair;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.data.Key;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Metrics {

  public static void putOrUpdateDocumentMetrics(DataBase dataBase, String space, String key, String metrics) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sqlUpdate = "UPDATE `" + dataBase.databaseName + "`.`metrics` SET `metrics`=? WHERE `space`=? AND `key`=?";
      try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
        statement.setString(1, metrics);
        statement.setString(2, space);
        statement.setString(3, key);
        if (statement.executeUpdate() == 1) {
          return null;
        }
      }
      String sqlInsert = "INSERT INTO `" + dataBase.databaseName + "`.`metrics` (`space`, `key`, `metrics`) VALUES (?,?, ?)";
      try (PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, space);
        statement.setString(2, key);
        statement.setString(3, metrics);
        statement.execute();
      }
      return null;
    });
  }

  public static List<String> downloadMetrics(DataBase dataBase, String space, String prefix) throws Exception {
    return dataBase.transactSimple((connection) -> {
      ArrayList<String> metrics = new ArrayList<>();
      String sqlUpdate = "SELECT `metrics` FROM `" + dataBase.databaseName + "`.`metrics` WHERE `space`=? AND LEFT(`key`," + prefix.length() + ")=?";
      try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
        statement.setString(1, space);
        statement.setString(2, prefix);
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            metrics.add(rs.getString(1));
          }
        }
      }
      return metrics;
    });
  }
}

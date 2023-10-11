/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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

import org.adamalang.common.Hashing;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.DeviceSubscription;
import org.adamalang.runtime.natives.NtPrincipal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PushSubscriptions {

  private static String authorityHash(String authority) {
    MessageDigest digest = Hashing.md5();
    digest.update(authority.getBytes(StandardCharsets.UTF_8));
    return Hashing.finishAndEncode(digest);
  }

  public static void registerSubscription(DataBase dataBase, String domain, NtPrincipal who, String subscription, String deviceInfo, long timestampExpiry) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = "INSERT INTO `" + dataBase.databaseName + "`.`push` (`domain`, `agent`, `authority_hash`, `authority`, `subscription`, `device_info`, `expiry`) VALUES (?,?,?,?,?,?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, domain);
        statement.setString(2, who.agent);
        statement.setString(3, authorityHash(who.authority));
        statement.setString(4, who.authority);
        statement.setString(5, subscription);
        statement.setString(6, deviceInfo);
        statement.setDate(7, new Date(timestampExpiry));
        statement.execute();
      }
    }
  }

  public static void deleteSubscription(DataBase dataBase, int id) throws Exception {
    dataBase.transactSimple((connection) -> {
      String sql = "DELETE FROM `" + dataBase.databaseName + "`.`push` WHERE `id`=" + id;
      DataBase.executeUpdate(connection, sql);
      return null;
    });
  }

  public static List<DeviceSubscription> list(DataBase dataBase, String domain, NtPrincipal who) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sqlGet = "SELECT `id`, subscription`, `device_info`  FROM `" + dataBase.databaseName + "`.`push` WHERE `domain`=? AND `agent`=? AND `authority`=? AND `authority_hash`=? ORDER BY `id`";
      try (PreparedStatement statement = connection.prepareStatement(sqlGet)) {
        statement.setString(1, domain);
        statement.setString(2, who.agent);
        statement.setString(3, who.authority);
        statement.setString(4, authorityHash(who.authority));
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<DeviceSubscription> subscriptions = new ArrayList<>();
          while (rs.next()) {
            subscriptions.add(new DeviceSubscription(rs.getInt(1), rs.getString(2), rs.getString(3)));
          }
          return subscriptions;
        }
      }
    }
  }
}

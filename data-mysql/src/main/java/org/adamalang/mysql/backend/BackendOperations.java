/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.backend;

import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.backend.data.DocumentIndex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/** operations on data */
public class BackendOperations {

  public static ArrayList<DocumentIndex> list(DataBase dataBase, String space, String marker, int limit) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      // select * from a LEFT OUTER JOIN b on a.a = b.b;
      String sql = new StringBuilder("SELECT `key`, `created`, `updated`, `head_seq`, `invalidate` FROM `").append(dataBase.databaseName) //
          .append("`.`index` WHERE `space`=? AND `key`>? LIMIT ").append(Math.max(Math.min(limit, 1000), 1)).toString();
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        statement.setString(2, marker == null ? "" : marker);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<DocumentIndex> keys = new ArrayList<>();
          while (rs.next()) {
            keys.add(new DocumentIndex(rs.getString(1), rs.getDate(2).toString(), rs.getDate(3).toString(), rs.getInt(4), rs.getBoolean(5)));
          }
          return keys;
        }
      }
    }
  }

}

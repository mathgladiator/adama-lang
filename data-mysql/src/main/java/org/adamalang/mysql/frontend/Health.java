package org.adamalang.mysql.frontend;

import org.adamalang.mysql.DataBase;

import java.sql.Connection;
import java.sql.Statement;

public class Health {
  public static boolean pingDataBase(DataBase dataBase) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      try (Statement statement = connection.createStatement()) {
        return statement.execute("SELECT 1");
      }
    }
  }
}

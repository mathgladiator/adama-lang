package org.adamalang.mysql.impl;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.WakeTask;
import org.adamalang.runtime.data.Key;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** fundamental operations */
public class MySQLWakeCore {
  private final SimpleExecutor executor;
  private final DataBase database;
  private final TimeSource time;

  public MySQLWakeCore(SimpleExecutor executor, DataBase database, TimeSource time) {
    this.executor = executor;
    this.database = database;
    this.time = time;
  }

  public void wake(Key key, long when, String region, String machine, Callback<Void> callback) {
    executor.execute(new NamedRunnable("mysqlwake_wake") {
      @Override
      public void execute() throws Exception {
        database.transact((connection) -> {
          String insertSQL = //
              "INSERT INTO `" + database.databaseName + "`.`alarms` (" + //
                  "`space`, `key`, `region`, `machine`, `wake_time`) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `region`=?, `machine`=?, `wake_time`=?" //
              ;
          try (PreparedStatement statementInsertIndex = connection.prepareStatement(insertSQL)) {
            statementInsertIndex.setString(1, key.space);
            statementInsertIndex.setString(2, key.key);
            statementInsertIndex.setString(3, region);
            statementInsertIndex.setString(4, machine);
            statementInsertIndex.setLong(5, time.nowMilliseconds() + when);
            statementInsertIndex.setString(6, region);
            statementInsertIndex.setString(7, machine);
            statementInsertIndex.setLong(8, time.nowMilliseconds() + when);
            statementInsertIndex.execute();
            return null;
          }
        }, database.metrics.wake_schedule.wrap(callback), ErrorCodes.MYSQL_WAKE_EXCEPTION);
      }
    });
  }

  public void list(String region, String machine, Callback<List<WakeTask>> callback) {
    executor.execute(new NamedRunnable("mysqlwake_list") {
      @Override
      public void execute() throws Exception {
        database.transact((connection) -> {
          String selectSQL = //
              "SELECT `id`, `space`, `key`, `wake_time` FROM `" + database.databaseName + //
                  "`.`alarms` WHERE `region`=? AND `machine`=?";
          try (PreparedStatement statementInsertIndex = connection.prepareStatement(selectSQL)) {
            statementInsertIndex.setString(1, region);
            statementInsertIndex.setString(2, machine);
            try (ResultSet rs = statementInsertIndex.executeQuery()) {
              ArrayList<WakeTask> results = new ArrayList<>();
              while (rs.next()) {
                results.add(new WakeTask(rs.getLong(1), new Key(rs.getString(2), rs.getString(3)), rs.getLong(4)));
              }
              return results;
            }
          }
        }, database.metrics.wake_schedule.wrap(callback), ErrorCodes.MYSQL_WAKE_EXCEPTION);
      }
    });
  }

  public void delete(long id, Callback<Void> callback) {
    executor.execute(new NamedRunnable("mysqlwake_delete") {
      @Override
      public void execute() throws Exception {
        database.transact((connection) -> {
          String delete = "DELETE FROM `" + database.databaseName + "`.`alarms` WHERE `id`=?";
          try (PreparedStatement statementDelete = connection.prepareStatement(delete)) {
            statementDelete.setLong(1, id);
            statementDelete.executeUpdate();
          }
          return null;
        }, database.metrics.wake_schedule.wrap(callback), ErrorCodes.MYSQL_WAKE_EXCEPTION);
      }
    });
  }
}


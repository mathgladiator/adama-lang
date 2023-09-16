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
package org.adamalang.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.mysql.contracts.SQLConsumer;
import org.adamalang.mysql.contracts.SQLTransact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;

/** the connection pool and helpers for interacting with MySQL */
public class DataBase implements AutoCloseable {
  private static final Logger LOG = LoggerFactory.getLogger(DataBase.class);
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(LOG);
  public final String databaseName;
  public final HikariDataSource pool;
  public final DataBaseMetrics metrics;

  public DataBase(DataBaseConfig config, DataBaseMetrics metrics) throws Exception {
    this.pool = config.createHikariDataSource();
    this.databaseName = config.databaseName;
    this.metrics = metrics;
  }

  public static String dateTimeOf(long time) {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(time));
  }

  public static boolean execute(Connection connection, String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      return statement.execute(sql);
    }
  }

  public static int executeUpdate(Connection connection, String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      return statement.executeUpdate(sql);
    }
  }

  public static int getInsertId(PreparedStatement statement) throws SQLException {
    try (ResultSet row = statement.getGeneratedKeys()) {
      row.next();
      return row.getInt(1);
    }
  }

  public static int walk(Connection connection, SQLConsumer action, String sql) throws SQLException {
    int count = 0;
    try (Statement statement = connection.createStatement()) {
      try (ResultSet row = statement.executeQuery(sql)) {
        while (row.next()) {
          action.accept(row);
          count++;
        }
        return count;
      }
    }
  }

  @Override
  public void close() throws Exception {
    pool.close();
  }

  public <R> void transact(SQLTransact<R> transaction, Callback<R> callback, int failureReason) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.transaction.start();
    int backoff = (int) (25 + Math.random() * 25);
    while (true) {
      try {
        Connection connection = pool.getConnection();
        boolean commit = false;
        try {
          connection.setAutoCommit(false);
          R result = transaction.execute(connection);
          commit = true;
          connection.commit();
          callback.success(result);
          instance.success();
          return;
        } finally {
          if (!commit) {
            connection.rollback();
          }
          connection.close();
        }
      } catch (Throwable ex) {
        if (ex instanceof ErrorCodeException) {
          callback.failure((ErrorCodeException) ex);
          instance.failure(((ErrorCodeException) ex).code);
          return;
        }
        boolean validException = ex instanceof java.sql.SQLIntegrityConstraintViolationException;
        if (!validException) {
          LOG.error("database-exception", ex);
        } else {
          metrics.valid_exception.run();
        }
        if (backoff < 500 && !validException) {
          try {
            Thread.sleep(backoff);
            backoff += (int) (Math.random() * backoff);
          } catch (InterruptedException ie) {
          }
        } else {
          ErrorCodeException ece = ErrorCodeException.detectOrWrap(failureReason, ex, LOGGER);
          callback.failure(ece);
          if (validException) {
            instance.success();
          } else {
            instance.failure(ece.code);
          }
          return;
        }
      }
    }
  }

  public <R> R transactSimple(SQLTransact<R> transaction) throws Exception {
    int backoff = (int) (25 + Math.random() * 25);
    while (true) {
      RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.transaction_simple.start();
      try {
        Connection connection = pool.getConnection();
        boolean commit = false;
        try {
          connection.setAutoCommit(false);
          R result = transaction.execute(connection);
          commit = true;
          connection.commit();
          instance.success();
          return result;
        } finally {
          if (!commit) {
            connection.rollback();
          }
          connection.close();
        }
      } catch (Throwable ex) {
        if (ex instanceof ErrorCodeException) {
          instance.failure(((ErrorCodeException) ex).code);
          throw ex;
        }
        boolean validException = ex instanceof java.sql.SQLIntegrityConstraintViolationException;
        if (!validException) {
          LOG.error("database-exception", ex);
        } else {
          metrics.valid_exception.run();
          instance.success();
        }
        if (backoff < 500 && !validException) {
          try {
            Thread.sleep(backoff);
            backoff += (int) (Math.random() * backoff);
          } catch (InterruptedException ie) {
          }
        } else {
          throw ex;
        }
      }
    }
  }
}

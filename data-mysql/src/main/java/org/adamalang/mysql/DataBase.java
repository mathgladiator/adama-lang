/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
  private static Logger LOG = LoggerFactory.getLogger(DataBase.class);
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
    int backoff = (int) (25 + Math.random() * 25);
    while(true) {
      RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.transaction.start();
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
          instance.success();
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
          if (!validException) {
            instance.failure(ece.code);
          }
          return;
        }
      }
    }
  }

  public <R> R transactSimple(SQLTransact<R> transaction) throws Exception {
    int backoff = (int) (25 + Math.random() * 25);
    while(true) {
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

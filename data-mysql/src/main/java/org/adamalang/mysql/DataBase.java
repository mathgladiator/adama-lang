/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.adamalang.mysql.contracts.SQLConsumer;
import org.adamalang.mysql.contracts.SQLTransact;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.sql.*;

/** the connection pool and helpers for interacting with MySQL */
public class DataBase implements AutoCloseable {
    public final ComboPooledDataSource pool;
    public final String databaseName;

    public DataBase(BaseConfig config) throws Exception {
        this.pool = config.createComboPooledDataSource();
        this.databaseName = config.databaseName;
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

    @Override
    public void close() throws Exception {
        pool.close();
    }

    public <R> void transact(SQLTransact<R> transaction, Callback<R> callback, int failureReason) {
        try {
            Connection connection = pool.getConnection();
            boolean commit = false;
            try {
                connection.setAutoCommit(false);
                R result = transaction.execute(connection);
                commit = true;
                connection.commit();
                callback.success(result);
            } finally {
                if (!commit) {
                    connection.rollback();
                }
                connection.close();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            callback.failure(ErrorCodeException.detectOrWrap(failureReason, ex));
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
}

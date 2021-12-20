package org.adamalang.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.adamalang.mysql.contracts.SQLConsumer;
import org.adamalang.mysql.contracts.SQLTransact;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.sql.*;

/** the connection pool and helpers for interacting with MySQL */
public class Base {
    public final ComboPooledDataSource pool;
    public final String databaseName;

    public Base(ComboPooledDataSource pool, String databaseName) {
        this.pool = pool;
        this.databaseName = databaseName;
    }

    public static boolean execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.execute(sql);
        }
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

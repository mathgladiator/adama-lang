package org.adamalang.data.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.sql.*;
import java.util.function.Consumer;

public class MySqlBase {
    public final ComboPooledDataSource pool;
    public final String databaseName;

    public MySqlBase(ComboPooledDataSource pool, String databaseName) {
        this.pool = pool;
        this.databaseName = databaseName;
    }

    public static boolean execute(Connection connection, String sql) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            return statement.execute(sql);
        } finally {
            statement.close();
        }
    }

    @FunctionalInterface
    public static interface Transact<R> {
        public R execute(Connection c) throws SQLException, ErrorCodeException;
    }

    public <R> void transact(Transact<R> transaction, Callback<R> callback) {
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
            callback.failure(ErrorCodeException.detectOrWrap(0, ex));
        }
    }

    public static int getInsertId(PreparedStatement statement) throws SQLException, ErrorCodeException {
        ResultSet row = statement.getGeneratedKeys();
        try {
            if (row.next()) {
                return row.getInt(1);
            } else {
                throw new ErrorCodeException(2);
            }
        } finally {
            row.close();
        }
    }

    public static void walk(Connection connection, Consumer<ResultSet> action, String sql) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            ResultSet row = statement.executeQuery(sql);
            try {
                while (row.next()) {
                    action.accept(row);
                }
            } finally {
                row.close();
            }
        } finally {
            statement.close();
        }
    }
}

package org.adamalang.data.mysql.contracts;

import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.sql.Connection;
import java.sql.SQLException;

/** for executing a transaction */
@FunctionalInterface
public interface SQLTransact<R> {
    public R execute(Connection c) throws SQLException, ErrorCodeException;
}

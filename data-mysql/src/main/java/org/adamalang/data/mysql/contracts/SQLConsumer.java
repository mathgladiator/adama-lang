package org.adamalang.data.mysql.contracts;

import java.sql.ResultSet;
import java.sql.SQLException;

/** for walking a result set or executing on a single row */
@FunctionalInterface
public interface SQLConsumer {
    public void accept(ResultSet rs) throws SQLException;
}

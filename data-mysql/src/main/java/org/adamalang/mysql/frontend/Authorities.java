package org.adamalang.mysql.frontend;

import org.adamalang.ErrorCodes;
import org.adamalang.mysql.Base;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public class Authorities {

    public static int createAuthority(Base base, int ownerId, String authority) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder().append("INSERT INTO `").append(base.databaseName).append("`.`authorities` (`owner`, `authority`, `keystore`) VALUES (?,?,'{}')").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, ownerId);
                statement.setString(2, authority);
                statement.execute();
                return Base.getInsertId(statement);
            }
        } catch (SQLIntegrityConstraintViolationException notUnique) {
            throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_ALREADY_EXISTS);
        }
    }
}

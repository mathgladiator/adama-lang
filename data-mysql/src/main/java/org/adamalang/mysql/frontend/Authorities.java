package org.adamalang.mysql.frontend;

import org.adamalang.ErrorCodes;
import org.adamalang.mysql.Base;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.sql.*;
import java.util.ArrayList;

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

    public static void setKeystore(Base base, int ownerId, String authority, String keystore) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder().append("UPDATE `").append(base.databaseName).append("`.`authorities` SET `keystore`=? WHERE `owner`=? AND authority=?").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, keystore);
                statement.setInt(2, ownerId);
                statement.setString(3, authority);
                if (statement.executeUpdate() != 1) {
                    throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_SET_NOT_FOUND);
                }
            }
        }
    }

    public static ArrayList<String> list(Base base, int ownerId) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            ArrayList<String> results = new ArrayList<>();
            String sql = new StringBuilder().append("SELECT `authority` FROM `").append(base.databaseName).append("`.`authorities` WHERE owner=").append(ownerId).append(" ORDER BY `authority` ASC").toString();
            Base.walk(connection, (rs) -> {
                results.add(rs.getString(1));
            }, sql);
            return results;
        }
    }

    public static void changeOwner(Base base, String authority, int oldOwnerId, int newOwnerId) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder().append("UPDATE `").append(base.databaseName).append("`.`authorities` SET `owner`=? WHERE `owner`=? AND authority=?").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, newOwnerId);
                statement.setInt(2, oldOwnerId);
                statement.setString(3, authority);
                if (statement.executeUpdate() != 1) {
                    throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_CHANGE_OWNER_NOT_FOUND_OR_INCORRECT);
                }
            }
        }
    }

    public static String getKeystore(Base base, String authority) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder().append("SELECT `keystore` FROM `").append(base.databaseName).append("`.`authorities` WHERE authority=?").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, authority);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return rs.getString(1);
                } else {
                    throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_GET_NOT_FOUND);
                }
            }
        }
    }

    public static void deleteAuthority(Base base, int ownerId, String authority) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder().append("DELETE FROM `").append(base.databaseName).append("`.`authorities` WHERE `owner`=? AND authority=?").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, ownerId);
                statement.setString(2, authority);
                if (statement.executeUpdate() != 1) {
                    throw new ErrorCodeException(ErrorCodes.FRONTEND_AUTHORITY_DELETE_NOT_FOUND);
                }
            }
        }
    }

}

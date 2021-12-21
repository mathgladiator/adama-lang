package org.adamalang.mysql.frontend;

import org.adamalang.ErrorCodes;
import org.adamalang.mysql.Base;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Spaces {

    public static int createSpace(Base base, int userId, String space) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder().append("INSERT INTO `").append(base.databaseName).append("`.`spaces` (`owner`, `name`, `plan`, `billing`) VALUES (?,?,'{}', 'free')").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, userId);
                statement.setString(2, space);
                statement.execute();
                return Base.getInsertId(statement);
            }
        } catch (SQLIntegrityConstraintViolationException notUnique) {
            throw new ErrorCodeException(ErrorCodes.FRONTEND_SPACE_ALREADY_EXISTS);
        }
    }

    public static int getSpaceId(Base base, String space) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder("SELECT `id` FROM `").append(base.databaseName).append("`.`spaces` WHERE name=?").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, space);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    throw new ErrorCodeException(ErrorCodes.FRONTEND_SPACE_DOESNT_EXIST);
                }
            }
        }
    }

    public static void setPlan(Base base, int spaceId, String plan) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder().append("UPDATE `").append(base.databaseName).append("`.`spaces` SET `plan`=? WHERE `id`=").append(spaceId).append(" LIMIT 1").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, plan);
                statement.execute();
            }
        }
    }

    public static void setBilling(Base base, int spaceId, String billing) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder().append("UPDATE `").append(base.databaseName).append("`.`spaces` SET `billing`=? WHERE `id`=").append(spaceId).append(" LIMIT 1").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, billing);
                statement.execute();
            }
        }
    }

    public static String getPlan(Base base, int spaceId) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder("SELECT `plan` FROM `").append(base.databaseName).append("`.`spaces` WHERE id=").append(spaceId).toString();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(1);
                    }
                    throw new ErrorCodeException(ErrorCodes.FRONTEND_PLAN_DOESNT_EXIST);
                }
            }
        }
    }

    public static class Item {
        public final String name;
        public final String callerRole;
        public final String billing;
        public final String created;

        public Item(String name, String callerRole, String billing, String created) {
            this.name = name;
            this.callerRole = callerRole;
            this.billing = billing;
            this.created = created;
        }
    }

    public static List<Item> list(Base base, int userId, String marker, int limit) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            // select * from a LEFT OUTER JOIN b on a.a = b.b;
            String sql = new StringBuilder("SELECT `s`.`name`,`s`.`owner`,`s`.`billing`,`s`.`created` FROM `").append(base.databaseName) //
                    .append("`.`spaces` as `s` LEFT OUTER JOIN `").append(base.databaseName).append("`.`grants` as `g` ON `s`.`id` = `g`.`space`") //
                    .append(" WHERE (`s`.owner=").append(userId).append(" OR `g`.`user`=").append(userId).append(") AND `s`.`name`>? ORDER BY `s`.`name` ASC LIMIT ").append(limit).toString();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, marker == null ? "" : marker);
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Item> names = new ArrayList<>();
                    while (rs.next()) {
                        names.add(new Item(rs.getString(1), rs.getInt(2) == userId ? "owner" : "developer", rs.getString(3), rs.getDate(4).toString()));
                    }
                    return names;
                }
            }
        }
    }

    public static boolean changePrimaryOwner(Base base, int spaceId, int oldOwner, int newOwner) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder().append("UPDATE `").append(base.databaseName).append("`.`spaces` SET `owner`=").append(newOwner).append(" WHERE `id`=").append(spaceId).append(" AND `owner`=").append(oldOwner).append(" LIMIT 1").toString();
            return Base.executeUpdate(connection, sql) > 0;
        }
    }

    public static void setRole(Base base, int spaceId, int userId, Role role) throws Exception  {
        try (Connection connection = base.pool.getConnection()) {
            {
                Base.execute(connection, new StringBuilder().append("DELETE FROM `").append(base.databaseName).append("`.`grants` WHERE `space`=").append(spaceId).append(" AND `user`=").append(userId).toString());
            }
            if (role != Role.None) {
                Base.execute(connection, new StringBuilder().append("INSERT INTO `").append(base.databaseName).append("`.`grants` (`space`, `user`, `role`) VALUES (").append(spaceId).append(",").append(userId).append(",").append(role.role).append(")").toString());
            }
        }
    }
}

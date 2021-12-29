/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.frontend;

import org.adamalang.ErrorCodes;
import org.adamalang.mysql.DataBase;
import org.adamalang.common.ErrorCodeException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Spaces {

    public static int createSpace(DataBase dataBase, int userId, String space) throws Exception {
        try (Connection connection = dataBase.pool.getConnection()) {
            String sqlTestWater = new StringBuilder().append("SELECT `owner`, `id` FROM `").append(dataBase.databaseName).append("`.`spaces` WHERE `name`=?").toString();
            try (PreparedStatement statement = connection.prepareStatement(sqlTestWater, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, space);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    if (rs.getInt(1) == userId) {
                        return rs.getInt(2);
                    }
                }
            }
            String sql = new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`spaces` (`owner`, `name`, `plan`, `billing`) VALUES (?,?,'{}', 'free')").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, userId);
                statement.setString(2, space);
                statement.execute();
                return DataBase.getInsertId(statement);
            }
        } catch (SQLIntegrityConstraintViolationException notUnique) {
            throw new ErrorCodeException(ErrorCodes.FRONTEND_SPACE_ALREADY_EXISTS);
        }
    }

    public static class Space {
        public final int id;
        public final int owner;
        public final String billing;
        public final Set<Integer> developers;

        public Space(int id, int owner, String billing, Set<Integer> developers) {
            this.id = id;
            this.owner = owner;
            this.billing = billing;
            this.developers = developers;
        }
    }

    public static Space getSpaceId(DataBase dataBase, String space) throws Exception {
        try (Connection connection = dataBase.pool.getConnection()) {
            String sql = new StringBuilder("SELECT `id`,`owner`,`billing` FROM `").append(dataBase.databaseName).append("`.`spaces` WHERE name=?").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, space);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        Set<Integer> developers = new HashSet<>();
                        int owner = rs.getInt(2);
                        developers.add(owner);
                        String sqlGrants = new StringBuilder("SELECT `user`,`role` FROM `").append(dataBase.databaseName).append("`.`grants` WHERE `space`=").append(rs.getInt(1)).toString();
                        DataBase.walk(connection, (g) -> {
                            switch (g.getInt(2)) {
                                case 0x01: // Role.Developer
                                    developers.add(g.getInt(1));
                                    break;
                            }
                        }, sqlGrants);
                        return new Space(rs.getInt(1), owner, rs.getString(3), developers);
                    }
                    throw new ErrorCodeException(ErrorCodes.FRONTEND_SPACE_DOESNT_EXIST);
                }
            }
        }
    }

    public static void setPlan(DataBase dataBase, int spaceId, String plan) throws Exception {
        try (Connection connection = dataBase.pool.getConnection()) {
            String sql = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`spaces` SET `plan`=? WHERE `id`=").append(spaceId).append(" LIMIT 1").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, plan);
                statement.execute();
            }
        }
    }

    public static void setBilling(DataBase dataBase, int spaceId, String billing) throws Exception {
        try (Connection connection = dataBase.pool.getConnection()) {
            String sql = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`spaces` SET `billing`=? WHERE `id`=").append(spaceId).append(" LIMIT 1").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, billing);
                statement.execute();
            }
        }
    }

    public static String getPlan(DataBase dataBase, int spaceId) throws Exception {
        try (Connection connection = dataBase.pool.getConnection()) {
            String sql = new StringBuilder("SELECT `plan` FROM `").append(dataBase.databaseName).append("`.`spaces` WHERE id=").append(spaceId).toString();
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

    public static List<Item> list(DataBase dataBase, int userId, String marker, int limit) throws Exception {
        try (Connection connection = dataBase.pool.getConnection()) {
            // select * from a LEFT OUTER JOIN b on a.a = b.b;
            String sql = new StringBuilder("SELECT `s`.`name`,`s`.`owner`,`s`.`billing`,`s`.`created` FROM `").append(dataBase.databaseName) //
                    .append("`.`spaces` as `s` LEFT OUTER JOIN `").append(dataBase.databaseName).append("`.`grants` as `g` ON `s`.`id` = `g`.`space`") //
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

    public static boolean changePrimaryOwner(DataBase dataBase, int spaceId, int oldOwner, int newOwner) throws Exception {
        try (Connection connection = dataBase.pool.getConnection()) {
            String sql = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`spaces` SET `owner`=").append(newOwner).append(" WHERE `id`=").append(spaceId).append(" AND `owner`=").append(oldOwner).append(" LIMIT 1").toString();
            return DataBase.executeUpdate(connection, sql) > 0;
        }
    }

    public static void setRole(DataBase dataBase, int spaceId, int userId, Role role) throws Exception  {
        try (Connection connection = dataBase.pool.getConnection()) {
            {
                DataBase.execute(connection, new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`grants` WHERE `space`=").append(spaceId).append(" AND `user`=").append(userId).toString());
            }
            if (role != Role.None) {
                DataBase.execute(connection, new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`grants` (`space`, `user`, `role`) VALUES (").append(spaceId).append(",").append(userId).append(",").append(role.role).append(")").toString());
            }
        }
    }
}

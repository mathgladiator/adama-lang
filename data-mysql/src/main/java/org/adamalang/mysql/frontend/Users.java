package org.adamalang.mysql.frontend;

import org.adamalang.mysql.Base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Users {
    public static int getOrCreateUserId(Base base, String email) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            {
                String sql = new StringBuilder("SELECT `id` FROM `").append(base.databaseName).append("`.`emails` WHERE email=?").toString();
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, email);
                    try (ResultSet rs = statement.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    }
                }
            }

            {
                String sql = new StringBuilder().append("INSERT INTO `").append(base.databaseName).append("`.`emails` (`email`) VALUES (?)").toString();
                try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, email);
                    statement.execute();
                    return Base.getInsertId(statement);
                }
            }
        }
    }

    public static List<String> listKeys(Base base, int userId) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            ArrayList<String> keys = new ArrayList<>();
            String sql = new StringBuilder().append("SELECT `public_key` FROM `").append(base.databaseName).append("`.`email_keys` WHERE `user`=").append(userId).toString();
            Base.walk(connection, (rs) -> {
                keys.add(rs.getString(1));
            }, sql);
            return keys;
        }
    }

    public static void addKey(Base base, int userId, String publicKey, Date expires) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder().append("INSERT INTO `").append(base.databaseName).append("`.`email_keys` (`user`,`public_key`,`expires`) VALUES (?,?,?)").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.setString(2, publicKey);
                statement.setDate(3, new java.sql.Date(expires.getTime()));
                statement.execute();
                return;
            }
        }
    }

    public static void removeAllKeys(Base base, int userId) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            String sql = new StringBuilder().append("DELETE FROM `").append(base.databaseName).append("`.`email_keys` WHERE `user`=?").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.execute();
                return;
            }
        }
    }
}

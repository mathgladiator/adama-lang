package org.adamalang.mysql.frontend;

import org.adamalang.mysql.Base;

import java.sql.Connection;
import java.util.List;

public class Spaces {
    public static void createSpace(Base base, int userId, String space) throws Exception {
        try (Connection connection = base.pool.getConnection()) {

        }
    }

    public static int getSpaceId(Base base, String space) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            return 0;
        }
    }

    public static void setPlan(Base base, int spaceId, String plan) throws Exception {
        try (Connection connection = base.pool.getConnection()) {

        }
    }

    public static String getPlan(Base base, int spaceId) throws Exception {
        try (Connection connection = base.pool.getConnection()) {
            return null;
        }
    }

    public static List<String> list(Base base, int userId, String marker, int limit) throws Exception {
        try (Connection connection = base.pool.getConnection()) {

            //
            return null;
        }
    }

    public static void setPrimaryOwner(Base base, int spaceId, int oldOwner, int newOwner) throws Exception {
        try (Connection connection = base.pool.getConnection()) {

        }
    }

    public static void setRole(Base base, int spaceId, int userId, Role role) throws Exception  {
        try (Connection connection = base.pool.getConnection()) {

        }
    }
}

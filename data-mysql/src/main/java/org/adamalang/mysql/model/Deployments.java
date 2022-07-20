/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.model;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.Deployment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.TreeSet;

public class Deployments {
  public static void undeploy(DataBase dataBase, String space, String target) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      { // delete prior versions
        String sql = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`deployed` WHERE `space`=? AND `target`=?").toString();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, space);
          statement.setString(2, target);
          statement.execute();
        }
      }
    }
  }

  public static void undeployAll(DataBase dataBase, String space) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      { // delete prior versions
        String sql = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`deployed` WHERE `space`=?").toString();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, space);
          statement.execute();
        }
      }
    }
  }

  public static void undeployTarget(DataBase dataBase, String target) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      { // delete prior versions
        String sql = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`deployed` WHERE `target`=?").toString();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, target);
          statement.execute();
        }
      }
    }
  }

  public static TreeSet<String> listAllTargets(DataBase dataBase) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("SELECT DISTINCT `target` FROM `").append(dataBase.databaseName).append("`.`deployed`").toString();

      TreeSet<String> targets = new TreeSet<>();
      DataBase.walk(connection, (rs) -> {
        targets.add(rs.getString(1));
      }, sql);
      return targets;
    }
  }

  public static ArrayList<Deployment> listSpacesOnTarget(DataBase dataBase, String target) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("SELECT `space`, `hash`, `plan` FROM `").append(dataBase.databaseName).append("`.`deployed` WHERE `target`=? ORDER BY `space` ASC").toString();

      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, target);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<Deployment> results = new ArrayList<>();
          while (rs.next()) {
            results.add(new Deployment(rs.getString(1), rs.getString(2), rs.getString(3), target));
          }
          return results;
        }
      }
    }
  }

  public static ArrayList<Deployment> listTargetsOnSpace(DataBase dataBase, String space) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("SELECT `hash`, `plan`, `target` FROM `").append(dataBase.databaseName).append("`.`deployed` WHERE `space`=? ORDER BY `target` ASC").toString();

      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        try (ResultSet rs = statement.executeQuery()) {
          ArrayList<Deployment> results = new ArrayList<>();
          while (rs.next()) {
            results.add(new Deployment(space, rs.getString(1), rs.getString(2), rs.getString(3)));
          }
          return results;
        }
      }
    }
  }

  public static Deployment get(DataBase dataBase, String target, String space) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder().append("SELECT `hash`, `plan` FROM `").append(dataBase.databaseName).append("`.`deployed` WHERE `target`=? AND `space`=? LIMIT 1").toString();

      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, target);
        statement.setString(2, space);
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            return new Deployment(space, rs.getString(1), rs.getString(2), target);
          }
          throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_NOT_FOUND);
        }
      }
    }
  }

  public static void deploy(DataBase dataBase, String space, String target, String hash, String plan) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      { // delete prior versions
        String sql = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`deployed` WHERE `space`=? AND `target`=?").toString();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, space);
          statement.setString(2, target);
          statement.execute();
        }
      }
      {
        String sql = new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`deployed` (`space`, `target`, `hash`, `plan`) VALUES (?,?,?,?)").toString();
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          statement.setString(1, space);
          statement.setString(2, target);
          statement.setString(3, hash);
          statement.setString(4, plan);
          statement.execute();
        }
      }
    }
  }

}

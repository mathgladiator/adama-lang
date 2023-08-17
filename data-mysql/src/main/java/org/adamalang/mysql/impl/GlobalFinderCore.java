/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.impl;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.data.BackupResult;
import org.adamalang.runtime.data.DocumentLocation;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.LocationType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GlobalFinderCore {
  private final DataBase database;

  public GlobalFinderCore(DataBase database) {
    this.database = database;
  }

  public void find(Key key, Callback<DocumentLocation> callback) {
    database.transact((connection) -> {
      String selectSQL = //
          "SELECT `id`, `type`, `region`, `machine`, `archive`, `deleted` FROM `" + database.databaseName + //
              "`.`directory` WHERE `space`=? AND `key`=?" //
          ;
      try (PreparedStatement statementInsertIndex = connection.prepareStatement(selectSQL)) {
        statementInsertIndex.setString(1, key.space);
        statementInsertIndex.setString(2, key.key);
        try (ResultSet rs = statementInsertIndex.executeQuery()) {
          if (rs.next()) {
            long id = rs.getLong(1);
            int type = rs.getInt(2);
            String region = rs.getString(3);
            String machineValue = rs.getString(4);
            String archiveValue = rs.getString(5);
            LocationType location = LocationType.fromType(type);
            if (location != null) {
              return new DocumentLocation(id, location, region, machineValue, archiveValue, rs.getBoolean(6));
            }
          }
        }
      }
      throw new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED);
    }, database.metrics.finder_find.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_FIND_EXCEPTION);
  }
  public void bind(Key key, String region, String machine, Callback<Void> callback) {
    database.transact((connection) -> {
      String updateIndexSQL = //
          "UPDATE `" + database.databaseName + "`.`directory` " + //
              "SET `type`=" + LocationType.Machine.type + //
              ", `region`=?" + ", `machine`=?" + " WHERE NOT `deleted` AND `space`=? AND `key`=? AND ((`machine`=? AND `region`=?) OR `type`!=" + LocationType.Machine.type + ")";
      try (PreparedStatement statementUpdate = connection.prepareStatement(updateIndexSQL)) {
        statementUpdate.setString(1, region);
        statementUpdate.setString(2, machine);
        statementUpdate.setString(3, key.space);
        statementUpdate.setString(4, key.key);
        statementUpdate.setString(5, machine);
        statementUpdate.setString(6, region);
        if (statementUpdate.executeUpdate() == 1) {
          return null;
        }
      }
      String insertSQL = //
          "INSERT INTO `" + database.databaseName + "`.`directory` (" + //
              "`space`, `key`, `type`, `head_seq`, `region`, `machine`, `archive`, `delta_bytes`, `asset_bytes`, `need_gc`) VALUES (?, ?, " + LocationType.Machine.type + ", 0, ?, ?, '', 0, 0, FALSE)" //
          ;
      try (PreparedStatement statementInsertIndex = connection.prepareStatement(insertSQL)) {
        statementInsertIndex.setString(1, key.space);
        statementInsertIndex.setString(2, key.key);
        statementInsertIndex.setString(3, region);
        statementInsertIndex.setString(4, machine);
        statementInsertIndex.execute();
        return null;
      }
    }, database.metrics.finder_bind.wrap(callback), ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE);
  }

  public void free(Key key, String region, String machine, Callback<Void> callback) {
    database.transact((connection) -> {
      String freeSQL = //
          "UPDATE `" + database.databaseName + "`.`directory` " + //
              "SET `type`=" + LocationType.Archive.type + //
              ", `region`=''" + ", `machine`=''" + " WHERE `space`=? AND `key`=? AND `machine`=? AND `region`=? AND `type`=" + LocationType.Machine.type;
      try (PreparedStatement statementUpdate = connection.prepareStatement(freeSQL)) {
        statementUpdate.setString(1, key.space);
        statementUpdate.setString(2, key.key);
        statementUpdate.setString(3, machine);
        statementUpdate.setString(4, region);
        statementUpdate.executeUpdate();
      }
      return null;
    }, database.metrics.finder_free.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_FREE_EXCEPTION);
  }

  public void backup(Key key, BackupResult result, String region, String machine, Callback<Void> callback) {
    database.transact((connection) -> {
      String backupSQL = //
          "UPDATE `" + database.databaseName + "`.`directory` " + //
              "SET `archive`=?" + ", `head_seq`=" + result.seq + //
              ", `need_gc` = (`need_gc` OR `asset_bytes` != " + result.assetBytes + ")" + //
              ", `delta_bytes`=" + result.deltaBytes + //
              ", `asset_bytes`=" + result.assetBytes + " WHERE `space`=? AND `key`=? AND `machine`=? AND `region`=? AND `type`=" + LocationType.Machine.type;
      try (PreparedStatement statementUpdate = connection.prepareStatement(backupSQL)) {
        statementUpdate.setString(1, result.archiveKey);
        statementUpdate.setString(2, key.space);
        statementUpdate.setString(3, key.key);
        statementUpdate.setString(4, machine);
        statementUpdate.setString(5, region);
        if (statementUpdate.executeUpdate() == 1) {
          return null;
        }
      }
      throw new ErrorCodeException(ErrorCodes.FINDER_SERVICE_MYSQL_CANT_BACKUP);
    }, database.metrics.finder_backup.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_BACKUP_EXCEPTION);
  }

  public void markDelete(Key key, String region, String machine, Callback<Void> callback) {
    database.transact((connection) -> {
      String backupSQL = //
          "UPDATE `" + database.databaseName + "`.`directory` " + //
              "SET `deleted`=TRUE WHERE `space`=? AND `key`=? AND `machine`=? AND `region`=? AND `type`=" + LocationType.Machine.type;
      try (PreparedStatement statementUpdate = connection.prepareStatement(backupSQL)) {
        statementUpdate.setString(1, key.space);
        statementUpdate.setString(2, key.key);
        statementUpdate.setString(3, machine);
        statementUpdate.setString(4, region);
        if (statementUpdate.executeUpdate() == 1) {
          return null;
        }
      }
      throw new ErrorCodeException(ErrorCodes.FINDER_SERVICE_MYSQL_CANT_MARK_DELETE);
    }, database.metrics.finder_backup.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_MARK_DELETE_EXCEPTION);
  }

  public void commitDelete(Key key, String region, String machine, Callback<Void> callback) {
    database.transact((connection) -> {
      String deleteSQL = //
          "DELETE FROM `" + database.databaseName + "`.`directory` " + //
              " WHERE `space`=? AND `key`=? AND `machine`=? AND `region`=? AND `type`=" + LocationType.Machine.type;
      try (PreparedStatement statementDelete = connection.prepareStatement(deleteSQL)) {
        statementDelete.setString(1, key.space);
        statementDelete.setString(2, key.key);
        statementDelete.setString(3, machine);
        statementDelete.setString(4, region);
        if (statementDelete.executeUpdate() == 1) {
          return null;
        }
      }
      throw new ErrorCodeException(ErrorCodes.FINDER_SERVICE_MYSQL_CANT_COMMIT_DELETE);
    }, database.metrics.finder_delete.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_COMMIT_DELETE_EXCEPTION);
  }

  public void list(String region, String machine, Callback<List<Key>> callback) {
    database.transact((connection) -> {
      String selectSQL = //
          "SELECT `space`, `key` FROM `" + database.databaseName + //
              "`.`directory` WHERE `region`=? AND `machine`=? AND NOT `deleted` AND `type`=" + //
              LocationType.Machine.type //
          ;
      try (PreparedStatement statementInsertIndex = connection.prepareStatement(selectSQL)) {
        statementInsertIndex.setString(1, region);
        statementInsertIndex.setString(2, machine);
        try (ResultSet rs = statementInsertIndex.executeQuery()) {
          ArrayList<Key> results = new ArrayList<>();
          while (rs.next()) {
            String space = rs.getString(1);
            String key = rs.getString(2);
            results.add(new Key(space, key));
          }
          return results;
        }
      }
    }, database.metrics.finder_list.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_LIST_EXCEPTION);
  }

  public void listDeleted(String region, String machine, Callback<List<Key>> callback) {
    database.transact((connection) -> {
      String selectSQL = //
          "SELECT `space`, `key` FROM `" + database.databaseName + //
              "`.`directory` WHERE `region`=? AND `machine`=? AND `deleted` AND `type`=" + //
              LocationType.Machine.type //
          ;
      try (PreparedStatement statementInsertIndex = connection.prepareStatement(selectSQL)) {
        statementInsertIndex.setString(1, region);
        statementInsertIndex.setString(2, machine);
        try (ResultSet rs = statementInsertIndex.executeQuery()) {
          ArrayList<Key> results = new ArrayList<>();
          while (rs.next()) {
            String space = rs.getString(1);
            String key = rs.getString(2);
            results.add(new Key(space, key));
          }
          return results;
        }
      }
    }, database.metrics.finder_list.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_LIST_EXCEPTION);
  }
}

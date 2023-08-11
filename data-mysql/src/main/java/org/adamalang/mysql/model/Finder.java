/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.data.BackupResult;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Finder implements FinderService {
  private final DataBase dataBase;
  private final String region;

  public Finder(DataBase dataBase, String region) {
    this.dataBase = dataBase;
    this.region = region;
  }

  @Override
  public void find(Key key, Callback<Result> callback) {
    dataBase.transact((connection) -> {
      String selectSQL = //
          "SELECT `id`, `type`, `region`, `machine`, `archive`, `deleted` FROM `" + dataBase.databaseName + //
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
            Location location = Location.fromType(type);
            if (location != null) {
              return new Result(id, location, region, machineValue, archiveValue, rs.getBoolean(6));
            }
          }
        }
      }
      throw new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED);
    }, dataBase.metrics.finder_find.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_FIND_EXCEPTION);
  }

  @Override
  public void findbind(Key key, String machine, Callback<Result> callback) {
    // dumb implementation
    bind(key, machine, new Callback<Void>() {
      @Override
      public void success(Void value) {
        find(key, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        find(key, callback);
      }
    });
  }

  @Override
  public void bind(Key key, String machine, Callback<Void> callback) {
    dataBase.transact((connection) -> {
      String updateIndexSQL = //
          "UPDATE `" + dataBase.databaseName + "`.`directory` " + //
              "SET `type`=" + Location.Machine.type + //
              ", `region`=?" + ", `machine`=?" + " WHERE NOT `deleted` AND `space`=? AND `key`=? AND ((`machine`=? AND `region`=?) OR `type`!=" + Location.Machine.type + ")";
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
          "INSERT INTO `" + dataBase.databaseName + "`.`directory` (" + //
              "`space`, `key`, `type`, `head_seq`, `region`, `machine`, `archive`, `delta_bytes`, `asset_bytes`, `need_gc`) VALUES (?, ?, " + Location.Machine.type + ", 0, ?, ?, '', 0, 0, FALSE)" //
          ;
      try (PreparedStatement statementInsertIndex = connection.prepareStatement(insertSQL)) {
        statementInsertIndex.setString(1, key.space);
        statementInsertIndex.setString(2, key.key);
        statementInsertIndex.setString(3, region);
        statementInsertIndex.setString(4, machine);
        statementInsertIndex.execute();
        return null;
      }
    }, dataBase.metrics.finder_bind.wrap(callback), ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE);
  }

  @Override
  public void free(Key key, String machineOn, Callback<Void> callback) {
    dataBase.transact((connection) -> {
      String freeSQL = //
          "UPDATE `" + dataBase.databaseName + "`.`directory` " + //
              "SET `type`=" + Location.Archive.type + //
              ", `region`=''" + ", `machine`=''" + " WHERE `space`=? AND `key`=? AND `machine`=? AND `region`=? AND `type`=" + Location.Machine.type;
      try (PreparedStatement statementUpdate = connection.prepareStatement(freeSQL)) {
        statementUpdate.setString(1, key.space);
        statementUpdate.setString(2, key.key);
        statementUpdate.setString(3, machineOn);
        statementUpdate.setString(4, region);
        statementUpdate.executeUpdate();
      }
      return null;
    }, dataBase.metrics.finder_free.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_FREE_EXCEPTION);
  }

  @Override
  public void backup(Key key, BackupResult result, String machineOn, Callback<Void> callback) {
    dataBase.transact((connection) -> {
      String backupSQL = //
          "UPDATE `" + dataBase.databaseName + "`.`directory` " + //
              "SET `archive`=?" + ", `head_seq`=" + result.seq + //
              ", `need_gc` = (`need_gc` OR `asset_bytes` != " + result.assetBytes + ")" + //
              ", `delta_bytes`=" + result.deltaBytes + //
              ", `asset_bytes`=" + result.assetBytes + " WHERE `space`=? AND `key`=? AND `machine`=? AND `region`=? AND `type`=" + Location.Machine.type;
      try (PreparedStatement statementUpdate = connection.prepareStatement(backupSQL)) {
        statementUpdate.setString(1, result.archiveKey);
        statementUpdate.setString(2, key.space);
        statementUpdate.setString(3, key.key);
        statementUpdate.setString(4, machineOn);
        statementUpdate.setString(5, region);
        if (statementUpdate.executeUpdate() == 1) {
          return null;
        }
      }
      throw new ErrorCodeException(ErrorCodes.FINDER_SERVICE_MYSQL_CANT_BACKUP);
    }, dataBase.metrics.finder_backup.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_BACKUP_EXCEPTION);
  }

  @Override
  public void markDelete(Key key, String machineOn, Callback<Void> callback) {
    dataBase.transact((connection) -> {
      String backupSQL = //
          "UPDATE `" + dataBase.databaseName + "`.`directory` " + //
              "SET `deleted`=TRUE WHERE `space`=? AND `key`=? AND `machine`=? AND `region`=? AND `type`=" + Location.Machine.type;
      try (PreparedStatement statementUpdate = connection.prepareStatement(backupSQL)) {
        statementUpdate.setString(1, key.space);
        statementUpdate.setString(2, key.key);
        statementUpdate.setString(3, machineOn);
        statementUpdate.setString(4, region);
        if (statementUpdate.executeUpdate() == 1) {
          return null;
        }
      }
      throw new ErrorCodeException(ErrorCodes.FINDER_SERVICE_MYSQL_CANT_MARK_DELETE);
    }, dataBase.metrics.finder_backup.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_MARK_DELETE_EXCEPTION);
  }

  @Override
  public void commitDelete(Key key, String machineOn, Callback<Void> callback) {
    dataBase.transact((connection) -> {
      String deleteSQL = //
          "DELETE FROM `" + dataBase.databaseName + "`.`directory` " + //
              " WHERE `space`=? AND `key`=? AND `machine`=? AND `region`=? AND `type`=" + Location.Machine.type;
      try (PreparedStatement statementDelete = connection.prepareStatement(deleteSQL)) {
        statementDelete.setString(1, key.space);
        statementDelete.setString(2, key.key);
        statementDelete.setString(3, machineOn);
        statementDelete.setString(4, region);
        if (statementDelete.executeUpdate() == 1) {
          return null;
        }
      }
      throw new ErrorCodeException(ErrorCodes.FINDER_SERVICE_MYSQL_CANT_COMMIT_DELETE);
    }, dataBase.metrics.finder_delete.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_COMMIT_DELETE_EXCEPTION);
  }

  @Override
  public void list(String machine, Callback<List<Key>> callback) {
    dataBase.transact((connection) -> {
      String selectSQL = //
          "SELECT `space`, `key` FROM `" + dataBase.databaseName + //
              "`.`directory` WHERE `region`=? AND `machine`=? AND NOT `deleted` AND `type`=" + //
              Location.Machine.type //
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
    }, dataBase.metrics.finder_list.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_LIST_EXCEPTION);
  }

  @Override
  public void listDeleted(String machine, Callback<List<Key>> callback) {
    dataBase.transact((connection) -> {
      String selectSQL = //
          "SELECT `space`, `key` FROM `" + dataBase.databaseName + //
              "`.`directory` WHERE `region`=? AND `machine`=? AND `deleted` AND `type`=" + //
              Location.Machine.type //
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
    }, dataBase.metrics.finder_list.wrap(callback), ErrorCodes.FINDER_SERVICE_MYSQL_LIST_EXCEPTION);
  }
}

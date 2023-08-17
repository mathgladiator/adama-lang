/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.impl;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.data.*;

import java.util.List;

/** The finder for the global control plane region */
public class GlobalRegionFinder implements FinderService {
  private final GlobalFinderCore core;
  private final String region;

  public GlobalRegionFinder(DataBase dataBase, String region) {
    this.core = new GlobalFinderCore(dataBase);
    this.region = region;
  }

  @Override
  public void find(Key key, Callback<DocumentLocation> callback) {
    core.find(key, callback);
  }

  @Override
  public void findbind(Key key, String machine, Callback<DocumentLocation> callback) {
    core.bind(key, region, machine, new Callback<Void>() {
      @Override
      public void success(Void value) {
        core.find(key, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        core.find(key, callback);
      }
    });
  }

  @Override
  public void bind(Key key, String machine, Callback<Void> callback) {
    core.bind(key, region, machine, callback);
  }

  @Override
  public void free(Key key, String machineOn, Callback<Void> callback) {
    core.free(key, region, machineOn, callback);
  }

  @Override
  public void backup(Key key, BackupResult result, String machineOn, Callback<Void> callback) {
    core.backup(key, result, region, machineOn, callback);
  }

  @Override
  public void markDelete(Key key, String machineOn, Callback<Void> callback) {
    core.markDelete(key, region, machineOn, callback);
  }

  @Override
  public void commitDelete(Key key, String machineOn, Callback<Void> callback) {
    core.commitDelete(key, region, machineOn, callback);
  }

  @Override
  public void list(String machine, Callback<List<Key>> callback) {
    core.list(region, machine, callback);
  }

  @Override
  public void listDeleted(String machine, Callback<List<Key>> callback) {
    core.listDeleted(region, machine, callback);
  }
}

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
public class GlobalFinder implements FinderService {
  public final MySQLFinderCore core;
  private final String region;
  private final String machine;

  public GlobalFinder(DataBase dataBase, String region, String machine) {
    this.core = new MySQLFinderCore(dataBase);
    this.region = region;
    this.machine = machine;
  }

  @Override
  public void find(Key key, Callback<DocumentLocation> callback) {
    core.find(key, callback);
  }

  @Override
  public void bind(Key key, Callback<Void> callback) {
    core.bind(key, region, machine, callback);
  }

  @Override
  public void free(Key key, Callback<Void> callback) {
    core.free(key, region, machine, callback);
  }

  @Override
  public void backup(Key key, BackupResult result, Callback<Void> callback) {
    core.backup(key, result, region, machine, callback);
  }

  @Override
  public void markDelete(Key key, Callback<Void> callback) {
    core.markDelete(key, region, machine, callback);
  }

  @Override
  public void commitDelete(Key key, Callback<Void> callback) {
    core.commitDelete(key, region, machine, callback);
  }

  @Override
  public void list(Callback<List<Key>> callback) {
    core.list(region, machine, callback);
  }

  @Override
  public void listDeleted(Callback<List<Key>> callback) {
    core.listDeleted(region, machine, callback);
  }
}

/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

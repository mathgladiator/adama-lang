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

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Capacity;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.runtime.sys.capacity.CapacityInstance;
import org.adamalang.runtime.sys.capacity.CapacityOverseer;

import java.util.List;

/** the global capacity overseer */
public class GlobalCapacityOverseer implements CapacityOverseer  {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(GlobalCapacityOverseer.class);
  private final DataBase database;

  public GlobalCapacityOverseer(DataBase database) {
    this.database = database;
  }

  @Override
  public void listAllSpace(String space, Callback<List<CapacityInstance>> callback) {
    try {
      callback.success(Capacity.listAll(database, space));
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_LIST_SPACE, ex, LOGGER));
    }
  }

  @Override
  public void listAllOnMachine(String region, String machine, Callback<List<CapacityInstance>> callback) {
    try {
      callback.success(Capacity.listAllOnMachine(database, region, machine));
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_LIST_MACHINE, ex, LOGGER));
    }
  }

  @Override
  public void listWithinRegion(String space, String region, Callback<List<CapacityInstance>> callback) {
    try {
      callback.success(Capacity.listRegion(database, space, region));
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_LIST_REGION, ex, LOGGER));
    }
  }

  @Override
  public void add(String space, String region, String machine, Callback<Void> callback) {
    try {
      Capacity.add(database, space, region, machine);
      callback.success(null);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_ADD, ex, LOGGER));
    }
  }

  @Override
  public void remove(String space, String region, String machine, Callback<Void> callback) {
    try {
      Capacity.remove(database, space, region, machine);
      callback.success(null);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_REMOVE, ex, LOGGER));
    }
  }

  @Override
  public void nuke(String space, Callback<Void> callback) {
    try {
      Capacity.removeAll(database, space);
      callback.success(null);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_NUKE, ex, LOGGER));
    }
  }

  @Override
  public void pickStableHostForSpace(String space, String region, Callback<String> callback) {
    try {
      String result = Hosts.pickStableHostFromRegion(database, region, "adama", space);
      if (result == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.GLOBAL_CAPACITY_EMPTY_PICKHOST));
        return;
      }
      callback.success(result);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_PICKHOST, ex, LOGGER));
    }
  }

  @Override
  public void pickNewHostForSpace(String space, String region, Callback<String> callback) {
    try {
      String result = Hosts.pickNewHostForSpace(database, region, "adama", space);
      if (result == null) {
        callback.failure(new ErrorCodeException(ErrorCodes.GLOBAL_CAPACITY_EMPTY_NEWHOST));
        return;
      }
      callback.success(result);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_NEWHOST, ex, LOGGER));
    }
  }
}

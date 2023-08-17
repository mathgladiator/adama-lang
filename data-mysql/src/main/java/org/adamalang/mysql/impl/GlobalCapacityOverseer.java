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
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_ADD, ex, LOGGER));
    }
  }

  @Override
  public void remove(String space, String region, String machine, Callback<Void> callback) {
    try {
      Capacity.remove(database, space, region, machine);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_REMOVE, ex, LOGGER));
    }
  }

  @Override
  public void nuke(String space, Callback<Void> callback) {
    try {
      Capacity.removeAll(database, space);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_NUKE, ex, LOGGER));
    }
  }

  @Override
  public void pickStableHostForSpace(String space, String region, Callback<String> callback) {
    try {
      callback.success(Hosts.pickStableHostFromRegion(database, region, "adama", space));
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_CAPACITY_EXCEPTION_PICKHOST, ex, LOGGER));
    }
  }

  @Override
  public void pickNewHostForSpace(String space, String region, Callback<String> callback) {
    // TODO
    callback.failure(new ErrorCodeException(-1));
  }
}

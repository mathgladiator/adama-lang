/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.util.HashMap;

public class MockFinderService implements FinderService {
  private final HashMap<Key, Result> map;

  public MockFinderService() {
    this.map = new HashMap<>();
  }

  @Override
  public void find(Key key, Callback<Result> callback) {
    Result result = map.get(key);
    if (result != null) {
      callback.success(result);
    } else {
      callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED));
    }
  }

  @Override
  public void bind(Key key, String region, String machine, Callback<Void> callback) {
    if (key.key.contains("cant-bind")) {
      callback.failure(new ErrorCodeException(-1234));
      return;
    }
    Result result = map.get(key);
    if (result != null) {
      if (result.location == Location.Archive) {
        map.put(key, new Result(1, Location.Machine, region, machine, result.archiveKey));
        callback.success(null);
      } else if (machine.equals(result.machine) && result.location == Location.Machine) {
        callback.success(null);
      } else {
        callback.failure(new ErrorCodeException(-1));
        return;
      }
    } else {
      map.put(key, new Result(1, Location.Machine, region, machine, ""));
      callback.success(null);
    }
  }

  public void bindLocal(Key key) {
    bind(key, "test-region", "test-machine", Callback.DONT_CARE_VOID);
  }

  @Override
  public void free(Key key, String machineOn, Callback<Void> callback) {
    Result result = map.get(key);
    if (result != null) {
      if (machineOn.equals(result.machine) && result.location == Location.Machine) {
        map.put(key, new Result(1, Location.Archive, "", "", result.archiveKey));
        callback.success(null);
      } else {
        callback.failure(new ErrorCodeException(-2));
        return;
      }
    } else {
      callback.failure(new ErrorCodeException(-3));
    }

  }

  @Override
  public void backup(Key key, String archiveKey, String machineOn, Callback<Void> callback) {
    Result result = map.get(key);
    if (result != null) {
      if (machineOn.equals(result.machine) && result.location == Location.Machine) {
        map.put(key, new Result(1, result.location, result.region, result.machine, archiveKey));
        callback.success(null);
      } else {
        callback.failure(new ErrorCodeException(-4));
        return;
      }
    } else {
      callback.failure(new ErrorCodeException(-5));
    }
  }

  @Override
  public void delete(Key key, String machineOn, Callback<Void> callback) {
    map.remove(key);
    callback.success(null);
  }

  @Override
  public void update(Key key, long deltaSize, long assetSize, Callback<Void> callback) {
    callback.success(null);
  }
}

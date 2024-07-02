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
package org.adamalang.common.keys;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.security.PrivateKey;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** store private keys in memory for nearly forever. TODO: expire at some point... maybe */
public abstract class PrivateKeyCache {
  private final ConcurrentHashMap<SpaceKeyIdPair, PrivateKey> keys;
  private final SimpleExecutor executor;

  public PrivateKeyCache(SimpleExecutor executor) {
    this.keys = new ConcurrentHashMap<>();
    this.executor = executor;
  }

  public void get(String space, int keyId, Callback<PrivateKey> callback) {
    SpaceKeyIdPair pair = new SpaceKeyIdPair(space, keyId);
    PrivateKey immediate = keys.get(pair);
    if (immediate != null) {
      callback.success(immediate);
      return;
    }
    executor.execute(new NamedRunnable("find-private-key") {
      @Override
      public void execute() throws Exception {
        PrivateKey result = keys.get(pair);
        if (result != null) {
          callback.success(result);
          return;
        }
        result = find(pair);
        if (result == null) {
          callback.failure(new ErrorCodeException(ErrorCodes.PRIVATE_KEY_NOT_FOUND));
          return;
        }
        keys.put(pair, result);
        callback.success(result);
      }
    });
  }

  protected abstract PrivateKey find(SpaceKeyIdPair pair);

  public static class SpaceKeyIdPair {
    public final String space;
    public final int id;

    public SpaceKeyIdPair(String space, int id) {
      this.space = space;
      this.id = id;
    }

    @Override
    public int hashCode() {
      return Objects.hash(space, id);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      SpaceKeyIdPair that = (SpaceKeyIdPair) o;
      return id == that.id && Objects.equals(space, that.space);
    }
  }
}

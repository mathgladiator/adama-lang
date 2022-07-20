/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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

  protected abstract PrivateKey find(SpaceKeyIdPair pair);

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

  public static class SpaceKeyIdPair {
    public final String space;
    public final int id;

    public SpaceKeyIdPair(String space, int id) {
      this.space = space;
      this.id = id;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      SpaceKeyIdPair that = (SpaceKeyIdPair) o;
      return id == that.id && Objects.equals(space, that.space);
    }

    @Override
    public int hashCode() {
      return Objects.hash(space, id);
    }
  }
}

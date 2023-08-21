/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/** a local cache of items bound to the current host */
public class BoundLocalFinderService implements FinderService {
  private final FinderService global;
  private final String region;
  private final String machine;
  private final ConcurrentHashMap<Key, DocumentLocation> cache;

  public BoundLocalFinderService(FinderService global, String region, String machine) {
    this.global = global;
    this.region = region;
    this.machine = machine;
    this.cache = new ConcurrentHashMap<>();
  }

  @Override
  public void find(Key key, Callback<DocumentLocation> callback) {
    DocumentLocation cached = cache.get(key);
    if (cached != null) {
      callback.success(cached);
      return;
    }
    global.find(key, callback);
  }

  @Override
  public void bind(Key key, Callback<Void> callback) {
    global.bind(key, callback);
  }

  @Override
  public void findbind(Key key, Callback<DocumentLocation> callback) {
    DocumentLocation cached = cache.get(key);
    if (cached != null) {
      callback.success(cached);
      return;
    }
    global.findbind(key, new Callback<>() {
      @Override
      public void success(DocumentLocation location) {
        if (BoundLocalFinderService.this.region.equals(location.region) && BoundLocalFinderService.this.machine.equals(location.machine)) {
          cache.put(key, location);
        }
        callback.success(location);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void free(Key key, Callback<Void> callback) {
    cache.remove(key);
    global.free(key, callback);
  }

  @Override
  public void backup(Key key, BackupResult result, Callback<Void> callback) {
    global.backup(key, result, callback);
  }

  @Override
  public void markDelete(Key key, Callback<Void> callback) {
    global.markDelete(key, callback);
  }

  @Override
  public void commitDelete(Key key, Callback<Void> callback) {
    cache.remove(key);
    global.commitDelete(key, callback);
  }

  @Override
  public void list(Callback<List<Key>> callback) {
    global.list(callback);
  }

  @Override
  public void listDeleted(Callback<List<Key>> callback) {
    global.listDeleted(callback);
  }
}

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
  public final String region;
  public final String machine;
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
    global.bind(key, new Callback<Void>() {
      @Override
      public void success(Void value) {
        global.find(key, new Callback<DocumentLocation>() {
          @Override
          public void success(DocumentLocation result) {
            cache.put(key, result);
            callback.success(null);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            // technically, the bind was a success; we just couldn't cache it
            callback.success(null);
          }
        });
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

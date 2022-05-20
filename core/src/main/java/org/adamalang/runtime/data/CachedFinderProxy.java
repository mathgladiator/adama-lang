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

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.util.concurrent.ConcurrentHashMap;

/** a cache a slow finder */
public class CachedFinderProxy implements FinderService {
  private final FinderService finder;
  private final ConcurrentHashMap<Key, Result> cache;

  public CachedFinderProxy(FinderService finder) {
    this.finder = finder;
    this.cache = new ConcurrentHashMap<>();
  }

  @Override
  public void create(Key key, Callback<Void> callback) {
    finder.create(key, callback);
  }

  @Override
  public void find(Key key, Callback<Result> callback) {
    Result cached = cache.get(key);
    if (cached != null) {
      callback.success(cached);
      return;
    }
    finder.find(key, new Callback<>() {
      @Override
      public void success(Result value) {
        cache.put(key, value);
        callback.success(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  /** nuke the cache for the given key */
  public void nuke(Key key) {
    cache.remove(key);
  }

  @Override
  public void set(Key key, String region, String machine, Callback<Void> callback) {
    finder.set(key, region, machine, callback);
  }

  @Override
  public void archive(Key key, String archiveKey, String machineOn, Callback<Void> callback) {
    finder.archive(key, archiveKey, machineOn, callback);
  }

  @Override
  public void delete(Key key, String machineOn, Callback<Void> callback) {
    finder.delete(key, machineOn, new Callback<>() {
      @Override
      public void success(Void value) {
        cache.remove(key);
        callback.success(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        cache.remove(key);
        callback.failure(ex);
      }
    });
  }

  @Override
  public void update(Key key, long deltaSize, long assetSize, Callback<Void> callback) {
    finder.update(key, deltaSize, assetSize, callback);
  }
}

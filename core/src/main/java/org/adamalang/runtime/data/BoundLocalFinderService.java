package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

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
  public void bind(Key key, String machine, Callback<Void> callback) {
    global.bind(key, machine, callback);
  }

  @Override
  public void findbind(Key key, String machine, Callback<DocumentLocation> callback) {
    DocumentLocation cached = cache.get(key);
    if (cached != null) {
      callback.success(cached);
      return;
    }
    global.findbind(key, machine, new Callback<DocumentLocation>() {
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
  public void free(Key key, String machineOn, Callback<Void> callback) {
    cache.remove(key);
    global.free(key, machineOn, callback);
  }

  @Override
  public void backup(Key key, BackupResult result, String machineOn, Callback<Void> callback) {
    global.backup(key, result, machineOn, callback);
  }

  @Override
  public void markDelete(Key key, String machineOn, Callback<Void> callback) {
    global.markDelete(key, machineOn, callback);
  }

  @Override
  public void commitDelete(Key key, String machineOn, Callback<Void> callback) {
    cache.remove(key);
    global.commitDelete(key, machineOn, callback);
  }

  @Override
  public void list(String machine, Callback<List<Key>> callback) {
    global.list(machine, callback);
  }

  @Override
  public void listDeleted(String machine, Callback<List<Key>> callback) {
    global.listDeleted(machine, callback);
  }
}

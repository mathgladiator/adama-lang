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

import java.util.List;

/** an interface to describe how to find a specific document by key */
public interface FinderService {

  /** find the location of a key */
  void find(Key key, Callback<Result> callback);

  /** take over for the key */
  void bind(Key key, String machine, Callback<Void> callback);

  /** find the result and bind it to me */
  void findbind(Key key, String machine, Callback<Result> callback);

  /** release the machine for the given key */
  void free(Key key, String machineOn, Callback<Void> callback);

  /** set a backup copy while still active on machine */
  void backup(Key key, BackupResult result, String machineOn, Callback<Void> callback);

  /** mark the key for deletion */
  void markDelete(Key key, String machineOn, Callback<Void> callback);

  /** signal that deletion has been completed */
  void commitDelete(Key key, String machineOn, Callback<Void> callback);

  /** list all items on a host */
  void list(String machine, Callback<List<Key>> callback);

  /** list all items on a host */
  void listDeleted(String machine, Callback<List<Key>> callback);

  /** where a document may be */
  enum Location {
    // a single machine
    Machine(2),

    // an archive
    Archive(4);

    public final int type;

    Location(int type) {
      this.type = type;
    }

    public static Location fromType(int type) {
      for (Location location : Location.values()) {
        if (location.type == type) {
          return location;
        }
      }
      return null;
    }
  }

  class Result {
    public final long id;
    public final Location location;
    public final String region;
    public final String machine;
    public final String archiveKey;
    public final boolean deleted;

    public Result(long id, Location location, String region, String machine, String archiveKey, boolean deleted) {
      this.id = id;
      this.location = location;
      this.region = region;
      this.machine = machine;
      this.archiveKey = archiveKey;
      this.deleted = deleted;
    }
  }
}

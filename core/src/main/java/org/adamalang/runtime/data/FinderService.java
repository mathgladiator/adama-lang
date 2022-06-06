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

/** an interface to describe how to find a specific document by key */
public interface FinderService {

  /** find the location of a key */
  void find(Key key, Callback<Result> callback);

  /** take over for the key */
  void bind(Key key, String region, String machine, Callback<Void> callback);

  /** release the machine for the given key */
  void free(Key key, String machineOn, Callback<Void> callback);

  /** set a backup copy while still active on machine */
  void backup(Key key, String archiveKey, long deltaSize, long assetSize, String machineOn, Callback<Void> callback);

  /** delete the key */
  void delete(Key key, String machineOn, Callback<Void> callback);

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

    public Result(long id, Location location, String region, String machine, String archiveKey) {
      this.id = id;
      this.location = location;
      this.region = region;
      this.machine = machine;
      this.archiveKey = archiveKey;
    }
  }
}

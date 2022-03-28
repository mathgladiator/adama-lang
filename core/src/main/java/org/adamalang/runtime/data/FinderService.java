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

  /** where a document may be */
  public static enum Location {
    // freshly created
    Fresh(1),

    // a single machine
    Machine(2),

    // an archive
    Archive(4);

    public final int type;

    private Location(int type) {
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

  public static class Result {
    public final long id;
    public final Location location;
    public final String value;

    public Result(long id, Location location, String value) {
      this.id = id;
      this.location = location;
      this.value = value;
    }
  }

  /** create a key; fails if key already exists */
  public void create(Key key, Callback<Void> callback);

  /** find the location of a key */
  public void find(Key key, Callback<Result> callback);

  /** take over for the key */
  public void takeover(Key key, Callback<Void> callback);

  /** archive the key and give up control */
  public void archive(Key key, String archiveKey, Callback<Void> callback);

  /** delete the key */
  public void delete(Key key, Callback<Void> callback);

  /** update billing related properties about the key */
  public void update(Key key, long deltaSize, long assetSize, Callback<Void> callback);
}

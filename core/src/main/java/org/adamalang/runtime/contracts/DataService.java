/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.natives.NtClient;

import java.util.Objects;

/** the contract for the data service */
public interface DataService {

  /** a document is identified by a key */
  public static class Key implements Comparable<Key> {
    public final String space;
    public final String key;

    public Key(String space, String key) {
      this.space = space;
      this.key = key;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Key key1 = (Key) o;
      return Objects.equals(space, key1.space) && Objects.equals(key, key1.key);
    }

    @Override
    public int hashCode() {
      return Objects.hash(space, key);
    }

    @Override
    public int compareTo(Key o) {
      if (this == o) return 0;
      int delta = space.compareTo(o.space);
      if (delta == 0) {
        delta = key.compareTo(o.key);
      }
      return delta;
    }
  }

  /** the local copy of the document should be changed by incorporating the given patch */
  public static class LocalDocumentChange {
    public final String patch;
    public final int seq;

    public LocalDocumentChange(String patch, int seq) {
      this.patch = patch;
      this.seq = seq;
    }
  }

  /** the remote copy should change */
  public static class RemoteDocumentUpdate {
    /** the request that is changing the document */
    public final String request;

    /** the request as as redo patch */
    public final String redo;

    /** the undo patch to revert this change */
    public final String undo;

    /** the sequencer of this change */
    public final int seq;

    /** who was responsible for the update */
    public final NtClient who;

    /** an identifier for the update from a user */
    public final String marker;

    /** this update is incomplete with respect to time, and this will ensure we schedule an invalidation in the future */
    public final boolean requiresFutureInvalidation;

    /** if requiresFutureInvalidation, then how many milliseconds should the system wait to invoke invalidation */
    public final int whenToInvalidateMilliseconds;

    public RemoteDocumentUpdate(final int seq, NtClient who, String marker, final String request, final String redo, final String undo, final boolean requiresFutureInvalidation, int whenToInvalidateMilliseconds) {
      this.seq = seq;
      this.who = who;
      this.marker = marker;
      this.request = request;
      this.redo = redo;
      this.undo = undo;
      this.requiresFutureInvalidation = requiresFutureInvalidation;
      this.whenToInvalidateMilliseconds = whenToInvalidateMilliseconds;
    }
  }

  /** create a new empty document */
  public void create(Key key, Callback<Long> callback);

  /** Download the entire object and return the entire json */
  void get(Key key, Callback<LocalDocumentChange> callback);

  /** write the first entry for the document */
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback);

  /** Apply a patch to the document using rfc7396 */
  public void patch(Key key, RemoteDocumentUpdate patch, Callback<Void> callback);

  /** Create a copy of the document from the beginning of time up to indicated sequencer */
  public void fork(Key keyCopy, Key keyDest, NtClient who, String marker, Callback<LocalDocumentChange> callback);

  /** Rewind the state of the document to the indicated marker by the given client */
  public void rewind(Key key, NtClient who, String marker, Callback<LocalDocumentChange> callback);

  /** Unsend the message at the given marker by the given client */
  public void unsend(Key key, NtClient who, String marker, Callback<LocalDocumentChange> callback);

  /** Delete the document given by the ID */
  void delete(Key key, Callback<Long> callback);

}

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

/** the contract for the data service */
public interface DataService {

  /** the local copy of the document should be changed by incorporating the given patch */
  public static class LocalDocumentChange {
    public final String patch;

    public LocalDocumentChange(String patch) {
      this.patch = patch;
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

    /** this update is incomplete with respect to time, and this will ensure we schedule an invalidation in the future */
    public final boolean requiresFutureInvalidation;

    /** if requiresFutureInvalidation, then how many milliseconds should the system wait to invoke invalidation */
    public final int whenToInvalidateMilliseconds;

    public RemoteDocumentUpdate(final int seq, NtClient who, final String request, final String redo, final String undo, final boolean requiresFutureInvalidation, int whenToInvalidateMilliseconds) {
      this.seq = seq;
      this.who = who;
      this.request = request;
      this.redo = redo;
      this.undo = undo;
      this.requiresFutureInvalidation = requiresFutureInvalidation;
      this.whenToInvalidateMilliseconds = whenToInvalidateMilliseconds;
    }
  }

  /** scan for keys to bring in that have a time based component to them */
  void scan(ActiveKeyStream stream);

  /** Download the entire object and return the entire json */
  void get(Key key, Callback<LocalDocumentChange> callback);

  /** write the first entry for the document */
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback);

  /** Apply a patch to the document using rfc7396 */
  public void patch(Key key, RemoteDocumentUpdate patch, Callback<Void> callback);

  public static enum ComputeMethod {
    Rewind,
    Unsend
  }

  /** Compute the change to rewind the state of the document to the indicated seq by the given client */
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback);

  /** Delete the document given by the ID */
  void delete(Key key, Callback<Void> callback);

}

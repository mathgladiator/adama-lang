/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

/** the contract for the data service */
public interface DataService {
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

    /** this update is incomplete with respect to time, and this will ensure we schedule an invalidation in the future */
    public final boolean requiresFutureInvalidation;

    /** if requiresFutureInvalidation, then how many milliseconds should the system wait to invoke invalidation */
    public final int whenToInvalidateMilliseconds;

    public RemoteDocumentUpdate(final int seq, final String request, final String redo, final String undo, final boolean requiresFutureInvalidation, int whenToInvalidateMilliseconds) {
      this.seq = seq;
      this.request = request;
      this.redo = redo;
      this.undo = undo;
      this.requiresFutureInvalidation = requiresFutureInvalidation;
      this.whenToInvalidateMilliseconds = whenToInvalidateMilliseconds;
    }
  }

  /** create a new empty document */
  public void create(DataCallback<Long> callback);

  /** Download the entire object and return the entire json */
  void get(long documentId, DataCallback<LocalDocumentChange> callback);

  /** write the first entry for the document */
  public void initialize(long documentId, RemoteDocumentUpdate patch, DataCallback<Void> callback);

  /** Apply a patch to the document using rfc7396 */
  public void patch(long documentId, RemoteDocumentUpdate patch, DataCallback<Void> callback);

  /** Create a copy of the document from the beginning of time up to indicated sequencer */
  long fork(long oldDocumentId, long newDocumentId, long seqEnd, DataCallback<LocalDocumentChange> callback);

  /** Rewind the state of the document to the indicated sequencer */
  void rewind(long documentId, long seqEnd, DataCallback<LocalDocumentChange> callback);

  /** Unsend the message(s) inclusively between the indicated sequencers */
  void unsend(long documentId, long seqBegin, long seqEnd, DataCallback<LocalDocumentChange> callback);

  /** Delete the document given by the ID */
  void delete(long documentId, DataCallback<Long> callback);

}

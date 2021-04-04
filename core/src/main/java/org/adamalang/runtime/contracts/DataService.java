/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

import com.fasterxml.jackson.databind.node.ObjectNode;

/** the contract for the data service */
public interface DataService {
  public static class DocumentChange {
    public ObjectNode node;
    public long seq;
  }

  /** create a new empty document */
  public void create(DataCallback<Long> callback);

  /** Download the entire object and return the entire json */
  void get(String gameSpace, long documentId, DataCallback<DocumentChange> callback);

  /** Apply a patch to the document using rfc7396 */
  void patch(long documentId, long seq, ObjectNode redo, ObjectNode undo, boolean requiresFutureInvalidation, int whenToInvalideMilliseconds, DataCallback<Long> callback);

  /** Create a copy of the document from the beginning of time up to indicated sequencer */
  long fork(long documentId, long seqEnd, DataCallback<DocumentChange> callback);

  /** Rewind the state of the document to the indicated sequencer */
  void rewind(long documentId, long seqEnd, DataCallback<DocumentChange> callback);

  /** Unsend the message(s) inclusively between the indicated sequencers */
  void unsend(long documentId, long seqBegin, long seqEnd, DataCallback<DocumentChange> callback);

  /** Delete the document given by the ID */
  void delete(long documentId, DataCallback<Long> callback);

}

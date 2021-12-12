package org.adamalang.runtime.exceptions;

/** the document throws this to rewind itself; this is destructive of all state */
public class PerformDocumentRewindException extends RuntimeException {
    public final int seq;

    /** @param seq the sequencer to rewind to. */
    public PerformDocumentRewindException(int seq) {
        this.seq = seq;
    }
}

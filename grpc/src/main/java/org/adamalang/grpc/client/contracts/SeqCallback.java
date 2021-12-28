package org.adamalang.grpc.client.contracts;

/** various document operations (attach/send) result in a sequencer; these operations may also fail */
public interface SeqCallback {
    /** the operation was a success */
    public void success(int seq);

    /** the operation failed */
    public void error(int code);
}

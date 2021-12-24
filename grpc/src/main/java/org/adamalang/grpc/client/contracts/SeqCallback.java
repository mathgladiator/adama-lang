package org.adamalang.grpc.client.contracts;

import io.grpc.stub.StreamObserver;
import org.adamalang.ErrorCodes;
import org.adamalang.grpc.proto.StreamSeqResult;
import org.adamalang.runtime.contracts.ExceptionLogger;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** various document operations (attach/send) result in a sequencer; these operations may also fail */
public interface SeqCallback {
    /** the operation was a success */
    public void success(int seq);

    /** the operation failed */
    public void error(int code);
}

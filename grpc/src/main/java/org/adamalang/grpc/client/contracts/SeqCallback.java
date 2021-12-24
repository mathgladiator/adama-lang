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

    public static StreamObserver<StreamSeqResult> WRAP(SeqCallback callback, ExceptionLogger logger) {
        return new StreamObserver<>() {
            @Override
            public void onNext(StreamSeqResult seqResult) {
                callback.success(seqResult.getSeq());
            }

            @Override
            public void onError(Throwable throwable) {
                callback.error(ErrorCodeException.detectOrWrap(ErrorCodes.GRPC_SEQ_UNKNOWN_EXCEPTION, throwable, logger).code);
            }

            @Override
            public void onCompleted() {
            }
        };
    }
}

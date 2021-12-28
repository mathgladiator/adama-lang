package org.adamalang.grpc.client.contracts;

import io.grpc.stub.StreamObserver;
import org.adamalang.ErrorCodes;
import org.adamalang.grpc.proto.CreateResponse;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.ErrorCodeException;

/** asking for a document to be created is either success or not (with a code) */
public interface CreateCallback {
    /** create was successful */
    public void created();

    /** create was not successful */
    public void error(int code);

    public static StreamObserver<CreateResponse> WRAP(CreateCallback callback, ExceptionLogger logger) {
        return new StreamObserver<>() {
            @Override
            public void onNext(CreateResponse createResponse) {
                if (createResponse.getSuccess()) {
                    callback.created();
                } else {
                    callback.error(createResponse.getFailureReason());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.error(ErrorCodeException.detectOrWrap(ErrorCodes.GRPC_CREATE_UNKNOWN_EXCEPTION, throwable, logger).code);
            }

            @Override
            public void onCompleted() {
            }
        };
    }
}

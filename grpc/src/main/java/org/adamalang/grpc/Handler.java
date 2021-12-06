package org.adamalang.grpc;

import io.grpc.stub.StreamObserver;
import org.adamalang.grpc.proto.*;

public class Handler extends AdamaGrpc.AdamaImplBase {

    @Override
    public void create(CreateRequest request, StreamObserver<CreateResponse> responseObserver) {
        super.create(request, responseObserver);
    }

    @Override
    public StreamObserver<MultiplexedStreamMessageClient> multiplexedProtocol(StreamObserver<MultiplexedStreamMessageServer> responseObserver) {
        return new StreamObserver<MultiplexedStreamMessageClient>() {
            @Override
            public void onNext(MultiplexedStreamMessageClient multiplexedStreamMessageClient) {
                long id = multiplexedStreamMessageClient.getId();
                StreamMessageClient payload = multiplexedStreamMessageClient.getPayload();
                switch (payload.getByTypeCase()) {
                    case CONNECT:
                        break;
                    case SEND:
                        break;
                    case DISCONNECT:
                        break;
                }

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }

    @Override
    public StreamObserver<StreamMessageClient> singleProtocol(StreamObserver<StreamMessageServer> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(StreamMessageClient payload) {
                switch (payload.getByTypeCase()) {
                    case CONNECT:
                        break;
                    case SEND:
                        break;
                    case DISCONNECT:
                        break;
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }
}

package org.adamalang.grpc.server;

import io.grpc.stub.StreamObserver;
import org.adamalang.grpc.proto.*;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.CoreStream;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Handler extends AdamaGrpc.AdamaImplBase {

    public final CoreService service;

    public Handler(CoreService service) {
        this.service = service;
    }

    @Override
    public void create(CreateRequest request, StreamObserver<CreateResponse> responseObserver) {
        service.create(new NtClient(request.getAgent(), request.getAuthority()), new Key(request.getSpace(), request.getKey()), request.getArg(), request.getEntropy(), new Callback<Void>() {
            @Override
            public void success(Void value) {
                responseObserver.onNext(CreateResponse.newBuilder().setSuccess(true).build());
            }

            @Override
            public void failure(ErrorCodeException ex) {
                responseObserver.onNext(CreateResponse.newBuilder().setSuccess(false).setFailureReason(ex.code).build());
            }
        });
    }

    @Override
    public StreamObserver<MultiplexedStreamMessageClient> multiplexedProtocol(StreamObserver<MultiplexedStreamMessageServer> responseObserver) {
        ConcurrentHashMap<Long, CoreStream> streams = new ConcurrentHashMap<>();

        return new StreamObserver<>() {
            @Override
            public void onNext(MultiplexedStreamMessageClient multiplexedStreamMessageClient) {
                long id = multiplexedStreamMessageClient.getId();
                long actOn = multiplexedStreamMessageClient.getAct();
                CoreStream stream = null;
                if (actOn > 0) {
                    stream = streams.get(actOn);
                    if (stream == null) {
                        int ERRORCODETODO = 555;
                        StreamMessageServer toServer = StreamMessageServer.newBuilder().setError(StreamError.newBuilder().setCode(555).build()).build();
                        responseObserver.onNext(MultiplexedStreamMessageServer.newBuilder().setId(id).setPayload(toServer).build());
                        return;
                    }
                }
                StreamMessageClient payload = multiplexedStreamMessageClient.getPayload();
                switch (payload.getByTypeCase()) {
                    case CONNECT:
                        StreamConnect connect = payload.getConnect();
                        service.connect(new NtClient(connect.getAgent(), connect.getAuthority()), new Key(connect.getSpace(), connect.getKey()), new Streamback() {
                            @Override
                            public void onSetupComplete(CoreStream stream) {
                                streams.put(id, stream);
                            }

                            @Override
                            public void status(StreamStatus status) {
                                StreamStatusCode code = StreamStatusCode.Connected;
                                switch (status) {
                                    case Connected:
                                        code = StreamStatusCode.Connected;
                                        break;
                                    case Disconnected:
                                        code = StreamStatusCode.Disconnected;
                                        break;
                                }
                                StreamMessageServer toServer = StreamMessageServer.newBuilder().setStatus(org.adamalang.grpc.proto.StreamStatus.newBuilder().setCode(code).build()).build();
                                responseObserver.onNext(MultiplexedStreamMessageServer.newBuilder().setId(id).setPayload(toServer).build());
                                if (status == StreamStatus.Disconnected) {
                                    streams.remove(id);
                                }
                            }

                            @Override
                            public void next(String data) {
                                StreamMessageServer toServer = StreamMessageServer.newBuilder().setData(StreamData.newBuilder().setDelta(data).build()).build();
                                responseObserver.onNext(MultiplexedStreamMessageServer.newBuilder().setId(id).setPayload(toServer).build());
                            }

                            @Override
                            public void failure(ErrorCodeException exception) {
                                StreamMessageServer toServer = StreamMessageServer.newBuilder().setError(StreamError.newBuilder().setCode(exception.code).build()).build();
                                responseObserver.onNext(MultiplexedStreamMessageServer.newBuilder().setId(id).setPayload(toServer).build());
                                streams.remove(id);
                            }
                        });
                        break;
                    case SEND:
                        if (stream != null) {
                            StreamSend send = payload.getSend();
                            stream.send(send.getChannel(), send.getMarker(), send.getMessage(), new Callback<>() {
                                @Override
                                public void success(Integer value) {
                                    StreamMessageServer toServer = StreamMessageServer.newBuilder().setResult(StreamSeqResult.newBuilder().setSeq(value).build()).build();
                                    responseObserver.onNext(MultiplexedStreamMessageServer.newBuilder().setId(id).setPayload(toServer).build());
                                }

                                @Override
                                public void failure(ErrorCodeException exception) {
                                    StreamMessageServer toServer = StreamMessageServer.newBuilder().setError(StreamError.newBuilder().setCode(exception.code).build()).build();
                                    responseObserver.onNext(MultiplexedStreamMessageServer.newBuilder().setId(id).setPayload(toServer).build());
                                }
                            });
                        }
                        break;
                    case DISCONNECT:
                        if (stream != null) {
                            stream.disconnect();
                            streams.remove(actOn);
                            StreamMessageServer toServer = StreamMessageServer.newBuilder().setStatus(org.adamalang.grpc.proto.StreamStatus.newBuilder().setCode(StreamStatusCode.Disconnected).build()).build();
                            responseObserver.onNext(MultiplexedStreamMessageServer.newBuilder().setId(id).setPayload(toServer).build());
                        }
                        break;
                }

            }

            @Override
            public void onError(Throwable throwable) {
                for (CoreStream stream : streams.values()) {
                    stream.disconnect();
                }
            }

            @Override
            public void onCompleted() {
                for (CoreStream stream : streams.values()) {
                    stream.disconnect();
                }
            }
        };
    }

}

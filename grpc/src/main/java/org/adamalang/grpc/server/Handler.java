package org.adamalang.grpc.server;

import io.grpc.stub.StreamObserver;
import org.adamalang.ErrorCodes;
import org.adamalang.grpc.proto.*;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.CoreStream;

import java.util.concurrent.ConcurrentHashMap;

public class Handler extends AdamaGrpc.AdamaImplBase {

    public final CoreService service;

    public Handler(CoreService service) {
        this.service = service;
    }

    private static String fixEntropy(String entropy) {
        if ("".equals(entropy)) {
            return null;
        }
        try {
            Long.parseLong(entropy);
            return entropy;
        } catch (NumberFormatException nfe) {
            return "" + entropy.hashCode();
        }
    }

    @Override
    public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        responseObserver.onNext(PingResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void create(CreateRequest request, StreamObserver<CreateResponse> responseObserver) {
        service.create(new NtClient(request.getAgent(), request.getAuthority()), new Key(request.getSpace(), request.getKey()), request.getArg(), fixEntropy(request.getEntropy()), new Callback<>() {
            @Override
            public void success(Void value) {
                responseObserver.onNext(CreateResponse.newBuilder().setSuccess(true).build());
                responseObserver.onCompleted();
            }

            @Override
            public void failure(ErrorCodeException ex) {
                responseObserver.onNext(CreateResponse.newBuilder().setSuccess(false).setFailureReason(ex.code).build());
                responseObserver.onCompleted();
            }
        });
    }

    @Override
    public StreamObserver<StreamMessageClient> multiplexedProtocol(StreamObserver<StreamMessageServer> responseObserver) {
        ConcurrentHashMap<Long, CoreStream> streams = new ConcurrentHashMap<>();
        responseObserver.onNext(StreamMessageServer.newBuilder().setEstablish(Establish.newBuilder().build()).build());
        return new StreamObserver<>() {
            @Override
            public void onNext(StreamMessageClient payload) {
                long id = payload.getId();
                CoreStream stream = null;
                if (payload.hasAct()) {
                    stream = streams.get(payload.getAct());
                    if (stream == null) {
                        responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(ErrorCodes.GRPC_COMMON_FAILED_TO_FIND_STREAM_USING_GIVEN_ACT).build()).build());
                        return;
                    }
                }
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
                                responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setStatus(org.adamalang.grpc.proto.StreamStatus.newBuilder().setCode(code).build()).build());
                                if (status == StreamStatus.Disconnected) {
                                    streams.remove(id);
                                }
                            }

                            @Override
                            public void next(String data) {
                                responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setData(StreamData.newBuilder().setDelta(data).build()).build());
                            }

                            @Override
                            public void failure(ErrorCodeException exception) {
                                responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(exception.code).build()).build());
                                streams.remove(id);
                            }
                        });
                        return;
                    case ASK:
                        stream.canAttach(new Callback<>() {
                            @Override
                            public void success(Boolean value) {
                                responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setResponse(StreamAskAttachmentResponse.newBuilder().setAllowed(value).build()).build());
                            }

                            @Override
                            public void failure(ErrorCodeException ex) {
                                responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(ex.code).build()).build());
                            }
                        });
                        return;
                    case ATTACH:
                        StreamAttach attach = payload.getAttach();
                        NtAsset asset = new NtAsset(attach.getId(), attach.getFilename(), attach.getContentType(), attach.getSize(), attach.getMd5(), attach.getSha384());
                        stream.attach(asset, new Callback<>() {
                            @Override
                            public void success(Integer value) {
                                responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setResult(StreamSeqResult.newBuilder().setSeq(value).build()).build());
                            }

                            @Override
                            public void failure(ErrorCodeException ex) {
                                responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(ex.code).build()).build());
                            }
                        });
                        return;
                    case SEND:
                        StreamSend send = payload.getSend();
                        String marker = null;
                        if (send.hasMarker()) {
                            marker = send.getMarker();
                        }
                        stream.send(send.getChannel(), marker, send.getMessage(), new Callback<>() {
                            @Override
                            public void success(Integer value) {
                                responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setResult(StreamSeqResult.newBuilder().setSeq(value).build()).build());
                            }

                            @Override
                            public void failure(ErrorCodeException exception) {
                                responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(exception.code).build()).build());
                            }
                        });
                        return;
                    case DISCONNECT:
                        System.err.println("disconnect");
                        stream.disconnect();
                        streams.remove(payload.getAct());
                        return;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                // TODO: log throwable
               onCompleted();
            }

            @Override
            public void onCompleted() {
                for (CoreStream stream : streams.values()) {
                    stream.disconnect();
                }
                responseObserver.onCompleted();
            }
        };
    }

}

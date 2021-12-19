package org.adamalang.grpc.client;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.grpc.stub.StreamObserver;
import org.adamalang.grpc.proto.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public class MultiplexProtocol {
    private final Executor service;
    private final StreamObserver<MultiplexedStreamMessageClient> upstream;
    private final HashMap<Long, DocumentConnection> documents;
    private final AtomicLong idgen;
    private final HashMap<Long, SettableFuture<Boolean>> outstandingBooleans;
    private final HashMap<Long, SettableFuture<Integer>> outstandingIntegers;

    public class DocumentConnection {
        private final long id;
        private DocumentEvents events;


        public DocumentConnection() {
            this.id = idgen.getAndIncrement();
            this.events = null;
        }

        protected void bind(DocumentEvents events) {
            this.events = events;
        }

        public ListenableFuture<Integer> send(String channel, String marker, String message) {
            long sendId = idgen.getAndIncrement();
            SettableFuture<Integer> future = SettableFuture.create();
            outstandingIntegers.put(sendId, future);
            upstream.onNext(MultiplexedStreamMessageClient.newBuilder().setId(sendId).setAct(id).setPayload(StreamMessageClient.newBuilder().setSend(StreamSend.newBuilder().setChannel(channel).setMarker(marker).setMessage(message).build()).build()).build());
            return future;
        }
    }

    public MultiplexProtocol(Executor service, AdamaGrpc.AdamaStub stub) {
        this.idgen = new AtomicLong(1);
        this.service = service;
        this.documents = new HashMap<>();
        this.outstandingBooleans = new HashMap<>();
        this.outstandingIntegers = new HashMap<>();
        this.upstream = stub.multiplexedProtocol(new StreamObserver<MultiplexedStreamMessageServer>() {
            @Override
            public void onNext(MultiplexedStreamMessageServer multiplexedStreamMessageServer) {
                service.execute(() -> {
                    StreamMessageServer payload = multiplexedStreamMessageServer.getPayload();



                    if (payload.getByTypeCase() == StreamMessageServer.ByTypeCase.BYTYPE_NOT_SET) {
                        // TODO: send an error
                        return;
                    }

                    switch (payload.getByTypeCase()) {
                        case DATA: {
                            DocumentConnection document = documents.get(multiplexedStreamMessageServer.getId());
                            if (document == null) {
                                return;
                            }
                            document.events.delta(payload.getData().getDelta());
                            return;
                        }
                        case STATUS: {
                            DocumentConnection document = documents.get(multiplexedStreamMessageServer.getId());
                            if (document == null) {
                                return;
                            }
                            if (payload.getStatus().getCode() != StreamStatusCode.Connected) {
                                documents.remove(multiplexedStreamMessageServer.getId());
                                document.events.disconnected();
                            } else {
                                document.events.connected();
                            }
                            return;
                        }
                        case RESPONSE: {
                            SettableFuture<Boolean> future = outstandingBooleans.remove(multiplexedStreamMessageServer.getId());
                            if (future != null) {
                                future.set(payload.getResponse().getAllowed());
                            }
                            return;
                        }
                        case RESULT: {
                            SettableFuture<Integer> future = outstandingIntegers.remove(multiplexedStreamMessageServer.getId());
                            if (future != null) {
                                future.set(payload.getResult().getSeq());
                            }
                            return;
                        }
                        case ERROR: {
                            DocumentConnection document = documents.remove(multiplexedStreamMessageServer.getId());
                            if (document != null) {
                                document.events.error(payload.getError().getCode());
                                documents.remove(multiplexedStreamMessageServer.getId());
                                return;
                            }
                            SettableFuture<Boolean> futureBool = outstandingBooleans.remove(multiplexedStreamMessageServer.getId());
                            if (futureBool != null) {
                                futureBool.setException(new RuntimeException("" + payload.getError().getCode()));
                                return;
                            }
                            SettableFuture<Integer> futureInt = outstandingIntegers.remove(multiplexedStreamMessageServer.getId());
                            if (futureInt != null) {
                                futureInt.setException(new RuntimeException("" + payload.getError().getCode()));
                                return;
                            }
                            return;
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                onCompleted();
            }

            @Override
            public void onCompleted() {
                service.execute(() -> {
                    for (Map.Entry<Long, DocumentConnection> entry : documents.entrySet()) {
                        entry.getValue().events.disconnected();
                    }
                    documents.clear();
                });
            }
        });
    }

    public void connect(String space, String key, String agent, String authority, DocumentEventsFactory factory) {
        DocumentConnection dc = new DocumentConnection();
        service.execute(() -> {
            documents.put(dc.id, dc);
            DocumentEvents events = factory.make(dc);
            dc.bind(events);
            upstream.onNext(MultiplexedStreamMessageClient.newBuilder().setId(dc.id).setPayload(StreamMessageClient.newBuilder().setConnect(StreamConnect.newBuilder().setAgent(agent).setAuthority(authority).setSpace(space).setKey(key).build()).build()).build());
            // TODO: write out the create message
        });
    }
}

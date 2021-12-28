package org.adamalang.gossip;

import io.grpc.stub.StreamObserver;
import org.adamalang.gossip.proto.*;

import java.util.Collections;
import java.util.concurrent.Executor;

public class Handler extends GossipGrpc.GossipImplBase {
    private Executor executor;
    private final InstanceSetChain chain;

    public Handler(Executor executor, InstanceSetChain chain) {
        this.executor = executor;
        this.chain = chain;
    }

    @Override
    public void bootstrap(BootstrapRequest request, StreamObserver<BootstrapResponse> responseObserver) {
        chain.ingest(Collections.singleton(request.getEndpoint()));
        responseObserver.onNext(BootstrapResponse.newBuilder().addAllEndpoints(chain.all()).build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GossipForward> exchange(StreamObserver<GossipReverse> responseObserver) {
        return new StreamObserver<>() {
            private InstanceSet set = null;
            @Override
            public void onNext(GossipForward gossipForward) {
                executor.execute(() -> {
                    switch (gossipForward.getChatterCase()) {
                        case START: {
                            BeginGossip start = gossipForward.getStart();
                            chain.ingest(start.getRecentEndpointsList());
                            set = chain.find(start.getHash());
                            if (set != null) {
                                responseObserver.onNext(GossipReverse.newBuilder().setOptimisticReturn(HashFoundRequestForwardQuickGossip.newBuilder().addAllCounters(set.counters()).addAllRecentEndpoints(chain.recent()).build()).build());
                            } else {
                                set = chain.current();
                                responseObserver.onNext(GossipReverse.newBuilder().setSadReturn(HashNotFoundReverseConversation.newBuilder().setHash(set.hash()).addAllRecentEndpoints(chain.recent()).build()).build());
                            }
                            return;
                        }
                        case FOUND_REVERSE: {
                            ReverseHashFound reverseHashFound = gossipForward.getFoundReverse();
                            set.ingest(reverseHashFound.getCountersList(), chain.now());
                            chain.ingest(reverseHashFound.getMissingEndpointsList());
                            responseObserver.onNext(GossipReverse.newBuilder().setTurnTables(ReverseHashFound.newBuilder().addAllCounters(set.counters()).addAllMissingEndpoints(chain.missing(set)).build()).build());
                            return;
                        }
                        case QUICK_GOSSIP: {
                            set.ingest(gossipForward.getQuickGossip().getCountersList(), chain.now());
                            return;
                        }
                        case SLOW_GOSSIP: {
                            chain.ingest(gossipForward.getSlowGossip().getAllEndpointsList());
                            return;
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                // TODO: LOG ERROR
                onCompleted();
            }

            @Override
            public void onCompleted() {
            }
        };
    }

    // bootstrap

    // exchange

    // optimistic
}

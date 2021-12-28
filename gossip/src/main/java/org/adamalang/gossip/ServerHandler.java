package org.adamalang.gossip;

import io.grpc.stub.StreamObserver;
import org.adamalang.gossip.proto.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerHandler extends GossipGrpc.GossipImplBase {
    private final Executor executor;
    private final InstanceSetChain chain;
    private final Metrics metrics;
    private AtomicBoolean alive;

    public ServerHandler(Executor executor, InstanceSetChain chain, AtomicBoolean alive, Metrics metrics) {
        this.executor = executor;
        this.chain = chain;
        this.metrics = metrics;
        this.alive = alive;
    }

    @Override
    public StreamObserver<GossipForward> exchange(StreamObserver<GossipReverse> responseObserver) {
        if (!alive.get()) {
            responseObserver.onError(new Exception("shutting-down"));
        }
        return new StreamObserver<>() {
            private InstanceSet set = null;

            @Override
            public void onNext(GossipForward gossipForward) {
                executor.execute(() -> {
                    switch (gossipForward.getChatterCase()) {
                        case START: {
                            metrics.bump_start();
                            BeginGossip start = gossipForward.getStart();
                            chain.ingest(start.getRecentEndpointsList(), new HashSet<>(start.getRecentDeletesList()));
                            set = chain.find(start.getHash());
                            if (set != null) {
                                responseObserver.onNext(GossipReverse.newBuilder().setOptimisticReturn(HashFoundRequestForwardQuickGossip.newBuilder().addAllCounters(set.counters()).addAllRecentDeletes(chain.deletes()).addAllMissingEndpoints(chain.missing(set)).build()).build());
                            } else {
                                set = chain.current();
                                responseObserver.onNext(GossipReverse.newBuilder().setSadReturn(HashNotFoundReverseConversation.newBuilder().setHash(set.hash()).addAllRecentEndpoints(chain.recent()).addAllRecentDeletes(chain.deletes()).build()).build());
                            }
                            return;
                        }
                        case FOUND_REVERSE: {
                            metrics.bump_found_reverse();
                            ReverseHashFound reverseHashFound = gossipForward.getFoundReverse();
                            set.ingest(reverseHashFound.getCountersList(), chain.now());
                            chain.ingest(reverseHashFound.getMissingEndpointsList(), Collections.emptySet());
                            responseObserver.onNext(GossipReverse.newBuilder().setTurnTables(ReverseHashFound.newBuilder().addAllCounters(set.counters()).addAllMissingEndpoints(chain.missing(set)).build()).build());
                            return;
                        }
                        case QUICK_GOSSIP: {
                            metrics.bump_quick_gossip();
                            set.ingest(gossipForward.getQuickGossip().getCountersList(), chain.now());
                            responseObserver.onCompleted();
                            return;
                        }
                        case SLOW_GOSSIP: {
                            metrics.bump_slow_gossip();
                            TreeSet<String> incoming = new TreeSet<>();
                            for (Endpoint ep : gossipForward.getSlowGossip().getAllEndpointsList()) {
                                incoming.add(ep.getId());
                            }
                            chain.ingest(gossipForward.getSlowGossip().getAllEndpointsList(), Collections.emptySet());
                            ArrayList<Endpoint> toSend = new ArrayList<>();
                            for (Endpoint ep : chain.all()) {
                                if (!incoming.contains(ep.getId())) {
                                    toSend.add(ep);
                                }
                            }
                            responseObserver.onNext(GossipReverse.newBuilder().setComplement(SlowGossipComplement.newBuilder().addAllMissingEndpoints(toSend).build()).build());
                            responseObserver.onCompleted();
                            return;
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                metrics.log_error(throwable);
            }

            @Override
            public void onCompleted() {
            }
        };
    }
}

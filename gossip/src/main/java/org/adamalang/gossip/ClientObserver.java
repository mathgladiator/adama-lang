package org.adamalang.gossip;

import com.google.protobuf.ProtocolStringList;
import io.grpc.stub.StreamObserver;
import org.adamalang.gossip.proto.*;

import java.util.Collections;
import java.util.TreeSet;
import java.util.concurrent.Executor;

public class ClientObserver implements StreamObserver<GossipReverse> {
    private final Executor executor;
    private final InstanceSetChain chain;
    private final Metrics metrics;
    private StreamObserver<GossipForward> forward;
    private InstanceSet current;

    public ClientObserver(Executor executor, InstanceSetChain chain, Metrics metrics) {
        this.executor = executor;
        this.chain = chain;
        this.metrics = metrics;
        this.forward = null;
    }


    public void initiate(StreamObserver<GossipForward> forward) {
        this.forward = forward;
        executor.execute(() -> {
            current = chain.current();
            forward.onNext(GossipForward.newBuilder().setStart(BeginGossip.newBuilder().setHash(current.hash()).addAllRecentDeletes(chain.deletes()).addAllRecentEndpoints(chain.recent()).build()).build());
        });
    }

    @Override
    public void onNext(GossipReverse gossipReverse) {
        executor.execute(() -> {
            switch (gossipReverse.getChatterCase()) {
                case SAD_RETURN: {
                    metrics.bump_sad_return();
                    HashNotFoundReverseConversation reverse = gossipReverse.getSadReturn();
                    chain.ingest(reverse.getRecentEndpointsList(), new TreeSet<>(reverse.getRecentDeletesList()));
                    current = chain.find(reverse.getHash());
                    if (current != null) {
                        forward.onNext(GossipForward.newBuilder().setFoundReverse(ReverseHashFound.newBuilder().addAllCounters(current.counters()).addAllMissingEndpoints(chain.missing(current)).build()).build());
                        forward.onCompleted();
                    } else {
                        forward.onNext(GossipForward.newBuilder().setSlowGossip(SlowGossip.newBuilder().addAllAllEndpoints(chain.all()).build()).build());
                    }
                    return;
                }
                case COMPLEMENT: {
                    metrics.bump_complement();
                    SlowGossipComplement complement = gossipReverse.getComplement();
                    chain.ingest(complement.getMissingEndpointsList(), Collections.emptySet());
                    forward.onCompleted();
                    return;
                }
                case OPTIMISTIC_RETURN: {
                    metrics.bump_optimistic_return();
                    HashFoundRequestForwardQuickGossip found = gossipReverse.getOptimisticReturn();
                    current.ingest(found.getCountersList(), chain.now());
                    chain.ingest(found.getMissingEndpointsList(), new TreeSet<>(found.getRecentDeletesList()));
                    forward.onNext(GossipForward.newBuilder().setQuickGossip(ForwardQuickGossip.newBuilder().addAllCounters(current.counters()).build()).build());
                    forward.onCompleted();
                    return;
                }
                case TURN_TABLES: {
                    metrics.bump_turn_tables();
                    ReverseHashFound found = gossipReverse.getTurnTables();
                    current.ingest(found.getCountersList(), chain.now());
                    chain.ingest(found.getMissingEndpointsList(), Collections.emptySet());
                    forward.onCompleted();
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
}

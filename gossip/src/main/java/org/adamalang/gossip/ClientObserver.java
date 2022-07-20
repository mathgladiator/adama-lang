/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.gossip;

import io.grpc.stub.StreamObserver;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.gossip.proto.*;

import java.util.Collections;
import java.util.TreeSet;

/** Represents the client for initiating a gossip to a peer */
public class ClientObserver implements StreamObserver<GossipReverse> {
  private final SimpleExecutor executor;
  private final InstanceSetChain chain;
  private final GossipMetrics metrics;
  private StreamObserver<GossipForward> forward;
  private InstanceSet current;
  private boolean done;

  public ClientObserver(SimpleExecutor executor, InstanceSetChain chain, GossipMetrics metrics) {
    this.executor = executor;
    this.chain = chain;
    this.metrics = metrics;
    this.forward = null;
    this.done = false;
  }

  public boolean isDone() {
    return done;
  }

  public void initiate(StreamObserver<GossipForward> forward) {
    this.forward = forward;
    executor.execute(new NamedRunnable("client-gossip-initiate") {
      @Override
      public void execute() throws Exception {
        current = chain.current();
        forward.onNext(GossipForward.newBuilder().setStart(BeginGossip.newBuilder().setHash(current.hash()).addAllRecentDeletes(chain.deletes()).addAllRecentEndpoints(chain.recent()).build()).build());
      }
    });
  }

  @Override
  public void onNext(GossipReverse gossipReverse) {
    executor.execute(new NamedRunnable("observer-on-next") {
      @Override
      public void execute() throws Exception {
        switch (gossipReverse.getChatterCase()) {
          case SAD_RETURN: {
            metrics.bump_sad_return();
            HashNotFoundReverseConversation reverse = gossipReverse.getSadReturn();
            chain.ingest(reverse.getRecentEndpointsList(), new TreeSet<>(reverse.getRecentDeletesList()));
            current = chain.find(reverse.getHash());
            if (current != null) {
              forward.onNext(GossipForward.newBuilder().setFoundReverse(ReverseHashFound.newBuilder().addAllCounters(current.counters()).addAllMissingEndpoints(chain.missing(current)).build()).build());
            } else {
              forward.onNext(GossipForward.newBuilder().setSlowGossip(SlowGossip.newBuilder().addAllAllEndpoints(chain.all()).build()).build());
            }
            return;
          }
          case SLOW_GOSSIP: {
            metrics.bump_client_slow_gossip();
            chain.ingest(gossipReverse.getSlowGossip().getAllEndpointsList(), Collections.emptySet());
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
      }
    });
  }

  @Override
  public void onError(Throwable throwable) {
    metrics.log_error(throwable);
  }

  @Override
  public void onCompleted() {
    executor.execute(new NamedRunnable("observer-on-finished") {
      @Override
      public void execute() throws Exception {
        done = true;
      }
    });
  }
}

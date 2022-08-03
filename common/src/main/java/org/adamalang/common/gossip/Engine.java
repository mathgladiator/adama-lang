/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.gossip;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.*;
import org.adamalang.common.gossip.codec.GossipProtocol;
import org.adamalang.common.gossip.codec.GossipProtocolCodec;
import org.adamalang.common.net.ByteStream;
import org.adamalang.common.net.ChannelClient;

public class Engine {
  private final String ip;
  private final GossipMetrics metrics;
  private final SimpleExecutor executor;
  private final InstanceSetChain chain;

  public Engine(String ip, GossipMetrics metrics) {
    this.ip = ip;
    this.metrics = metrics;
    this.executor = SimpleExecutor.create("gossip");
    this.chain = new InstanceSetChain(TimeSource.REAL_TIME);
  }

  public Runnable createLocalApplicationHeartbeat(String role, int port, int monitoringPort) {
    GossipProtocol.Endpoint endpoint = new GossipProtocol.Endpoint();
    endpoint.id = ProtectedUUID.generate();
    endpoint.ip = ip;
    endpoint.port = port;
    endpoint.monitoringPort = monitoringPort;
    endpoint.counter = 0;
    endpoint.created = System.currentTimeMillis();
    GossipProtocol.Endpoint[] local = new GossipProtocol.Endpoint[] { endpoint };
    String[] deletes = new String[] {};
    return () -> {
      executor.execute(new NamedRunnable("heartbeat-local-app") {
        @Override
        public void execute() throws Exception {
          endpoint.counter++;
          chain.ingest(local, deletes);
        }
      });
    };
  }

  public void registerClient(ChannelClient client) {
  }

  public void unregisterClient(ChannelClient client) {
  }

  public class Exchange extends GossipProtocolCodec.StreamChatterFromServer {
    private InstanceSet current;
    private ByteStream remote;

    public void start(ByteStream remote) {
      this.remote = remote;
      executor.execute(new NamedRunnable("gossip-start") {
        @Override
        public void execute() throws Exception {
          current = chain.current();
          ByteBuf buf = remote.create(100);
          GossipProtocol.BeginGossip begin = new GossipProtocol.BeginGossip();
          begin.hash = current.hash();
          begin.recent_deletes = chain.deletes();
          begin.recent_endpoints = chain.recent();
          GossipProtocolCodec.write(buf, begin);
          remote.next(buf);
        }
      });
    }

    @Override
    public void handle(GossipProtocol.ReverseSlowGossip payload) {
      executor.execute(new NamedRunnable("gossip-reverse-slow") {
        @Override
        public void execute() throws Exception {
          chain.ingest(payload.all_endpoints, payload.recent_deletes);
          remote.completed();
        }
      });
    }

    @Override
    public void handle(GossipProtocol.ReverseQuickGossip payload) {
      executor.execute(new NamedRunnable("gossip-reverse-quick") {
        @Override
        public void execute() throws Exception {
          current.ingest(payload.counters, chain.now());
          chain.ingest(payload.missing_endpoints, new String[] {});
          remote.completed();
        }
      });
    }

    @Override
    public void handle(GossipProtocol.HashNotFoundReverseConversation payload) {
      executor.execute(new NamedRunnable("gossip-hash-not-found-do-reverse") {
        @Override
        public void execute() throws Exception {
          chain.ingest(payload.recent_endpoints, payload.recent_deletes);
          current = chain.find(payload.hash);
          if (current != null) {
            GossipProtocol.ReverseHashFound found = new GossipProtocol.ReverseHashFound();
            found.missing_endpoints = chain.missing(current);
            found.counters = current.counters();
            ByteBuf buf = remote.create(100);
            GossipProtocolCodec.write(buf, found);
            remote.next(buf);
          } else {
            GossipProtocol.ForwardSlowGossip slow = new GossipProtocol.ForwardSlowGossip();
            slow.all_endpoints = chain.all();
            slow.recent_deletes = chain.deletes();
            ByteBuf buf = remote.create(100);
            GossipProtocolCodec.write(buf, slow);
            remote.next(buf);
          }
        }
      });
    }

    @Override
    public void handle(GossipProtocol.HashFoundRequestForwardQuickGossip payload) {
      executor.execute(new NamedRunnable("gossip-hash-found-forward") {
        @Override
        public void execute() throws Exception {
          current.ingest(payload.counters, chain.now());
          chain.ingest(payload.recent_endpoints, payload.recent_deletes);
          GossipProtocol.ForwardQuickGossip quick = new GossipProtocol.ForwardQuickGossip();
          quick.counters = current.counters();
          ByteBuf buf = remote.create(100);
          GossipProtocolCodec.write(buf, quick);
          remote.next(buf);
          remote.completed();
        }
      });
    }

    @Override
    public void completed() {
    }

    @Override
    public void error(int errorCode) {
    }
  }

  public Exchange client() {
    return new Exchange();
  }

  public ByteStream server(ByteStream upstream) {
    return new GossipProtocolCodec.StreamChatterFromClient() {
      @Override
      public void completed() { }

      @Override
      public void error(int errorCode) { }

      @Override
      public void handle(GossipProtocol.ForwardSlowGossip payload) {
        executor.execute(new NamedRunnable("gossip-forward-slow") {
          @Override
          public void execute() throws Exception {
            chain.ingest(payload.all_endpoints, payload.recent_deletes);
            GossipProtocol.ReverseSlowGossip slow = new GossipProtocol.ReverseSlowGossip();
            slow.all_endpoints = chain.all();
            slow.recent_deletes = chain.deletes();
            ByteBuf buf = upstream.create(100);
            GossipProtocolCodec.write(buf, slow);
            upstream.next(buf);
            upstream.completed();
          }
        });
      }

      @Override
      public void handle(GossipProtocol.ReverseHashFound payload) {
        executor.execute(new NamedRunnable("gossip-reverse-hash-found") {
          @Override
          public void execute() throws Exception {
            set.ingest(payload.counters, chain.now());
            chain.ingest(payload.missing_endpoints, new String[]{});
            GossipProtocol.ReverseQuickGossip quick = new GossipProtocol.ReverseQuickGossip();
            quick.counters = set.counters();
            quick.missing_endpoints = chain.missing(set);
            ByteBuf buf = upstream.create(100);
            GossipProtocolCodec.write(buf, quick);
            upstream.next(buf);
            upstream.completed();
          }
        });
      }

      @Override
      public void handle(GossipProtocol.ForwardQuickGossip payload) {
        executor.execute(new NamedRunnable("gossip-forward-quick") {
          @Override
          public void execute() throws Exception {
            set.ingest(payload.counters, chain.now());
            upstream.completed();
          }
        });
      }
      InstanceSet set;

      @Override
      public void handle(GossipProtocol.BeginGossip payload) {
        executor.execute(new NamedRunnable("gossip-begin") {
          @Override
          public void execute() throws Exception {
            chain.ingest(payload.recent_endpoints, payload.recent_deletes);
            set = chain.find(payload.hash);
            if (set != null) {
              GossipProtocol.HashFoundRequestForwardQuickGossip found = new GossipProtocol.HashFoundRequestForwardQuickGossip();
              found.counters = set.counters();
              found.recent_deletes = chain.deletes();
              found.recent_endpoints = chain.missing(set);
              ByteBuf buf = upstream.create(100);
              GossipProtocolCodec.write(buf, found);
              upstream.next(buf);
            } else {
              set = chain.current();
              GossipProtocol.HashNotFoundReverseConversation notfound = new GossipProtocol.HashNotFoundReverseConversation();
              notfound.hash = set.hash;
              notfound.recent_endpoints = chain.recent();
              notfound.recent_deletes = chain.deletes();
              ByteBuf buf = upstream.create(100);
              GossipProtocolCodec.write(buf, notfound);
              upstream.next(buf);
            }
          }
        });
      }
    };
  }
}

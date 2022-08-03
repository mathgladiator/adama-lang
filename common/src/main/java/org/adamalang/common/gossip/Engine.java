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
import io.netty.buffer.Unpooled;
import org.adamalang.common.*;
import org.adamalang.common.gossip.codec.GossipProtocol;
import org.adamalang.common.gossip.codec.GossipProtocolCodec;
import org.adamalang.common.net.ByteStream;
import org.adamalang.common.net.ChannelClient;

import java.util.*;
import java.util.function.Consumer;

public class Engine {
  private final String ip;
  private final GossipMetrics metrics;
  private final SimpleExecutor executor;
  private final InstanceSetChain chain;
  private final HashMap<String, ArrayList<Consumer<Collection<String>>>> subscribersByApp;
  private final HashMap<Integer, ChannelClient> clients;
  private int clientId;

  public Engine(String ip, GossipMetrics metrics, TimeSource time) {
    this.ip = ip;
    this.metrics = metrics;
    this.executor = SimpleExecutor.create("gossip");
    this.chain = new InstanceSetChain(time);
    this.subscribersByApp = new HashMap<>();
    this.clients = new HashMap<>();
    this.clientId = 1;
  }

  public void createLocalApplicationHeartbeat(String role, int port, int monitoringPort, Consumer<Runnable> callback) {
    executor.execute(new NamedRunnable("heartbeat-local-app") {
      @Override
      public void execute() throws Exception {
        String id = ProtectedUUID.generate();
        GossipProtocol.Endpoint endpoint = new GossipProtocol.Endpoint();
        endpoint.id = id;
        endpoint.ip = ip;
        endpoint.role = role;
        endpoint.port = port;
        endpoint.monitoringPort = monitoringPort;
        endpoint.counter = 0;
        endpoint.created = System.currentTimeMillis();
        GossipProtocol.Endpoint[] local = new GossipProtocol.Endpoint[] { endpoint };
        String[] deletes = new String[] {};
        chain.ingest(local, deletes);
        callback.accept(chain.pick(id));
      }
    });
  }

  public void subscribe(String role, Consumer<Collection<String>> consumer) {
    executor.execute(new NamedRunnable("subscribe-app", role) {
      @Override
      public void execute() throws Exception {
        ArrayList<Consumer<Collection<String>>> subscribers = subscribersByApp.get(role);
        if (subscribers == null) {
          subscribers = new ArrayList<>();
          subscribersByApp.put(role, subscribers);
        }
        subscribers.add(consumer);
        consumer.accept(chain.current().targetsFor(role));
      }
    });
  }

  public void broadcastChangesWhileInExecutor() {
    for (Map.Entry<String, ArrayList<Consumer<Collection<String>>>> entry : subscribersByApp.entrySet()) {
      var set = chain.current().targetsFor(entry.getKey());
      for (Consumer<Collection<String>> subscriber : entry.getValue()) {
        subscriber.accept(set);
      }
    }
  }

  public void summarizeHtml(Consumer<String> html) {
    executor.execute(new NamedRunnable("summarizing-html") {
      @Override
      public void execute() throws Exception {
        StringBuilder sbHtml = new StringBuilder();
        sbHtml.append("<html><head><title>Gossip Summary</title></head><body><table>");
        sbHtml.append("<tr><th>ID</th><th>Witness (ms ago)</th><th>IP</th><th>Port</th><th>Role</th><th>Counter</th><th>Age (ms)</th></tr>");
        ArrayList<Instance> copySortedForHumans = new ArrayList<>(chain.current().instances);
        copySortedForHumans.sort(Instance::humanizeCompare);
        for (Instance instance : copySortedForHumans) {
          sbHtml.append("<tr>");
          sbHtml.append("<td>").append(instance.id).append("</td>");
          sbHtml.append("<td>").append(System.currentTimeMillis() - instance.witnessed()).append(" ms</td>");
          sbHtml.append("<td>").append(instance.ip).append("</td>");
          sbHtml.append("<td>").append(instance.port).append("</td>");
          sbHtml.append("<td>").append(instance.role).append("</td>");
          sbHtml.append("<td>").append(instance.counter()).append("</td>");
          sbHtml.append("<td>").append(System.currentTimeMillis() - instance.created).append("</td>");
          sbHtml.append("</tr>");
        }
        sbHtml.append("</table></body></html>");
        html.accept(sbHtml.toString());
      }
    });
  }

  public void tick() {
    executor.execute(new NamedRunnable("round") {
      @Override
      public void execute() throws Exception {
        chain.scan();
        chain.gc();
      }
    });
  }

  public Runnable registerClient(ChannelClient client) {
    final int id;
    synchronized (clients) {
      id = clientId++;
      clients.put(id, client);
    }
    return () -> {
      synchronized (clients) {
        clients.remove(id);
      }
    };
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
          ByteBuf buf = Unpooled.buffer();
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
          boolean changed = chain.ingest(payload.all_endpoints, payload.recent_deletes);
          remote.completed();
          if (changed) {
            broadcastChangesWhileInExecutor();
          }
        }
      });
    }

    @Override
    public void handle(GossipProtocol.ReverseQuickGossip payload) {
      executor.execute(new NamedRunnable("gossip-reverse-quick") {
        @Override
        public void execute() throws Exception {
          current.ingest(payload.counters, chain.now());
          chain.ingest(payload.missing_endpoints, payload.recent_deletes);
          remote.completed();
          if (payload.missing_endpoints.length > 0 || payload.recent_deletes.length > 0) {
            broadcastChangesWhileInExecutor();
          }
        }
      });
    }

    @Override
    public void handle(GossipProtocol.HashNotFoundReverseConversation payload) {
      executor.execute(new NamedRunnable("gossip-hash-not-found-do-reverse") {
        @Override
        public void execute() throws Exception {
          boolean changed = chain.ingest(payload.recent_endpoints, payload.recent_deletes);
          current = chain.find(payload.hash);
          ByteBuf buf = Unpooled.buffer();
          if (current != null) {
            GossipProtocol.ReverseHashFound found = new GossipProtocol.ReverseHashFound();
            found.missing_endpoints = chain.missing(current);
            found.recent_deletes = chain.deletes();
            found.counters = current.counters();
            GossipProtocolCodec.write(buf, found);
          } else {
            GossipProtocol.ForwardSlowGossip slow = new GossipProtocol.ForwardSlowGossip();
            slow.all_endpoints = chain.all();
            slow.recent_deletes = chain.deletes();
            GossipProtocolCodec.write(buf, slow);
          }
          remote.next(buf);
          if (changed) {
            broadcastChangesWhileInExecutor();
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
          boolean changed = chain.ingest(payload.recent_endpoints, payload.recent_deletes);
          GossipProtocol.ForwardQuickGossip quick = new GossipProtocol.ForwardQuickGossip();
          quick.counters = current.counters();
          quick.recent_endpoints = chain.recent();
          quick.recent_deletes = chain.deletes();
          ByteBuf buf = Unpooled.buffer();
          GossipProtocolCodec.write(buf, quick);
          remote.next(buf);
          remote.completed();
          if (changed) {
            broadcastChangesWhileInExecutor();
          }
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

  public void ready(Runnable done) {
    executor.execute(new NamedRunnable("ready-check") {
      @Override
      public void execute() throws Exception {
        done.run();
      }
    });
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
            boolean changed = chain.ingest(payload.all_endpoints, payload.recent_deletes);
            GossipProtocol.ReverseSlowGossip slow = new GossipProtocol.ReverseSlowGossip();
            slow.all_endpoints = chain.all();
            slow.recent_deletes = chain.deletes();
            ByteBuf buf = Unpooled.buffer();
            GossipProtocolCodec.write(buf, slow);
            upstream.next(buf);
            upstream.completed();
            if (changed) {
              broadcastChangesWhileInExecutor();
            }
          }
        });
      }

      @Override
      public void handle(GossipProtocol.ReverseHashFound payload) {
        executor.execute(new NamedRunnable("gossip-reverse-hash-found") {
          @Override
          public void execute() throws Exception {
            set.ingest(payload.counters, chain.now());
            boolean changed = chain.ingest(payload.missing_endpoints, payload.recent_deletes);
            GossipProtocol.ReverseQuickGossip quick = new GossipProtocol.ReverseQuickGossip();
            quick.counters = set.counters();
            quick.missing_endpoints = chain.missing(set);
            quick.recent_deletes = chain.deletes();
            ByteBuf buf = Unpooled.buffer();
            GossipProtocolCodec.write(buf, quick);
            upstream.next(buf);
            upstream.completed();
            if (changed) {
              broadcastChangesWhileInExecutor();
            }
          }
        });
      }

      @Override
      public void handle(GossipProtocol.ForwardQuickGossip payload) {
        executor.execute(new NamedRunnable("gossip-forward-quick") {
          @Override
          public void execute() throws Exception {
            set.ingest(payload.counters, chain.now());
            boolean changed = chain.ingest(payload.recent_endpoints, payload.recent_deletes);
            upstream.completed();
            if (changed) {
              broadcastChangesWhileInExecutor();
            }
          }
        });
      }
      InstanceSet set;

      @Override
      public void handle(GossipProtocol.BeginGossip payload) {
        executor.execute(new NamedRunnable("gossip-begin") {
          @Override
          public void execute() throws Exception {
            boolean changed = chain.ingest(payload.recent_endpoints, payload.recent_deletes);
            set = chain.find(payload.hash);
            ByteBuf buf = Unpooled.buffer();
            if (set != null) {
              GossipProtocol.HashFoundRequestForwardQuickGossip found = new GossipProtocol.HashFoundRequestForwardQuickGossip();
              found.counters = set.counters();
              found.recent_deletes = chain.deletes();
              found.recent_endpoints = chain.missing(set);
              GossipProtocolCodec.write(buf, found);
            } else {
              set = chain.current();
              GossipProtocol.HashNotFoundReverseConversation notfound = new GossipProtocol.HashNotFoundReverseConversation();
              notfound.hash = set.hash;
              notfound.recent_endpoints = chain.recent();
              notfound.recent_deletes = chain.deletes();
              GossipProtocolCodec.write(buf, notfound);
            }
            upstream.next(buf);
            if (changed) {
              broadcastChangesWhileInExecutor();
            }
          }
        });
      }
    };
  }
}

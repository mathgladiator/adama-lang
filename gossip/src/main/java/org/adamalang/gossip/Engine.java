/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.gossip;

import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import org.adamalang.common.*;
import org.adamalang.gossip.proto.Endpoint;
import org.adamalang.gossip.proto.GossipGrpc;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Engine implements AutoCloseable {
    private final ChannelCredentials credentials;
    private final InstanceSetChain chain;
    private final Runnable me;
    private final Random jitter;
    private final GossipPartnerPicker picker;
    private final ScheduledExecutorService executor;
    private final String ip;
    private final Metrics metrics;
    private final AtomicBoolean alive;
    private HashMap<String, GossipGrpc.GossipStub> stubs;
    private final Supplier<Server> serverSupplier;

    private io.grpc.Server server;
    private ScheduledFuture<?> gossiper = null;

    public Engine(MachineIdentity identity, TimeSource time, HashSet<String> initial, int port, Metrics metrics) throws Exception {
        this.credentials = TlsChannelCredentials.newBuilder() //
                .keyManager(identity.getCert(), identity.getKey()) //
                .trustManager(identity.getTrust()).build(); //
        this.chain = new InstanceSetChain(time);
        String id = UUID.randomUUID().toString();
        chain.ingest(Collections.singleton(Endpoint.newBuilder().setIp(identity.ip).setId(id).setPort(port).setCounter(0).setRole("gossip").build()), Collections.emptySet());
        me = chain.pick(id);
        this.jitter = new Random();
        this.picker = new GossipPartnerPicker(chain, initial, jitter);
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.ip = identity.ip;
        this.metrics = metrics;
        this.alive = new AtomicBoolean(false);
        this.stubs = new HashMap<>();
        this.server = null;
        serverSupplier =  ExceptionSupplier.TO_RUNTIME(() ->
                NettyServerBuilder.forPort(port).addService(new ServerHandler(executor, chain, alive, metrics))
                .sslContext(GrpcSslContexts //
                        .forServer(identity.getCert(), identity.getKey()) //
                        .trustManager(identity.getTrust()) //
                        .clientAuth(ClientAuth.REQUIRE) //
                        .build())
                .build());
    }

    // these require executor
    public void newApp(String role, int port, Consumer<Runnable> callback) {
        executor.execute(() -> {
            String id = UUID.randomUUID().toString();
            chain.ingest(Collections.singleton(Endpoint.newBuilder().setIp(ip).setId(id).setPort(port).setCounter(0).setRole(role).build()), Collections.emptySet());
            callback.accept(chain.pick(id));
        });
    }

    public void hash(Consumer<String> callback) {
        executor.execute(() -> {
            callback.accept(chain.current().hash());
        });
    }

    private void gossipInExecutor() {
        // heartbeat myself
        me.run();
        // pick a random partner
        String target = picker.pick();
        GossipGrpc.GossipStub stub = stubs.get(target);
        if (stub == null) {
            stub = GossipGrpc.newStub(NettyChannelBuilder.forTarget(target, credentials).build());
            stubs.put(target, stub);
        }
        ClientObserver observer = new ClientObserver(executor, chain, metrics);
        observer.initiate(stub.exchange(observer));
        executor.schedule(() -> {
            if (!observer.isDone()) {
                observer.onError(new RuntimeException("Gossip took too long"));
            }
        }, jitter.nextInt(1000) + 2000, TimeUnit.MILLISECONDS);
    }

    /** Start serving requests. */
    public void start() throws IOException {
        if (alive.compareAndExchange(false, true) == false) {
            server = serverSupplier.get();
            server.start();
            gossiper = executor.scheduleAtFixedRate(() -> {
                try {
                    gossipInExecutor();
                } catch (Exception failed) {
                    metrics.log_error(failed);
                }
            }, jitter.nextInt(500) + 500, 500, TimeUnit.MILLISECONDS);
            Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
                Engine.this.close();
            })));
        }
    }

    /** Finish serving request */
    @Override
    public void close() throws InterruptedException {
        if (alive.compareAndExchange(true, false) == true) {
            gossiper.cancel(false);
            server.shutdownNow().awaitTermination(4, TimeUnit.SECONDS);
            executor.shutdown();
            executor.awaitTermination(4, TimeUnit.SECONDS);
            server = null;
            gossiper = null;
        }
    }
}

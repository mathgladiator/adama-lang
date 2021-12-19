package org.adamalang.grpc.server;

import io.grpc.ServerBuilder;
import io.grpc.TlsServerCredentials;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import org.adamalang.grpc.common.MachineIdentity;
import org.adamalang.runtime.sys.CoreService;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private final io.grpc.Server server;
    private final CoreService service;
    private final int port;
    private final AtomicBoolean alive;

    public Server(MachineIdentity identity, CoreService service, int port) throws Exception{
        this.service = service;
        this.port = port;
        this.alive = new AtomicBoolean(true);
        // .useTransportSecurity()
        this.server = NettyServerBuilder.forPort(port).addService(new Handler(service))
                .sslContext(GrpcSslContexts //
                        .forServer(identity.getCert(), identity.getKey()) //
                        .trustManager(identity.getTrust()) //
                        .clientAuth(ClientAuth.REQUIRE) //
                        .build())
                .build();
    }

    /** Start serving requests. */
    public void start() throws IOException {
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (alive.compareAndExchange(true, false)) {
                System.err.println("*** auto-shutting down server since JVM is shutting down");
                try {
                    Server.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        }));
    }

    public void stop() throws InterruptedException {
        alive.set(false);
        server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
}

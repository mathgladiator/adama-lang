package org.adamalang.grpc.client;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.stub.StreamObserver;
import org.adamalang.grpc.common.MachineIdentity;
import org.adamalang.grpc.proto.*;

import java.io.File;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public class Client {
    private final Executor executor;
    private final ManagedChannel channel;
    public final AdamaGrpc.AdamaStub stub;
    public final MultiplexProtocol protocol;

    public Client(MachineIdentity identity, String target, Executor executor) throws Exception {
        ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
                .keyManager(identity.getCert(), identity.getKey())
                .trustManager(identity.getTrust()).build();
        this.channel =  NettyChannelBuilder.forTarget(target, credentials).build();
        this.stub = AdamaGrpc.newStub(channel);
        this.executor = executor;
        this.protocol = new MultiplexProtocol(executor, stub);
    }

    public ListenableFuture<Void> create(String agent, String authority, String space, String key, String entropy, String arg) {
        SettableFuture<Void> future = SettableFuture.create();
        CreateRequest.Builder builder = CreateRequest.newBuilder().setAgent(agent).setAuthority(authority).setSpace(space).setKey(key).setArg(arg);
        if (entropy != null) {
            builder.setEntropy(entropy);
        }
        CreateRequest request = builder.build();
        stub.create(request, new StreamObserver<>() {
            @Override
            public void onNext(CreateResponse createResponse) {
                System.err.println("got a create response: " + createResponse.getSuccess() + "/" + createResponse.getFailureReason());
                if (createResponse.getSuccess()) {
                    future.set(null);
                } else {

                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.err.println("completed");
            }
        });
        return future;
    }

    public ListenableFuture<MultiplexProtocol> findConnection() {
        return Futures.immediateFuture(protocol);
    }
}

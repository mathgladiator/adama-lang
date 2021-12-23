package org.adamalang.grpc.client;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.adamalang.grpc.common.MachineIdentity;
import org.adamalang.grpc.proto.*;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** a managed client that makes talking to the gRPC server nice */
public class Client {
    private final ScheduledExecutorService executor;
    private final ManagedChannel channel;
    public final AdamaGrpc.AdamaStub stub;
    public final MultiplexProtocol protocol;
    public final ClientState state;
    private final Random rng;

    public Client(MachineIdentity identity, String target, ScheduledExecutorService executor) throws Exception {
        ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
                .keyManager(identity.getCert(), identity.getKey())
                .trustManager(identity.getTrust()).build();
        this.channel =  NettyChannelBuilder.forTarget(target, credentials).build();
        this.stub = AdamaGrpc.newStub(channel);
        this.executor = executor;
        this.state = new ClientState(executor);
        this.protocol = new MultiplexProtocol(executor, stub);
        this.rng = new Random();
    }

    /** block the current thread to ensure the client is connected right now */
    public boolean ping(int timeLimit) throws Exception {
        AtomicBoolean success = new AtomicBoolean(false);
        int backoff = 5;
        int time = 0;
        do {
            CountDownLatch latch = new CountDownLatch(1);
            this.stub.ping(PingRequest.newBuilder().build(), new StreamObserver<>() {
                @Override
                public void onNext(PingResponse pingResponse) {
                    System.err.println("onNext: " + success.get());
                    success.set(true);
                    latch.countDown();
                }

                @Override
                public void onError(Throwable throwable) {
                    System.err.println("onError");
                    latch.countDown();
                }

                @Override
                public void onCompleted() {
                    latch.countDown();
                }
            });
            latch.await(timeLimit - time, TimeUnit.MILLISECONDS);
            if (success.get()) {
                System.err.println("->exit");
                return true;
            }
            Thread.sleep(backoff);
            time += backoff;
            backoff += Math.round(backoff * rng.nextDouble() + 1);
        } while (!success.get() && time < timeLimit);
        return false;
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
                if (createResponse.getSuccess()) {
                    future.set(null);
                } else {
                    future.setException(new ErrorCodeException(createResponse.getFailureReason()));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                future.setException(throwable);
            }

            @Override
            public void onCompleted() {
            }
        });
        return future;
    }

    public DocumentInterface connect(String agent, String authority, String space, String key, RemoteDocumentEvents events) {
        RemoteDocument document = new RemoteDocument(state.generateId(), agent, authority, space, key, events);
        state.connect(document);
        return new DocumentInterface() {
            @Override
            public void send(String channel, String marker, String message) {
                state.send(document, channel, marker, message);
            }

            @Override
            public void disconnect() {
                state.disconnect(document);
            }
        };
    }

    @Deprecated
    public ListenableFuture<MultiplexProtocol> findConnection() {
        return Futures.immediateFuture(protocol);
    }
}

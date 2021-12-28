package org.adamalang.gossip;

import io.grpc.stub.StreamObserver;
import org.adamalang.gossip.proto.BootstrapRequest;
import org.adamalang.gossip.proto.BootstrapResponse;
import org.adamalang.gossip.proto.Endpoint;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ServerBootstrapTests {
    @Test
    public void bootstrap() {
        MockTime timeX = new MockTime();
        MockMetrics metrics = new MockMetrics();
        InstanceSetChain X = new InstanceSetChain(timeX);
        ServerHandler serverHandler = new ServerHandler((r) -> r.run(), X, metrics);
        AtomicReference<BootstrapResponse> response = new AtomicReference<>(null);
        AtomicBoolean complete = new AtomicBoolean(false);
        serverHandler.bootstrap(BootstrapRequest.newBuilder().setEndpoint(Endpoint.newBuilder().setId("me").setIp("ip").build()).build(), new StreamObserver<BootstrapResponse>() {
            @Override
            public void onNext(BootstrapResponse bootstrapResponse) {
                response.set(bootstrapResponse);
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onCompleted() {
                complete.set(true);
            }
        });
        Assert.assertTrue(complete.get());
        Assert.assertNotNull(response.get());
    }
}

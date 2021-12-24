package org.adamalang.grpc.mocks;

import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.contracts.Lifecycle;

public class MockClentLifecycle implements Lifecycle {

    public StringBuilder events;

    public MockClentLifecycle() {
        this.events = new StringBuilder();
    }
    @Override
    public synchronized void connected(InstanceClient client) {
        events.append("C");

    }

    @Override
    public synchronized void disconnected(InstanceClient client) {
        events.append("D");
    }
}

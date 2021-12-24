package org.adamalang.grpc.mocks;

import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.contracts.ClientLifecycle;

public class MockClentLifecycle implements ClientLifecycle {

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

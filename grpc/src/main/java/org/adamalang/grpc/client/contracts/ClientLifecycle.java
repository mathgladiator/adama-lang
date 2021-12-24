package org.adamalang.grpc.client.contracts;

import org.adamalang.grpc.client.InstanceClient;

public interface ClientLifecycle {
    public void connected(InstanceClient client);

    public void disconnected(InstanceClient client);
}

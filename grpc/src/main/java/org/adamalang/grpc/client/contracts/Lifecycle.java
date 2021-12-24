package org.adamalang.grpc.client.contracts;

import org.adamalang.grpc.client.InstanceClient;

/** a persistent connection is either connected or not */
public interface Lifecycle {
    /** the given client is connected and should be good to use */
    public void connected(InstanceClient client);

    /** the given client is disconnected and not good to use */
    public void disconnected(InstanceClient client);
}

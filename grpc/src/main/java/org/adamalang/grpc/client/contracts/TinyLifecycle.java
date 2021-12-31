package org.adamalang.grpc.client.contracts;

import org.adamalang.grpc.client.InstanceClient;

public interface TinyLifecycle {
  public void connected(InstanceClient client, Runnable cancel);

  public void disconnect();
}

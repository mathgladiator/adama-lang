package org.adamalang.grpc.client.contracts;

import org.adamalang.grpc.client.InstanceClient;

public interface FoundInstanceCallback {
  public void found(InstanceClient client);

  public void nope();
}

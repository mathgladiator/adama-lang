package org.adamalang.grpc.client.contracts;

import org.adamalang.grpc.client.InstanceClient;

public interface FoundInstanceCallback {
  void found(InstanceClient client);

  void nope();
}

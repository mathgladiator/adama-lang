package org.adamalang.grpc.client;

import org.adamalang.grpc.client.contracts.FoundInstanceCallback;
import org.adamalang.runtime.contracts.Key;

import java.util.function.Consumer;

public interface InstanceClientFinder {
  void find(String space, String key, FoundInstanceCallback callback);
}

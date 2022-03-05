package org.adamalang.net.client.contracts;

import java.util.Collection;

public interface RoutingTarget {
  void integrate(String target, Collection<String> spaces);
}

package org.adamalang.grpc.client.contracts;

import java.util.Set;

/** events for space routing */
public interface SpaceTrackingEvents {
  /** a request created the new to track a space */
  void gainInterestInSpace(String space);

  /** the targets within the routing table for a space changed */
  void shareTargetsFor(String space, Set<String> targets);

  /** we lost interest in a space */
  void lostInterestInSpace(String space);
}

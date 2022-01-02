package org.adamalang.grpc.client.contracts;

import java.util.Set;

/** events for space routing */
public interface SpaceTrackingEvents {
  /** a request created the new to track a space */
  public void gainInterestInSpace(String space);

  /** the targets within the routing table for a space changed */
  public void shareTargetsFor(String space, Set<String> targets);

  /** we lost interest in a space */
  public void lostInterestInSpace(String space);
}

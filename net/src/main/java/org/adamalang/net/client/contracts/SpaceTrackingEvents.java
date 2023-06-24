/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.contracts;

import java.util.Set;

/** events for space routing */
public interface SpaceTrackingEvents {
  /** a request created the new to track a space */
  void gainInterestInSpace(String space);

  /** the targets within the routing table for a space changed */
  void shareTargetsFor(String space, Set<String> targets);

  /** we lost interest in a space */
  void lostInterestInSpace(String space);

  public static final SpaceTrackingEvents NoOp = new SpaceTrackingEvents() {
    @Override
    public void gainInterestInSpace(String space) {
    }

    @Override
    public void shareTargetsFor(String space, Set<String> targets) {
    }

    @Override
    public void lostInterestInSpace(String space) {
    }
  };
}

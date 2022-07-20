/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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

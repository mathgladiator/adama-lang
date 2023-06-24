/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.contracts;

import org.junit.Test;

import java.util.Collections;

public class SpaceTrackingEventsTests {
  @Test
  public void coverage() {
    SpaceTrackingEvents.NoOp.gainInterestInSpace("space");
    SpaceTrackingEvents.NoOp.lostInterestInSpace("space");
    SpaceTrackingEvents.NoOp.shareTargetsFor("space", Collections.singleton("target"));
  }
}

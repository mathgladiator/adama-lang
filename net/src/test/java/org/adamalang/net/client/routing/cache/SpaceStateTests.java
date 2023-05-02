/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.routing.cache;

import org.adamalang.net.client.routing.cache.SpaceState;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class SpaceStateTests {
  @Test
  public void flow() {
    SpaceState state = new SpaceState();
    state.add("x");
    Assert.assertTrue(state.list().contains("x"));
    Assert.assertTrue(state.subtract("x"));
    Assert.assertNull(state.pick("key"));
    state.add("y");
    state.add("z");
    state.add("t");
    TreeSet<String> targets = state.list();
    Assert.assertTrue(targets.contains("y"));
    Assert.assertTrue(targets.contains("z"));
    Assert.assertTrue(targets.contains("t"));
    Assert.assertFalse(targets.contains("x"));
  }
}

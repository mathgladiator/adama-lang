/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.gossip;

import org.adamalang.common.gossip.EngineRole;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class EngineRoleTests {
  @Test
  public void basis() {
    Random jitter = new Random();
    for (int k = 0; k < 1000; k++) {
      Assert.assertTrue(EngineRole.computeWait(jitter, EngineRole.Node) < 301);
      Assert.assertTrue(EngineRole.computeWait(jitter, EngineRole.SuperNode) < 15*5+1);
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.rate;

import org.adamalang.common.gossip.MockTime;
import org.junit.Assert;
import org.junit.Test;

public class DelayGateTests {
  @Test
  public void coverage() {
    MockTime time = new MockTime();
    time.currentTime = 10000;
    DelayGate gate = new DelayGate(time, 1000);
    Assert.assertTrue(gate.test());
    Assert.assertFalse(gate.test());
    time.currentTime += 500;
    Assert.assertFalse(gate.test());
    time.currentTime += 499;
    Assert.assertFalse(gate.test());
    time.currentTime += 1;
    Assert.assertTrue(gate.test());
    time.currentTime += 999;
    Assert.assertFalse(gate.test());
    time.currentTime += 1;
    Assert.assertTrue(gate.test());
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.support.testgen;

import org.junit.Assert;
import org.junit.Test;

public class PhaseRunTests {
  @Test
  public void disconnect() {
    PhaseRun.wrap((x) -> {}).disconnect();
  }

  @Test
  public void mustBeTrueCoverage() {
    PhaseRun.mustBeTrue(true, "x");
    try {
      PhaseRun.mustBeTrue(false, "xyz");
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("xyz", re.getMessage());
    }
  }
}

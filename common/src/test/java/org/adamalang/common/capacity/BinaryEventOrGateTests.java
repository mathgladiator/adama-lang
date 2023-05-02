/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.capacity;

import org.junit.Assert;
import org.junit.Test;

public class BinaryEventOrGateTests {
  @Test
  public void flow() {
    StringBuilder sb = new StringBuilder();
    BinaryEventOrGate gate = new BinaryEventOrGate((b) -> sb.append(b ? "START" : "STOP"));
    gate.a(true);
    gate.b(true);
    gate.a(false);
    gate.b(false);
    gate.a(true);
    gate.b(true);
    gate.a(false);
    gate.b(false);
    Assert.assertEquals("STARTSTOPSTARTSTOP", sb.toString());
  }
}

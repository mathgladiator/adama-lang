/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.ops;

import org.adamalang.TestFrontEnd;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

public class CapacityAgentTests {
  @Test
  public void flow() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      // empty test cases
      fe.capacityAgent.deliverAdamaHosts(Collections.emptyList());
      fe.capacityAgent.heat("target", 0.5, .2);
      fe.capacityAgent.deliverMeteringRecords(new ArrayList<>());
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.routing.cache;

import org.adamalang.common.SimpleExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class RoutingTableTargetTests {
  @Test
  public void flow() throws Exception {
    RoutingTableTarget router = new RoutingTableTarget(SimpleExecutor.NOW);
    router.integrate("t1", Collections.singleton("space"));
    router.integrate("t2", Collections.singleton("space"));
    router.integrate("t3", Collections.singleton("a"));

    for (int k = 0; k < 1000; k++) {
      Assert.assertEquals("t3", router.table.get("a", "x-" + k));
    }
    Assert.assertEquals("t1", router.table.get("space", "a"));
    Assert.assertEquals("t2", router.table.get("space", "b"));
    Assert.assertEquals("t1", router.table.get("space", "c"));
    Assert.assertEquals("t2", router.table.get("space", "d"));

    router.remove("t1");

    Assert.assertEquals("t2", router.table.get("space", "a"));
    Assert.assertEquals("t2", router.table.get("space", "b"));
    Assert.assertEquals("t2", router.table.get("space", "c"));
    Assert.assertEquals("t2", router.table.get("space", "d"));
  }
}

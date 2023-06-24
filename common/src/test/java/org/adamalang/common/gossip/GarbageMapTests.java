/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.gossip;

import org.adamalang.common.gossip.Constants;
import org.adamalang.common.gossip.GarbageMap;
import org.junit.Assert;
import org.junit.Test;

public class GarbageMapTests {
  @Test
  public void trim() {
    GarbageMap<String> map = new GarbageMap<>(10);
    for (int k = 0; k < 100; k++) {
      map.put("k+" + k, "v:" + k, 0);
    }
    Assert.assertEquals(10, map.size());
  }

  @Test
  public void flow() {
    GarbageMap<String> map = new GarbageMap<>(10);
    Assert.assertEquals(0, map.keys().size());
    map.put("x", "f(x)", 0);
    Assert.assertEquals(1, map.keys().size());
    Assert.assertEquals("f(x)", map.get("x"));
    Assert.assertEquals(1, map.size());
    Assert.assertEquals("f(x)", map.remove("x"));
    Assert.assertEquals(0, map.size());
    map.put("x", "f(x)", 0);
    Assert.assertEquals(0, map.gc(0));
    Assert.assertEquals(1, map.size());
    Assert.assertEquals("f(x)", map.get("x"));
    Assert.assertEquals(0, map.gc(Constants.MILLISECONDS_TO_SIT_IN_GARBAGE_MAP - 1));
    Assert.assertEquals(1, map.keys().size());
    Assert.assertEquals("f(x)", map.get("x"));
    Assert.assertEquals(0, map.gc(Constants.MILLISECONDS_TO_SIT_IN_GARBAGE_MAP));
    Assert.assertEquals(1, map.keys().size());
    Assert.assertEquals("f(x)", map.get("x"));
    Assert.assertEquals(1, map.gc(Constants.MILLISECONDS_TO_SIT_IN_GARBAGE_MAP + 1));
    Assert.assertEquals(0, map.keys().size());
    Assert.assertNull(map.get("x"));
    Assert.assertNull(map.remove("x"));
  }
}

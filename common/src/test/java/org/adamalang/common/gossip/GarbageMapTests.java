/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.common.gossip;

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

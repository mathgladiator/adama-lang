/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class NtMapTests {
  @Test
  public void flow() {
    final var map = new NtMap<Integer, Integer>();
    final var ptr = map.lookup(42);
    Assert.assertEquals(0, map.size());
    ptr.set(100);
    Assert.assertEquals(1, map.size());
    final var it = map.iterator();
    Assert.assertTrue(it.hasNext());
    Assert.assertEquals(100, (int) it.next().value);
    Assert.assertFalse(it.hasNext());
    final var copy = new NtMap<>(map);
    Assert.assertEquals(1, copy.size());
    final var copy2 = new NtMap<Integer, Integer>();
    copy2.set(copy);
    Assert.assertEquals(1, copy2.size());
    final var copy3 = new NtMap<Integer, Integer>();
    copy3.insert(copy);
    Assert.assertEquals(1, copy3.size());
    ptr.delete();
    Assert.assertEquals(0, map.size());
    map.put(1000, 40);
    Assert.assertEquals(1, map.size());
    Assert.assertEquals(40, (int) map.lookup(1000).get());
    map.entries();
    Assert.assertEquals(40, (int) map.remove(1000).getOrDefaultTo(-100));
  }

  @Test
  public void ordering() {
    final var map = new NtMap<Integer, Integer>();
    Assert.assertFalse(map.min().has());
    Assert.assertFalse(map.max().has());
    map.put(1, 2);
    map.put(5, 6);
    map.put(3, 4);
    Assert.assertEquals(2, (int) map.min().get().value);
    Assert.assertEquals(6, (int) map.max().get().value);
    Iterator<NtPair<Integer, Integer>> it = map.iterator();
    var f1 = it.next();
    var f2 = it.next();
    var f3 = it.next();
    Assert.assertFalse(it.hasNext());
    Assert.assertEquals(1, (int) f1.key);
    Assert.assertEquals(3, (int) f2.key);
    Assert.assertEquals(5, (int) f3.key);
  }
}

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
package org.adamalang.runtime.natives.lists;

import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class EmptyNtListTests {
  @Test
  public void coverage() {
    final var list = new EmptyNtList<String>();
    list.get().get();
    Assert.assertEquals(0, list.size());
    list.orderBy(true, (x, y) -> 0);
    list.skip(true, 0);
    list.limit(false, 100);
    list.where(true, null);
    list.shuffle(true, null);
    Assert.assertEquals(0, list.toArray(n -> new String[n]).length);
    Assert.assertFalse(list.lookup(10).has());
    list.transform(String::length);
    list.__delete();
    Assert.assertFalse(list.iterator().hasNext());
    Assert.assertNull(list.iterator().next());
    Assert.assertEquals(0, list.reduce(String::length, l -> l.lookup(0)).size());
    list.map(zzz -> {});
    Assert.assertFalse(list.lookup(134).has());
    Assert.assertFalse(list.lookup(new NtMaybe<>()).has());
    Assert.assertFalse(list.lookup(new NtMaybe<>(42)).has());
    list.mapFunction((x) -> x.length());
    Assert.assertEquals(0, list.unique(ListUniqueMode.Last, (x) -> x).size());
    Assert.assertEquals(0, list.unique(ListUniqueMode.First, (x) -> x).size());
  }
}

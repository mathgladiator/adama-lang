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

import org.adamalang.runtime.contracts.IndexQuerySet;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ArrayNtListTests {
  @Test
  public void empty() {
    final var list = new ArrayNtList<String>(new ArrayList<>());
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
    Assert.assertFalse(list.lookup(134).has());
    Assert.assertFalse(list.lookup(new NtMaybe<>()).has());
    Assert.assertFalse(list.lookup(new NtMaybe<>(42)).has());
  }

  @Test
  public void map() {
    final var s = new ArrayList<String>();
    s.add("x");
    s.add("yx");
    s.add("zxx");
    final var list = new ArrayNtList<>(s);
    NtList<Integer> mapped = list.mapFunction((x) -> x.length());
    Assert.assertEquals(2, (int) mapped.lookup(1).get());
  }

  @Test
  public void items() {
    final var s = new ArrayList<String>();
    s.add("x");
    s.add("y");
    s.add("z");
    final var list = new ArrayNtList<>(s);
    list.get().get().get();
    Assert.assertEquals(3, list.size());
    list.shuffle(true, new Random(0));
    Assert.assertEquals("y", s.get(0));
    Assert.assertEquals("z", s.get(1));
    Assert.assertEquals("x", s.get(2));
    final var filtered =
        list.where(
            true,
            new WhereClause<String>() {
              @Override
              public int[] getIndices() {
                return new int[0];
              }

              @Override
              public Integer getPrimaryKey() {
                return null;
              }

              @Override
              public void scopeByIndicies(final IndexQuerySet __set) {}

              @Override
              public boolean test(final String item) {
                return item.equals("x");
              }
            });
    Assert.assertEquals(1, filtered.size());
    Assert.assertEquals(2, list.skip(true, 1).size());
    Assert.assertEquals(2, list.limit(true, 2).size());
    Assert.assertEquals("y", list.lookup(0).get());
    Assert.assertFalse(list.lookup(40).has());
    list.orderBy(true, String::compareTo);
    Assert.assertEquals("x", s.get(0));
    Assert.assertEquals("y", s.get(1));
    Assert.assertEquals("z", s.get(2));
    list.__delete(); // do nothing
    final var result = list.transform(String::length);
    Assert.assertEquals(3, result.size());
  }

  @Test
  public void reduce() {
    final var s = new ArrayList<String>();
    s.add("xxx");
    s.add("yy");
    s.add("z");
    s.add("xyz");
    new AtomicInteger(0);
    final var list = new ArrayNtList<>(s);
    final var r = list.reduce(String::length, l -> l.lookup(0).get());
    Assert.assertEquals(3, r.size());
    Assert.assertEquals("xxx", r.lookup(3).get());
    Assert.assertEquals("yy", r.lookup(2).get());
    Assert.assertEquals("z", r.lookup(1).get());
    final var counter = new AtomicInteger(0);
    list.map(
        zzz -> {
          counter.incrementAndGet();
        });
    Assert.assertEquals(4, counter.get());
  }

  @Test
  public void test_records() {
    final var M = new ArrayList<MockRecord>();
    final var m = new MockRecord(null);
    M.add(m);
    final var list = new ArrayNtList<>(M);
    list.__delete();
    Assert.assertTrue(m.__isDying());
  }
}

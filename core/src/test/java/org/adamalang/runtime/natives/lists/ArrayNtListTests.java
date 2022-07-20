/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives.lists;

import org.adamalang.runtime.contracts.IndexQuerySet;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.mocks.MockRecord;
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

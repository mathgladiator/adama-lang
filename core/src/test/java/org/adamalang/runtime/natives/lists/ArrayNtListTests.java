/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives.lists;

import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRecordBridge;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.contracts.IndexQuerySet;
import org.adamalang.runtime.bridges.NativeBridge;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ArrayNtListTests {
    @Test
    public void empty() {
        ArrayNtList<String> list = new ArrayNtList<>(new ArrayList<>(), NativeBridge.STRING_NATIVE_SUPPORT);
        Assert.assertEquals(0, list.size());
        list.orderBy(true, (x, y) -> 0);
        list.skipAndLimit(true, 0, 100);
        list.where(true, null);
        list.shuffle(true, null);
        Assert.assertEquals(0, list.toArray().length);
        Assert.assertFalse(list.lookup(10).has());
        list.transform((x) -> x.length(), NativeBridge.INTEGER_NATIVE_SUPPORT);
        list.__delete();
        Assert.assertFalse(list.iterator().hasNext());
    }

    @Test
    public void items() {
        ArrayList<String> s = new ArrayList<>();
        s.add("x");
        s.add("y");
        s.add("z");
        ArrayNtList<String> list = new ArrayNtList<>(s, NativeBridge.STRING_NATIVE_SUPPORT);
        list.get().get().get();
        Assert.assertEquals(3, list.size());

        list.shuffle(true, new Random(0));
        Assert.assertEquals("y", s.get(0));
        Assert.assertEquals("z", s.get(1));
        Assert.assertEquals("x", s.get(2));
        NtList<String> filtered = list.where(true, new WhereClause<String>() {
            @Override
            public int[] getIndices() {
                return new int[0];
            }

            @Override
            public Integer getPrimaryKey() {
                return null;
            }

            @Override
            public void scopeByIndicies(IndexQuerySet __set) {
            }

            @Override
            public boolean test(String item) {
                return item.equals("x");
            }
        });
        Assert.assertEquals(1, filtered.size());
        Assert.assertEquals(2, list.skipAndLimit(true, 1, 50).size());
        Assert.assertEquals(2, list.skipAndLimit(true, 0, 2).size());
        Assert.assertEquals("y", list.lookup(0).get());
        Assert.assertFalse(list.lookup(40).has());
        list.orderBy(true, String::compareTo);
        Assert.assertEquals("x", s.get(0));
        Assert.assertEquals("y", s.get(1));
        Assert.assertEquals("z", s.get(2));
        list.__delete(); // do nothing
        NtList<Integer> result = list.transform((x) -> x.length(), NativeBridge.INTEGER_NATIVE_SUPPORT);
        Assert.assertEquals(3, result.size());
    }

    @Test
    public void reduce() {
        ArrayList<String> s = new ArrayList<>();
        s.add("xxx");
        s.add("yy");
        s.add("z");
        s.add("xyz");
        AtomicInteger inv = new AtomicInteger(0);
        ArrayNtList<String> list = new ArrayNtList<>(s, NativeBridge.STRING_NATIVE_SUPPORT);
        NtMap<Integer, String> r = list.reduce((x) -> x.length(), (l) -> l.lookup(0).get());
        Assert.assertEquals(3, r.size());
        Assert.assertEquals("xxx", r.lookup(3).get());
        Assert.assertEquals("yy", r.lookup(2).get());
        Assert.assertEquals("z", r.lookup(1).get());
        AtomicInteger counter = new AtomicInteger(0);
        list.map((zzz) -> { counter.incrementAndGet(); });
        Assert.assertEquals(4, counter.get());
    }

    @Test
    public void test_records() {
        ArrayList<MockRecord> M = new ArrayList<>();
        MockRecord m = new MockRecord(Utility.createObjectNode(), null);
        M.add(m);
        ArrayNtList<MockRecord> list = new ArrayNtList<>(M, new MockRecordBridge());
        list.__delete();
        Assert.assertTrue(m.__isDying());
    }
}

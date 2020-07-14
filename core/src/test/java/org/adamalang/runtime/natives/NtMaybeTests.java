/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.mocks.MockCanGetAndSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class NtMaybeTests {
    @Test
    public void set_chain() {
        MockCanGetAndSet<Integer> cgs = new MockCanGetAndSet<>();
        NtMaybe<Integer> maybe = new NtMaybe<Integer>().withAssignChain(cgs);
        Assert.assertFalse(maybe.has());
        maybe.set(123);
        Assert.assertEquals(123, (int) cgs.get());
        maybe.set(new NtMaybe<>(52));
        Assert.assertEquals(52, (int) cgs.get());
    }

    @Test
    public void flow() {
        NtMaybe<Integer> maybe = new NtMaybe<>();
        Assert.assertFalse(maybe.has());
        maybe.set(123);
        Assert.assertTrue(maybe.has());
        Assert.assertEquals(123, (int) maybe.get());
        NtMaybe<Integer> copy = new NtMaybe<>(maybe);
        Assert.assertTrue(copy.has());
        Assert.assertEquals(123, (int) copy.get());
        Assert.assertTrue(maybe == maybe.resolve());
    }

    @Test
    public void with_value() {
        NtMaybe<Integer> maybe = new NtMaybe<>(123);
        NtMaybe<Integer> copy = new NtMaybe<>(maybe);
        Assert.assertTrue(copy.has());
        Assert.assertEquals(123, (int) copy.get());
        copy.delete();
        Assert.assertFalse(copy.has());
        Assert.assertTrue(maybe.has());
        Assert.assertEquals(123, (int) maybe.get());
    }

    @Test
    public void via_set() {
        NtMaybe<Integer> maybe = new NtMaybe<>(123);
        NtMaybe<Integer> copy = new NtMaybe<>();
        copy.set(maybe);
        Assert.assertEquals(123, (int) copy.get());
        copy.delete();
        Assert.assertFalse(copy.has());
        Assert.assertTrue(maybe.has());
        Assert.assertEquals(123, (int) maybe.get());
    }

    @Test
    public void sort() {
        ArrayList<NtMaybe<Integer>> ints = new ArrayList<>();
        ints.add(new NtMaybe<>());
        ints.add(new NtMaybe<>(10000));
        ints.add(new NtMaybe<>());
        ints.add(new NtMaybe<>(123));
        ints.add(new NtMaybe<>());
        ints.add(new NtMaybe<>(1000));
        ints.add(new NtMaybe<>());
        ints.add(new NtMaybe<>(12));
        ints.add(new NtMaybe<>());
        ints.add(new NtMaybe<>());
        ints.add(new NtMaybe<>());
        ints.sort((a, b) -> a.compareValues(b, (x, y) -> x.compareTo(y)));
        Assert.assertEquals(12, (int) ints.get(0).get());
        Assert.assertEquals(123, (int) ints.get(1).get());
        Assert.assertEquals(1000, (int) ints.get(2).get());
        Assert.assertEquals(10000, (int) ints.get(3).get());
        Assert.assertEquals(null, ints.get(4).get());
    }
}
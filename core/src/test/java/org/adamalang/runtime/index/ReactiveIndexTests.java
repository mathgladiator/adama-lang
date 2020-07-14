/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.index;

import org.adamalang.runtime.mocks.MockRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class ReactiveIndexTests {
    @Test
    public void flow() {
        TreeSet<MockRecord> unknowns = new TreeSet<>();
        ReactiveIndex<MockRecord> index = new ReactiveIndex<>(unknowns);
        Assert.assertNull(index.of(42));
        index.add(42, MockRecord.make(1));
        Assert.assertEquals(1, index.of(42).size());
        index.add(42, MockRecord.make(12));
        Assert.assertEquals(2, index.of(42).size());
        Assert.assertFalse(unknowns.contains(MockRecord.make(12)));
        Assert.assertFalse(unknowns.contains(MockRecord.make(1)));
        index.remove(42, MockRecord.make(12));
        Assert.assertTrue(unknowns.contains(MockRecord.make(12)));
        Assert.assertEquals(1, index.of(42).size());
        index.remove(42, MockRecord.make(1));
        Assert.assertNull(index.of(42));
        Assert.assertTrue(unknowns.contains(MockRecord.make(1)));
    }

    @Test
    public void del() {
        TreeSet<MockRecord> unknowns = new TreeSet<>();
        ReactiveIndex<MockRecord> index = new ReactiveIndex<>(unknowns);
        unknowns.add(MockRecord.make(123));
        Assert.assertEquals(1, unknowns.size());
        index.delete(MockRecord.make(123));
        Assert.assertEquals(0, unknowns.size());
    }
}

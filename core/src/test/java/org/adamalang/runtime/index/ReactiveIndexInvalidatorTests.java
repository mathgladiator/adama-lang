/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.index;

import org.adamalang.runtime.mocks.MockRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class ReactiveIndexInvalidatorTests {
    @Test
    public void flow1() {
        TreeSet<MockRecord> unknowns = new TreeSet<>();
        ReactiveIndex<MockRecord> index = new ReactiveIndex<>(unknowns);
        unknowns.add(MockRecord.make(123));
        ReactiveIndexInvalidator<MockRecord> inv = new ReactiveIndexInvalidator<>(index,  MockRecord.make(123)) {
            @Override
            public int pullValue() {
                return 42;
            }
        };

        inv.reindex();
        unknowns.clear();
        Assert.assertEquals(0, unknowns.size());
        inv.__raiseInvalid();
        Assert.assertEquals(1, unknowns.size());
        inv.deindex();
        Assert.assertEquals(0, unknowns.size());
    }

    @Test
    public void flow2() {
        TreeSet<MockRecord> unknowns = new TreeSet<>();
        ReactiveIndex<MockRecord> index = new ReactiveIndex<>(unknowns);
        unknowns.add(MockRecord.make(123));
        ReactiveIndexInvalidator<MockRecord> inv = new ReactiveIndexInvalidator<>(index,  MockRecord.make(123)) {
            @Override
            public int pullValue() {
                return 42;
            }
        };
        inv.reindex();
        unknowns.clear();
        inv.deindex();
        Assert.assertEquals(0, unknowns.size());
    }

    @Test
    public void flow3() {
        TreeSet<MockRecord> unknowns = new TreeSet<>();
        ReactiveIndex<MockRecord> index = new ReactiveIndex<>(unknowns);
        unknowns.add(MockRecord.make(123));
        ReactiveIndexInvalidator<MockRecord> inv = new ReactiveIndexInvalidator<>(index,  MockRecord.make(123)) {
            @Override
            public int pullValue() {
                return 42;
            }
        };
        Assert.assertEquals(1, unknowns.size());
        inv.deindex();
        Assert.assertEquals(0, unknowns.size());
    }
}

/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;

public class RxMaybeTests {
    private static String commit(RxMaybe<RxInt32> mi) {
        ObjectNode node = Utility.createObjectNode();
        mi.__commit("v", node);
        return node.toString();
    }
    @Test
    public void make() {
        MockRxParent parent = new MockRxParent();
        RxMaybe<RxInt32> mi = new RxMaybe<>(parent, (p) -> new RxInt32(p, 42));
        Assert.assertFalse(mi.has());
        mi.make();
        Assert.assertTrue(mi.has());
    }
    @Test
    public void commit_flow() {
        MockRxParent parent = new MockRxParent();
        RxMaybe<RxInt32> mi = new RxMaybe<>(parent, (p) -> new RxInt32(p, 42));
        Assert.assertFalse(mi.has());
        Assert.assertEquals("{}", commit(mi));
        parent.assertDirtyCount(0);
        mi.make();
        parent.assertDirtyCount(1);
        Assert.assertEquals("{\"v\":42}", commit(mi));
        mi.delete();
        parent.assertDirtyCount(2);
        Assert.assertEquals("{\"v\":null}", commit(mi));
        Assert.assertEquals("{}", commit(mi));
        mi.make().set(50);
        mi.delete();
        parent.assertDirtyCount(5);
        Assert.assertEquals("{\"v\":null}", commit(mi));
        mi.make().set(50);
        parent.assertDirtyCount(7);
        Assert.assertEquals("{\"v\":50}", commit(mi));
        mi.make().set(5000);
    }
    @Test
    public void revert_delete_flow() {
        RxMaybe<RxInt32> mi = new RxMaybe<>(null, (p) -> new RxInt32(p, 42));
        MockRxChild child = new MockRxChild();
        mi.__subscribe(child);
        Assert.assertFalse(mi.has());
        Assert.assertEquals("{}", commit(mi));
        mi.make();
        child.assertInvalidateCount(2);
        Assert.assertEquals("{\"v\":42}", commit(mi));
        mi.delete();
        child.assertInvalidateCount(3);
        Assert.assertFalse(mi.has());
        mi.__revert();
        Assert.assertTrue(mi.has());
        Assert.assertEquals("{}", commit(mi));
        child.assertInvalidateCount(4);
    }
    @Test
    public void revert_data_flow() {
        RxMaybe<RxInt32> mi = new RxMaybe<>(null, (p) -> new RxInt32(p, 42));
        MockRxChild child = new MockRxChild();
        mi.__subscribe(child);
        Assert.assertFalse(mi.has());
        Assert.assertEquals("{}", commit(mi));
        mi.make();
        child.assertInvalidateCount(2);
        Assert.assertEquals("{\"v\":42}", commit(mi));
        mi.make().set(5000);
        child.assertInvalidateCount(4);
        Assert.assertTrue(mi.has());
        mi.__revert();
        child.assertInvalidateCount(6);
        Assert.assertEquals("{}", commit(mi));
    }
    @Test
    public void revert_creation_flow() {
        RxMaybe<RxInt32> mi = new RxMaybe<>(null, (p) -> new RxInt32(p, 42));
        MockRxChild child = new MockRxChild();
        mi.__subscribe(child);
        mi.make().set(50);
        mi.__revert();
        child.assertInvalidateCount(4);
        Assert.assertEquals("{}", commit(mi));
    }
    @Test
    public void compare_a() {
        Comparator<RxInt32> cmp = (a, b) -> a.compareTo(b);
        RxMaybe<RxInt32> m1 = new RxMaybe<>(null, (p) -> new RxInt32(p, 1));
        RxMaybe<RxInt32> m2 = new RxMaybe<>(null, (p) -> new RxInt32(p, 1));
        Assert.assertEquals(0, m1.compareValues(m2, cmp));
        m2.make();
        Assert.assertEquals(1, m1.compareValues(m2, cmp));
        m1.make();
        Assert.assertEquals(0, m1.compareValues(m2, cmp));
    }
    @Test
    public void compare_b() {
        Comparator<RxInt32> cmp = (a, b) -> a.compareTo(b);
        RxMaybe<RxInt32> m1 = new RxMaybe<>(null, (p) -> new RxInt32(p, 1));
        RxMaybe<RxInt32> m2 = new RxMaybe<>(null, (p) -> new RxInt32(p, 1));
        Assert.assertEquals(0, m1.compareValues(m2, cmp));
        m1.make();
        Assert.assertEquals(-1, m1.compareValues(m2, cmp));
        m2.make();
        Assert.assertEquals(0, m1.compareValues(m2, cmp));
    }
    @Test
    public void compare_c() {
        Comparator<RxInt32> cmp = (a, b) -> a.compareTo(b);
        RxMaybe<RxInt32> m1 = new RxMaybe<>(null, (p) -> new RxInt32(p, 1));
        RxMaybe<RxInt32> m2 = new RxMaybe<>(null, (p) -> new RxInt32(p, 2));
        m1.make();
        m2.make();
        Assert.assertEquals(-1, m1.compareValues(m2, cmp));
    }
    @Test
    public void compare_d() {
        Comparator<RxInt32> cmp = (a, b) -> a.compareTo(b);
        RxMaybe<RxInt32> m1 = new RxMaybe<>(null, (p) -> new RxInt32(p, 2));
        RxMaybe<RxInt32> m2 = new RxMaybe<>(null, (p) -> new RxInt32(p, 1));
        m1.make();
        m2.make();
        Assert.assertEquals(1, m1.compareValues(m2, cmp));
    }
    @Test
    public void to_native() {
        RxMaybe<RxInt32> m1 = new RxMaybe<>(null, (p) -> new RxInt32(p, 2));
        NtMaybe<Integer> n1 = m1.get();
        Assert.assertFalse(n1.has());
        m1.make();
        Assert.assertFalse(n1.has());
        n1 = m1.get();
        Assert.assertTrue(n1.has());
        Assert.assertEquals(2, (int) n1.get());
        Assert.assertTrue(m1.has());
        n1.delete();
        Assert.assertFalse(m1.has());
    }
    @Test
    public void copy() {
        RxMaybe<RxInt32> from = new RxMaybe<>(null, (p) -> new RxInt32(p, 42));
        RxMaybe<RxInt32> to = new RxMaybe<>(null, (p) -> new RxInt32(p, 42));
        to.set(from.get());
        Assert.assertFalse(to.has());
        from.make();
        to.set(from.get());
        Assert.assertTrue(to.has());
        Assert.assertEquals(42, (int) to.get().get());
    }
    @Test
    public void oddball() {
        MockRecord record = new MockRecord(Utility.createObjectNode(), null);
        RxMaybe<MockRecord> x = new RxMaybe<>(null, (p) -> record);
        Assert.assertFalse(x.get().has());
        x.make();
        Assert.assertTrue(x.get().has());
        Assert.assertTrue(record == x.get().get());
    }
}

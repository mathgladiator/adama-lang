/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class RxInt64Tests {
    @Test
    public void dirty_and_commit() {
        MockRxParent parent = new MockRxParent();
        RxInt64 l = new RxInt64(parent, 42);
        Assert.assertEquals(42, (long) l.get());
        l.set(50L);
        parent.assertDirtyCount(1);
        l.set(60);
        parent.assertDirtyCount(2);
        ObjectNode output = Utility.createObjectNode();
        l.__commit("v", output);
        Assert.assertEquals("{\"v\":\"60\"}", output.toString());
        ObjectNode outputAgain = Utility.createObjectNode();
        l.__commit("v2", output);
        Assert.assertEquals("{}", outputAgain.toString());
        l.set(12354124314124L);
        l.__commit("v", output);
        Assert.assertEquals("{\"v\":\"12354124314124\"}", output.toString());
    }
    @Test
    public void invalidate_and_revert() {
        RxInt64 l = new RxInt64(null, 42);
        MockRxChild child = new MockRxChild();
        l.__subscribe(child);
        l.set(50);
        child.assertInvalidateCount(1);
        l.set(50);
        child.assertInvalidateCount(1);
        l.set(55);
        child.assertInvalidateCount(2);
        Assert.assertEquals(55, l.getIndexValue());
        l.__revert();
        child.assertInvalidateCount(3);
        Assert.assertEquals(42, (long) l.get());
        l.__revert();
        child.assertInvalidateCount(3);
        l.__cancelAllSubscriptions();
        l.set(100);
        child.assertInvalidateCount(3);
    }
    @Test
    public void compare() {
        RxInt64 l1 = new RxInt64(null, 1);
        RxInt64 l2 = new RxInt64(null, 2);
        Assert.assertEquals(-1, l1.compareTo(l2));
        Assert.assertEquals(1, l2.compareTo(l1));
    }
    @Test
    public void ops() {
        RxInt64 l = new RxInt64(null, 1);
        l.bumpUpPre();
        Assert.assertEquals(2, (long) l.get());
        l.bumpUpPost();
        Assert.assertEquals(3, (long) l.get());
        l.bumpDownPre();
        Assert.assertEquals(2, (long) l.get());
        l.bumpDownPost();
        Assert.assertEquals(1, (long) l.get());
        l.opAddTo(10);
        Assert.assertEquals(11, (long) l.get());
        l.opMultBy(2);
        Assert.assertEquals(22, (long) l.get());
        l.opSubFrom(7);
        Assert.assertEquals(15, (long) l.get());
        l.opModBy(14);
        Assert.assertEquals(1, (long) l.get());
    }
}

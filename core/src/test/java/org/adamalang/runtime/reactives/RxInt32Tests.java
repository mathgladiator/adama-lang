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

public class RxInt32Tests {
    @Test
    public void dirty_and_commit() {
        MockRxParent parent = new MockRxParent();
        RxInt32 i = new RxInt32(parent, 42);
        Assert.assertEquals(42, (int) i.get());
        i.set(50);
        parent.assertDirtyCount(1);
        i.set(60);
        parent.assertDirtyCount(2);
        ObjectNode output = Utility.createObjectNode();
        i.__commit("v", output);
        Assert.assertEquals("{\"v\":60}", output.toString());
        ObjectNode outputAgain = Utility.createObjectNode();
        i.__commit("v2", output);
        Assert.assertEquals("{}", outputAgain.toString());
    }
    @Test
    public void invalidate_and_revert() {
        RxInt32 i = new RxInt32(null, 42);
        MockRxChild child = new MockRxChild();
        i.__subscribe(child);
        i.set(50);
        child.assertInvalidateCount(1);
        i.set(50);
        child.assertInvalidateCount(1);
        i.set(55);
        child.assertInvalidateCount(2);
        Assert.assertEquals(55, i.getIndexValue());
        i.__revert();
        child.assertInvalidateCount(3);
        Assert.assertEquals(42, (int) i.get());
        i.__revert();
        child.assertInvalidateCount(3);
        i.__cancelAllSubscriptions();
        i.set(100);
        child.assertInvalidateCount(3);

    }
    @Test
    public void compare() {
        RxInt32 i1 = new RxInt32(null, 1);
        RxInt32 i2 = new RxInt32(null, 2);
        Assert.assertEquals(-1, i1.compareTo(i2));
        Assert.assertEquals(1, i2.compareTo(i1));
    }
    @Test
    public void ops() {
        RxInt32 i = new RxInt32(null, 1);
        i.bumpUpPre();
        Assert.assertEquals(2, (int) i.get());
        i.bumpUpPost();
        Assert.assertEquals(3, (int) i.get());
        i.bumpDownPre();
        Assert.assertEquals(2, (int) i.get());
        i.bumpDownPost();
        Assert.assertEquals(1, (int) i.get());
        i.opAddTo(10);
        Assert.assertEquals(11, (int) i.get());
        i.opMultBy(2);
        Assert.assertEquals(22, (int) i.get());
        i.opSubFrom(7);
        Assert.assertEquals(15, (int) i.get());
        i.opModBy(14);
        Assert.assertEquals(1, (int) i.get());
    }
}

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

public class RxDoubleTests {
    @Test
    public void dirty_and_commit() {
        MockRxParent parent = new MockRxParent();
        RxDouble d = new RxDouble(parent, 42);
        Assert.assertEquals(42, d.get(), 0.1);
        d.set(50);
        parent.assertDirtyCount(1);
        d.set(6.28);
        parent.assertDirtyCount(2);
        ObjectNode output = Utility.createObjectNode();
        d.__commit("v", output);
        Assert.assertEquals("{\"v\":6.28}", output.toString());
        ObjectNode outputAgain = Utility.createObjectNode();
        d.__commit("v2", output);
        Assert.assertEquals("{}", outputAgain.toString());
    }
    @Test
    public void invalidate_and_revert() {
        RxDouble d = new RxDouble(null, 42);
        MockRxChild child = new MockRxChild();
        d.__subscribe(child);
        d.set(50);
        child.assertInvalidateCount(1);
        Double dFity = 50.0;
        d.set(dFity);
        child.assertInvalidateCount(2);
        d.set(55.0);
        child.assertInvalidateCount(3);
        d.__revert();
        child.assertInvalidateCount(4);
        Assert.assertEquals(42, d.get(), 0.1);
        d.__revert();
        child.assertInvalidateCount(4);
        d.__cancelAllSubscriptions();
        d.set(100);
        child.assertInvalidateCount(4);
    }
    @Test
    public void compare() {
        RxDouble d1 = new RxDouble(null, 1);
        RxDouble d2 = new RxDouble(null, 2);
        Assert.assertEquals(-1, d1.compareTo(d2));
        Assert.assertEquals(1, d2.compareTo(d1));
    }
    @Test
    public void ops() {
        RxDouble d = new RxDouble(null, 1);
        d.bumpUpPre();
        Assert.assertEquals(2, d.get(), 0.1);
        d.bumpUpPost();
        Assert.assertEquals(3, d.get(), 0.1);
        d.bumpDownPre();
        Assert.assertEquals(2, d.get(), 0.1);
        d.bumpDownPost();
        Assert.assertEquals(1, d.get(), 0.1);
        d.opAddTo(10);
        Assert.assertEquals(11, d.get(), 0.1);
        d.opMultBy(2);
        Assert.assertEquals(22, d.get(), 0.1);
        d.opSubFrom(7);
        Assert.assertEquals(15, d.get(), 0.1);
        d.opDivBy(30.0);
        Assert.assertEquals(0.5, d.get(), 0.1);
    }

}

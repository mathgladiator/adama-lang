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

public class RxBooleanTests {
    @Test
    public void dirty_and_commit() {
        MockRxParent parent = new MockRxParent();
        RxBoolean rx = new RxBoolean(parent, false);
        Assert.assertEquals(false, rx.get());
        parent.assertDirtyCount(0);
        rx.set(true);
        parent.assertDirtyCount(1);
        Assert.assertEquals(true, rx.get());
        Assert.assertEquals(0, rx.compareTo(rx));
        rx.set(true);
        parent.assertDirtyCount(1);
        rx.set(true);
        parent.assertDirtyCount(1);
        rx.set(false);
        parent.assertDirtyCount(2);
        rx.set(true);
        parent.assertDirtyCount(3);
        ObjectNode output = Utility.createObjectNode();
        rx.__commit("v", output);
        Assert.assertEquals("{\"v\":true}", output.toString());
        ObjectNode outputAgain = Utility.createObjectNode();
        rx.__commit("v2", output);
        Assert.assertEquals("{}", outputAgain.toString());
    }

    @Test
    public void invalidate_and_revert() {
        MockRxParent parent = new MockRxParent();
        RxBoolean rx = new RxBoolean(parent, false);
        MockRxChild invalidate = new MockRxChild();
        rx.__subscribe(invalidate);
        invalidate.assertInvalidateCount(0);
        rx.set(true);
        invalidate.assertInvalidateCount(1);
        rx.set(true);
        invalidate.assertInvalidateCount(1);
        rx.__revert();
        invalidate.assertInvalidateCount(2);
        rx.__revert();
        invalidate.assertInvalidateCount(2);
        rx.__cancelAllSubscriptions();
        rx.set(true);
        invalidate.assertInvalidateCount(2);
    }

    @Test
    public void compare() {
        RxBoolean rt = new RxBoolean(null, true);
        RxBoolean rf = new RxBoolean(null, false);
        Assert.assertEquals(1, rt.compareTo(rf));
        Assert.assertEquals(-1, rf.compareTo(rt));
    }
}

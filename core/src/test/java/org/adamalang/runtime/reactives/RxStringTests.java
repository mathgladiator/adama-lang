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

public class RxStringTests {
    @Test
    public void dirty_and_commit() {
        MockRxParent parent = new MockRxParent();
        RxString s = new RxString(parent, "xxx");
        Assert.assertEquals("xxx", s.get());
        parent.assertDirtyCount(0);
        s.set("cake");
        parent.assertDirtyCount(1);
        s.set("cake");
        parent.assertDirtyCount(2);
        Assert.assertEquals("cake", s.get());
        ObjectNode output = Utility.createObjectNode();
        s.__commit("v", output);
        Assert.assertEquals("{\"v\":\"cake\"}", output.toString());
        ObjectNode outputAgain = Utility.createObjectNode();
        s.__commit("v2", output);
        Assert.assertEquals("{}", outputAgain.toString());
    }
    @Test
    public void ops() {
        MockRxParent parent = new MockRxParent();
        RxString s = new RxString(parent, "a");
        s.opAddTo(0);
        parent.assertDirtyCount(1);
        s.opAddTo(true);
        parent.assertDirtyCount(2);
        s.opAddTo(0.0);
        parent.assertDirtyCount(3);
        s.opAddTo("b");
        parent.assertDirtyCount(4);
        Assert.assertEquals("a0true0.0b", s.get());
    }
    @Test
    public void invalidate_and_revert() {
        MockRxChild child = new MockRxChild();
        RxString s = new RxString(null, "xyz");
        s.__subscribe(child);
        Assert.assertEquals("xyz", s.get());
        child.assertInvalidateCount(0);
        s.set("cake");
        child.assertInvalidateCount(1);
        s.set("cake");
        child.assertInvalidateCount(2);
        Assert.assertEquals("cake", s.get());
        s.__revert();
        Assert.assertEquals("xyz", s.get());
        child.assertInvalidateCount(3);
    }
    @Test
    public void compare() {
        RxString a = new RxString(null, "a");
        RxString b = new RxString(null, "b");
        Assert.assertEquals(-1, a.compareTo(b));
        Assert.assertEquals(1, b.compareTo(a));
    }
}

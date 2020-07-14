/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class RxClientTests {
    private static final NtClient A = new NtClient("a", "d");
    private static final NtClient B = new NtClient("b", "c");
    private static final NtClient CC = new NtClient("c", "c");

    @Test
    public void hash() {
        Assert.assertEquals(2016, new RxClient(null, NtClient.NO_ONE).getIndexValue());
        Assert.assertEquals(3107, new RxClient(null, A).getIndexValue());
        Assert.assertEquals(3137, new RxClient(null, B).getIndexValue());
        Assert.assertEquals(3168, new RxClient(null, CC).getIndexValue());
    }
    @Test
    public void dirty_and_commit() {
        MockRxParent parent = new MockRxParent();
        RxClient c = new RxClient(parent, NtClient.NO_ONE);
        parent.assertDirtyCount(0);
        c.set(A);
        parent.assertDirtyCount(1);
        c.set(A);
        parent.assertDirtyCount(1);
        ObjectNode output = Utility.createObjectNode();
        c.__commit("v", output);
        Assert.assertEquals("{\"v\":{\"agent\":\"a\",\"authority\":\"d\"}}", output.toString());
        ObjectNode outputAgain = Utility.createObjectNode();
        c.__commit("v2", output);
        Assert.assertEquals("{}", outputAgain.toString());
    }
    @Test
    public void invalidate_and_revert() {
        RxClient c = new RxClient(null, NtClient.NO_ONE);
        MockRxChild child = new MockRxChild();
        c.__subscribe(child);
        child.assertInvalidateCount(0);
        c.set(A);
        child.assertInvalidateCount(1);
        c.set(A);
        Assert.assertEquals(A, c.get());
        child.assertInvalidateCount(1);
        c.set(B);
        Assert.assertEquals(B, c.get());
        child.assertInvalidateCount(2);
        c.__revert();
        child.assertInvalidateCount(3);
        Assert.assertEquals(NtClient.NO_ONE, c.get());
        c.__cancelAllSubscriptions();
        c.set(B);
        child.assertInvalidateCount(3);
    }
    @Test
    public void compare() {
        RxClient a = new RxClient(null, A);
        RxClient b = new RxClient(null, B);
        RxClient c = new RxClient(null, CC);
        Assert.assertEquals(1, a.compareTo(b));
        Assert.assertEquals( -1, b.compareTo(a));
        Assert.assertEquals(1, a.compareTo(c));
        Assert.assertEquals( -1, b.compareTo(c));
        Assert.assertEquals(-1, c.compareTo(a));
        Assert.assertEquals( 1, c.compareTo(b));
    }
}

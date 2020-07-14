/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.async;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class OutstandingFutureTests {

    @Test
    public void flow() {
        OutstandingFuture fut = new OutstandingFuture(1, "ch", NtClient.NO_ONE, null, 0, 0, false);
        Assert.assertFalse(fut.test("ch", NtClient.NO_ONE, null, 0, 0, false));
        Assert.assertTrue(fut.outstanding());
        fut.take();
        Assert.assertFalse(fut.outstanding());
        fut.reset();
        Assert.assertFalse(fut.outstanding());
        Assert.assertTrue(fut.test("ch", NtClient.NO_ONE, null, 0, 0, false));
        Assert.assertFalse(fut.test("ch2", NtClient.NO_ONE, null, 0, 0, false));
        Assert.assertTrue(fut.outstanding());
        Assert.assertFalse(fut.test("ch", NtClient.NO_ONE, null, 0, 0, false));
        Assert.assertFalse(fut.test("ch2", NtClient.NO_ONE, null, 0, 0, false));

        ObjectNode node = Utility.createObjectNode();
        fut.dump(node);
        Assert.assertEquals("{\"id\":1,\"channel\":\"ch\"}", node.toString());
    }

    @Test
    public void array_dump() {
        OutstandingFuture fut = new OutstandingFuture(1, "ch", NtClient.NO_ONE, Utility.createArrayNode(), 1, 5, false);
        ObjectNode node = Utility.createObjectNode();
        fut.dump(node);
        Assert.assertEquals("{\"id\":1,\"channel\":\"ch\",\"options\":[],\"min\":1,\"max\":5,\"distinct\":false}", node.toString());
    }

    @Test
    public void array_updates_dump() {
        OutstandingFuture fut = new OutstandingFuture(1, "ch", NtClient.NO_ONE, Utility.createArrayNode(), 1, 5, false);
        fut.reset();
        ArrayNode arr = Utility.createArrayNode();
        arr.add(1);
        fut.test("ch", NtClient.NO_ONE, arr, 2, 7, true);
        ObjectNode node = Utility.createObjectNode();
        fut.dump(node);
        Assert.assertEquals("{\"id\":1,\"channel\":\"ch\",\"options\":[1],\"min\":2,\"max\":7,\"distinct\":true}", node.toString());
    }
}

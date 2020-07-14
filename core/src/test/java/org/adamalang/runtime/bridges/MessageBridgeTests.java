/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.bridges;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.jshell.execution.Util;
import org.adamalang.runtime.mocks.MockMessage;
import org.adamalang.runtime.mocks.MockMessageBridge;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class MessageBridgeTests {
    @Test
    public void flow() {
        MockMessageBridge bridge = new MockMessageBridge();
        MockMessage val = bridge.readFromMessageObject(Utility.parseJsonObject("{}"), "x");
        Assert.assertEquals(0, val.x);
        Assert.assertEquals("{\"x\":0,\"y\":0}", bridge.toPrivateJsonNode(NtClient.NO_ONE, val).toString());
        val = bridge.readFromMessageObject(Utility.parseJsonObject("{\"x\":{}}"), "x");
        Assert.assertEquals(0, val.x);
        Assert.assertEquals("{\"x\":0,\"y\":0}", bridge.toPrivateJsonNode(NtClient.NO_ONE, val).toString());
        val = bridge.readFromMessageObject(Utility.parseJsonObject("{\"x\":{\"x\":123}}"), "x");
        Assert.assertEquals(123, val.x);
        Assert.assertEquals("{\"x\":123,\"y\":0}", bridge.toPrivateJsonNode(NtClient.NO_ONE, val).toString());
        ObjectNode obj = Utility.createObjectNode();
        ArrayNode arr = Utility.createArrayNode();
        bridge.writeTo("z", val, obj);
        bridge.appendTo(val, arr);
        Assert.assertEquals("{\"z\":{\"x\":123,\"y\":0}}", obj.toString());
        Assert.assertEquals("[{\"x\":123,\"y\":0}]", arr.toString());
        MockMessage[] items = bridge.convertArrayMessage(Utility.parseJsonObject("{\"items\":[{\"x\":123,\"y\":0},{\"x\":0,\"y\":42}]}"));
        Assert.assertEquals(2, items.length);
        Assert.assertEquals(123, items[0].x);
        Assert.assertEquals(0, items[0].y);
        Assert.assertEquals(0, items[1].x);
        Assert.assertEquals(42, items[1].y);
    }
}

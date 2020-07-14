/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.bridges;

import com.fasterxml.jackson.databind.JsonNode;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRecordBridge;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class RecordBridgeTests {
    @Test
    public void flow() {
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.fromJsonNode(Utility.parseJsonObject("{}"));
        bridge.fromJsonNode(null);
        bridge.fromJsonNode(Utility.createArrayNode());
    }

    @Test
    public void coverage() {
        MockRecordBridge bridge = new MockRecordBridge();
        try {
            bridge.writeTo(null, null, null);
            Assert.fail();
        } catch (UnsupportedOperationException ose) {
        }
        try {
            bridge.appendTo(null, null);
        } catch (UnsupportedOperationException ose) {
        }
        MockRecord mr = new MockRecord(Utility.createObjectNode(), null);
        mr.allowCache = true;
        JsonNode nodeA = bridge.toPrivateJsonNode(NtClient.NO_ONE, mr);
        JsonNode nodeB = bridge.toPrivateJsonNode(NtClient.NO_ONE, mr);
        Assert.assertTrue(nodeA == nodeB);
        mr.allowCache = false;
        JsonNode nodeC = bridge.toPrivateJsonNode(NtClient.NO_ONE, mr);
        Assert.assertFalse(nodeA != nodeB);

    }
}

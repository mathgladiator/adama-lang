/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.mocks.MockLivingDocument;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRecordBridge;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class RxFactoryTests {
    @Test
    public void r_bool() {
        Assert.assertTrue(RxFactory.makeRxBoolean(null, Utility.parseJsonObject("{\"v\":true}"), "v", false).get());
        Assert.assertFalse(RxFactory.makeRxBoolean(null, Utility.parseJsonObject("{}"), "v", false).get());
    }
    @Test
    public void r_int() {
        Assert.assertEquals(123, (int) RxFactory.makeRxInt32(null, Utility.parseJsonObject("{\"v\":123}"), "v", 0).get());
        Assert.assertEquals(123, (int) RxFactory.makeRxInt32(null, Utility.parseJsonObject("{\"v\":\"123\"}"), "v", 0).get());
        Assert.assertEquals(42, (int) RxFactory.makeRxInt32(null, Utility.parseJsonObject("{}"), "v", 42).get());
    }
    @Test
    public void r_long() {
        Assert.assertEquals(123, (long) RxFactory.makeRxInt64(null, Utility.parseJsonObject("{\"v\":123}"), "v", 0).get());
        Assert.assertEquals(123, (long) RxFactory.makeRxInt64(null, Utility.parseJsonObject("{\"v\":\"123\"}"), "v", 0).get());
        Assert.assertEquals(42, (long) RxFactory.makeRxInt64(null, Utility.parseJsonObject("{}"), "v", 42).get());
    }
    @Test
    public void r_double() {
        Assert.assertEquals(123.5, RxFactory.makeRxDouble(null, Utility.parseJsonObject("{\"v\":123.5}"), "v", 0).get(), 0.1);
        Assert.assertEquals(123.5, RxFactory.makeRxDouble(null, Utility.parseJsonObject("{\"v\":\"123.5\"}"), "v", 0).get(), 0.1);
        Assert.assertEquals(64.5, RxFactory.makeRxDouble(null, Utility.parseJsonObject("{}"), "v", 64.6).get(), 0.1);
    }
    @Test
    public void r_str() {
        Assert.assertEquals("ninja", RxFactory.makeRxString(null, Utility.parseJsonObject("{\"v\":\"ninja\"}"), "v", "").get());
        Assert.assertEquals("cake", RxFactory.makeRxString(null, Utility.parseJsonObject("{}"), "v", "cake").get());
    }
    @Test
    public void r_client() {
        NtClient demo = new NtClient("a", "b");
        ObjectNode n = Utility.createObjectNode();
        demo.dump(n);
        Assert.assertEquals("{\"agent\":\"a\",\"authority\":\"b\"}", n.toString());
        Assert.assertEquals(demo, RxFactory.makeRxClient(null, Utility.parseJsonObject("{\"v\":{\"agent\":\"a\",\"authority\":\"b\"}}"), "v", NtClient.NO_ONE).get());
        Assert.assertEquals(demo, RxFactory.makeRxClient(null, Utility.parseJsonObject("{}"), "v", demo).get());
        Assert.assertEquals(NtClient.NO_ONE, RxFactory.makeRxClient(null, Utility.parseJsonObject("{}"), "v", NtClient.NO_ONE).get());
    }
    @Test
    public void r_maybe_nope() {
        ObjectNode node = Utility.parseJsonObject("{}");
        RxMaybe<RxInt32> m = RxFactory.makeRxMaybe(null, node, "v", (p) -> RxFactory.makeRxInt32(p, node, "v", 42));
        Assert.assertFalse(m.has());
    }
    @Test
    public void r_maybe_yep() {
        ObjectNode node = Utility.parseJsonObject("{\"v\":123}");
        RxMaybe<RxInt32> m = RxFactory.makeRxMaybe(null, node, "v", (p) -> RxFactory.makeRxInt32(p, node, "v", 42));
        Assert.assertTrue(m.has());
        Assert.assertEquals(123, m.get().get());
    }
    @Test
    public void child_exists_nope() {
        ObjectNode node = Utility.parseJsonObject("{\"v\":123}");
        RxFactory.ensureChildNodeExists(node, "x");
        Assert.assertEquals("{\"v\":123,\"x\":{}}", node.toString());
        RxFactory.ensureChildNodeExists(node, "v");
        Assert.assertEquals("{\"v\":{},\"x\":{}}", node.toString());
    }
    @Test
    public void child_exists_yep() {
        ObjectNode node = Utility.parseJsonObject("{\"v\":{\"x\":123}}");
        RxFactory.ensureChildNodeExists(node, "v");
        Assert.assertEquals("{\"v\":{\"x\":123}}", node.toString());
    }
    @Test
    public void r_table_1() {
        MockLivingDocument document = new MockLivingDocument();
        RxTable<MockRecord> tbl = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{}"), "t", new MockRecordBridge());
        Assert.assertEquals(0, tbl.size());
    }
    @Test
    public void r_table_2() {
        MockLivingDocument document = new MockLivingDocument();
        RxTable<MockRecord> tbl = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{}}"), "t", new MockRecordBridge());
        Assert.assertEquals(0, tbl.size());
    }
    @Test
    public void r_table_3() {
        MockLivingDocument document = new MockLivingDocument();
        RxTable<MockRecord> tbl = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":5,\"rows\":{\"0\":{}}}}"), "t", new MockRecordBridge());
        Assert.assertEquals(1, tbl.size());
    }
}

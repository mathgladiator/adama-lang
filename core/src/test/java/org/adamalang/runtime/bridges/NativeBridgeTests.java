/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.bridges;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class NativeBridgeTests {
    @Test
    public void test_domains() {
        Assert.assertEquals(123, (int) NativeBridge.INTEGER_NATIVE_SUPPORT.fromDomainString("123"));
        Assert.assertEquals(123, (long) NativeBridge.LONG_NATIVE_SUPPORT.fromDomainString("123"));
        Assert.assertEquals("123", NativeBridge.STRING_NATIVE_SUPPORT.fromDomainString("123"));
        Assert.assertEquals("123", NativeBridge.INTEGER_NATIVE_SUPPORT.toDomainString(123));
        Assert.assertEquals("123", NativeBridge.LONG_NATIVE_SUPPORT.toDomainString(123L));
        Assert.assertEquals("123", NativeBridge.STRING_NATIVE_SUPPORT.toDomainString("123"));
    }
    @Test
    public void test_int() {
        Assert.assertEquals(0, (int) NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(Utility.createObjectNode(), "foo"));
        Assert.assertEquals(123, (int) NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(Utility.parseJsonObject("{\"foo\":123}"), "foo"));
        JsonNode node = NativeBridge.INTEGER_NATIVE_SUPPORT.toPrivateJsonNode(NtClient.NO_ONE, 123);
        Assert.assertTrue(node.isIntegralNumber());
        Assert.assertEquals(123, node.asInt(0));
        ObjectNode obj = Utility.createObjectNode();
        NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo("f", 123, obj);
        Assert.assertEquals("{\"f\":123}", obj.toString());
        ArrayNode arr = Utility.createArrayNode();
        NativeBridge.INTEGER_NATIVE_SUPPORT.appendTo(42, arr);
        Assert.assertEquals("[42]", arr.toString());
        Integer[] a = NativeBridge.INTEGER_NATIVE_SUPPORT.makeArray(10);
        Assert.assertEquals(10, a.length);
    }

    @Test
    public void test_long() {
        Assert.assertEquals(0L, (long) NativeBridge.LONG_NATIVE_SUPPORT.readFromMessageObject(Utility.createObjectNode(), "foo"));
        Assert.assertEquals(123L, (long) NativeBridge.LONG_NATIVE_SUPPORT.readFromMessageObject(Utility.parseJsonObject("{\"foo\":123}"), "foo"));
        Assert.assertEquals(123L, (long) NativeBridge.LONG_NATIVE_SUPPORT.readFromMessageObject(Utility.parseJsonObject("{\"foo\":\"123\"}"), "foo"));
        JsonNode node = NativeBridge.LONG_NATIVE_SUPPORT.toPrivateJsonNode(NtClient.NO_ONE, 123L);
        Assert.assertTrue(node.isTextual());
        Assert.assertEquals(123L, node.asInt(0));
        ObjectNode obj = Utility.createObjectNode();
        NativeBridge.LONG_NATIVE_SUPPORT.writeTo("f", 123L, obj);
        Assert.assertEquals("{\"f\":\"123\"}", obj.toString());
        ArrayNode arr = Utility.createArrayNode();
        NativeBridge.LONG_NATIVE_SUPPORT.appendTo(42L, arr);
        Assert.assertEquals("[\"42\"]", arr.toString());
        Long[] a = NativeBridge.LONG_NATIVE_SUPPORT.makeArray(10);
        Assert.assertEquals(10, a.length);
    }

    @Test
    public void test_double() {
        Assert.assertEquals(0, (double) NativeBridge.DOUBLE_NATIVE_SUPPORT.readFromMessageObject(Utility.createObjectNode(), "foo"), 0.0000001);
        Assert.assertEquals(123.5, (double) NativeBridge.DOUBLE_NATIVE_SUPPORT.readFromMessageObject(Utility.parseJsonObject("{\"foo\":123.5}"), "foo"), 0.0000001);
        JsonNode node = NativeBridge.DOUBLE_NATIVE_SUPPORT.toPrivateJsonNode(NtClient.NO_ONE, 123.5);
        Assert.assertTrue(node.isNumber());
        Assert.assertEquals(123.5, node.asDouble(0), 0.0000001);
        ObjectNode obj = Utility.createObjectNode();
        NativeBridge.DOUBLE_NATIVE_SUPPORT.writeTo("f", 123.5, obj);
        Assert.assertEquals("{\"f\":123.5}", obj.toString());
        ArrayNode arr = Utility.createArrayNode();
        NativeBridge.DOUBLE_NATIVE_SUPPORT.appendTo(42.5, arr);
        Assert.assertEquals("[42.5]", arr.toString());
        Double[] a = NativeBridge.DOUBLE_NATIVE_SUPPORT.makeArray(10);
        Assert.assertEquals(10, a.length);
    }

    @Test
    public void test_bool() {
        Assert.assertEquals(false, (boolean) NativeBridge.BOOLEAN_NATIVE_SUPPORT.readFromMessageObject(Utility.createObjectNode(), "foo"));
        Assert.assertEquals(true, (boolean) NativeBridge.BOOLEAN_NATIVE_SUPPORT.readFromMessageObject(Utility.parseJsonObject("{\"foo\":true}"), "foo"));
        JsonNode node = NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(NtClient.NO_ONE, true);
        Assert.assertTrue(node.isBoolean());
        Assert.assertEquals(true, node.asBoolean(false));
        ObjectNode obj = Utility.createObjectNode();
        NativeBridge.BOOLEAN_NATIVE_SUPPORT.writeTo("f", true, obj);
        Assert.assertEquals("{\"f\":true}", obj.toString());
        ArrayNode arr = Utility.createArrayNode();
        NativeBridge.BOOLEAN_NATIVE_SUPPORT.appendTo(true, arr);
        NativeBridge.BOOLEAN_NATIVE_SUPPORT.appendTo(false, arr);
        NativeBridge.BOOLEAN_NATIVE_SUPPORT.appendTo(true, arr);
        Assert.assertEquals("[true,false,true]", arr.toString());
        Boolean[] a = NativeBridge.BOOLEAN_NATIVE_SUPPORT.makeArray(10);
        Assert.assertEquals(10, a.length);
    }

    @Test
    public void test_string() {
        Assert.assertEquals("", NativeBridge.STRING_NATIVE_SUPPORT.readFromMessageObject(Utility.createObjectNode(), "foo"));
        Assert.assertEquals("x", NativeBridge.STRING_NATIVE_SUPPORT.readFromMessageObject(Utility.parseJsonObject("{\"foo\":\"x\"}"), "foo"));
        JsonNode node = NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(NtClient.NO_ONE, "x");
        Assert.assertTrue(node.isTextual());
        Assert.assertEquals("x", node.asText());
        ObjectNode obj = Utility.createObjectNode();
        NativeBridge.STRING_NATIVE_SUPPORT.writeTo("f", "z", obj);
        Assert.assertEquals("{\"f\":\"z\"}", obj.toString());
        ArrayNode arr = Utility.createArrayNode();
        NativeBridge.STRING_NATIVE_SUPPORT.appendTo("a", arr);
        NativeBridge.STRING_NATIVE_SUPPORT.appendTo("b", arr);
        NativeBridge.STRING_NATIVE_SUPPORT.appendTo("c", arr);
        Assert.assertEquals("[\"a\",\"b\",\"c\"]", arr.toString());
        String[] a = NativeBridge.STRING_NATIVE_SUPPORT.makeArray(10);
        Assert.assertEquals(10, a.length);
    }

    @Test
    public void test_string_conv() {
        Assert.assertEquals("123", NativeBridge.STRING_NATIVE_SUPPORT.readFromMessageObject(Utility.parseJsonObject("{\"foo\":123}"), "foo"));
        Assert.assertEquals("true", NativeBridge.STRING_NATIVE_SUPPORT.readFromMessageObject(Utility.parseJsonObject("{\"foo\":true}"), "foo"));
        Assert.assertEquals("1.2", NativeBridge.STRING_NATIVE_SUPPORT.readFromMessageObject(Utility.parseJsonObject("{\"foo\":1.2}"), "foo"));
        Assert.assertEquals("", NativeBridge.STRING_NATIVE_SUPPORT.readFromMessageObject(Utility.parseJsonObject("{\"foo\":{}}"), "foo"));
    }

    @Test
    public void test_client() {
        Assert.assertEquals(new NtClient("x", "z"), NativeBridge.CLIENT_NATIVE_SUPPORT.readFromMessageObject(Utility.parseJsonObject("{\"foo\":{\"agent\":\"x\",\"authority\":\"z\"}}"), "foo"));
        ObjectNode obj = Utility.createObjectNode();
        NativeBridge.CLIENT_NATIVE_SUPPORT.writeTo("foo", NtClient.NO_ONE, obj);
        Assert.assertEquals("{\"foo\":{\"agent\":\"?\",\"authority\":\"?\"}}", obj.toString());
        ArrayNode arr = Utility.createArrayNode();
        NativeBridge.CLIENT_NATIVE_SUPPORT.appendTo(NtClient.NO_ONE, arr);
        Assert.assertEquals("[{\"agent\":\"?\",\"authority\":\"?\"}]", arr.toString());
        NtClient[] a = NativeBridge.CLIENT_NATIVE_SUPPORT.makeArray(10);
        Assert.assertEquals(10, a.length);
        JsonNode node = NativeBridge.CLIENT_NATIVE_SUPPORT.toPrivateJsonNode(NtClient.NO_ONE, NtClient.NO_ONE);
        Assert.assertEquals("{\"agent\":\"?\",\"authority\":\"?\"}", node.toString());
    }

    @Test
    public void test_list() {
        ObjectNode obj = Utility.createObjectNode();
        ArrayNode arr = Utility.createArrayNode();
        NativeBridge<NtList<String>> bridge = NativeBridge.WRAP_LIST(NativeBridge.STRING_NATIVE_SUPPORT);
        NtList<String> val = bridge.readFromMessageObject(Utility.parseJsonObject("{}"), "x");
        Assert.assertEquals(0, val.size());
        Assert.assertEquals("[]", bridge.toPrivateJsonNode(NtClient.NO_ONE, val).toString());
        bridge.writeTo("x", val, obj);
        bridge.appendTo(val, arr);
        Assert.assertEquals("{\"x\":[]}", obj.toString());
        Assert.assertEquals("[[]]", arr.toString());
        val = bridge.readFromMessageObject(Utility.parseJsonObject("{\"x\":[\"z\"]}"), "x");
        Assert.assertEquals(1, val.size());
        Assert.assertEquals("[\"z\"]", bridge.toPrivateJsonNode(NtClient.NO_ONE, val).toString());
        bridge.writeTo("x", val, obj);
        bridge.appendTo(val, arr);
        Assert.assertEquals("{\"x\":[\"z\"]}", obj.toString());
        Assert.assertEquals("[[],[\"z\"]]", arr.toString());
        NtList<String>[] a = bridge.makeArray(10);
        Assert.assertEquals(10, a.length);
    }

    @Test
    public void test_array() {
        ObjectNode obj = Utility.createObjectNode();
        ArrayNode arr = Utility.createArrayNode();
        NativeBridge<String[]> bridge = NativeBridge.WRAP_ARRAY(NativeBridge.STRING_NATIVE_SUPPORT);
        String[] val = bridge.readFromMessageObject(Utility.parseJsonObject("{}"), "x");
        Assert.assertEquals(0, val.length);
        Assert.assertEquals("[]", bridge.toPrivateJsonNode(NtClient.NO_ONE, val).toString());
        bridge.writeTo("x", val, obj);
        bridge.appendTo(val, arr);
        Assert.assertEquals("{\"x\":[]}", obj.toString());
        Assert.assertEquals("[[]]", arr.toString());
        val = bridge.readFromMessageObject(Utility.parseJsonObject("{\"x\":[\"z\"]}"), "x");
        Assert.assertEquals(1, val.length);
        Assert.assertEquals("[\"z\"]", bridge.toPrivateJsonNode(NtClient.NO_ONE, val).toString());
        bridge.writeTo("x", val, obj);
        bridge.appendTo(val, arr);
        Assert.assertEquals("{\"x\":[\"z\"]}", obj.toString());
        Assert.assertEquals("[[],[\"z\"]]", arr.toString());
        try {
            bridge.makeArray(10);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
    }

    @Test
    public void test_maybe() {
        NativeBridge<NtMaybe<String>> bridge = NativeBridge.WRAP_MAYBE(NativeBridge.STRING_NATIVE_SUPPORT);
        NtMaybe<String> val = bridge.readFromMessageObject(Utility.parseJsonObject("{}"), "x");
        ObjectNode obj = Utility.createObjectNode();
        ArrayNode arr = Utility.createArrayNode();
        Assert.assertFalse(val.has());
        Assert.assertEquals("null", bridge.toPrivateJsonNode(NtClient.NO_ONE, val).toString());
        bridge.writeTo("x", val, obj);
        Assert.assertEquals("{}", obj.toString());
        bridge.appendTo(val, arr);
        Assert.assertEquals("[null]", arr.toString());
        val = bridge.readFromMessageObject(Utility.parseJsonObject("{\"x\":\"z\"}"), "x");
        Assert.assertTrue(val.has());
        Assert.assertEquals("z", val.get());
        Assert.assertEquals("\"z\"", bridge.toPrivateJsonNode(NtClient.NO_ONE, val).toString());
        bridge.writeTo("x", val, obj);
        Assert.assertEquals("{\"x\":\"z\"}", obj.toString());
        bridge.writeTo("x", new NtMaybe<>(), obj);
        bridge.appendTo(val, arr);
        Assert.assertEquals("[null,\"z\"]", arr.toString());
        Assert.assertEquals("{\"x\":null}", obj.toString());
        NtMaybe<String>[] a = bridge.makeArray(10);
        Assert.assertEquals(10, a.length);
    }

    @Test
    public void test_map() throws Exception {
        NativeBridge<NtMap<String, String>> bridge = NativeBridge.WRAP_MAP(NativeBridge.STRING_NATIVE_SUPPORT, NativeBridge.STRING_NATIVE_SUPPORT);
        NtMap<String, String> map1 = bridge.readFromMessageObject(Utility.parseJsonObject("{\"x\":{\"a\":\"b\"}}"), "x");
        Assert.assertEquals(1, map1.size());
        Assert.assertEquals("b", map1.lookup("a").get());
        NtMap<String, String> map2 = bridge.fromJsonNode(Utility.parseJsonObject("{\"a\":\"c\"}"));
        Assert.assertEquals(1, map2.size());
        Assert.assertEquals("c", map2.lookup("a").get());
        Assert.assertEquals("{\"a\":\"b\"}", bridge.toPrivateJsonNode(NtClient.NO_ONE, map1).toString());
        Assert.assertEquals("{\"a\":\"c\"}", bridge.toPrivateJsonNode(NtClient.NO_ONE, map2).toString());
        Assert.assertEquals(10, bridge.makeArray(10).length);
        ObjectNode obj = Utility.createObjectNode();
        bridge.writeTo("z", map1, obj);
        ArrayNode arr = Utility.createArrayNode();
        bridge.appendTo(map2, arr);
        Assert.assertEquals("{\"z\":{\"a\":\"b\"}}", obj.toString());
        Assert.assertEquals("[{\"a\":\"c\"}]", arr.toString());
        Assert.assertEquals(0, bridge.fromJsonNode(Utility.MAPPER.readTree("null")).size());
        Assert.assertEquals(0, bridge.fromJsonNode(null).size());
    }
}

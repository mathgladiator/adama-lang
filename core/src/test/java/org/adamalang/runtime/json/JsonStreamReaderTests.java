/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class JsonStreamReaderTests {
    @Test
    public void scanOver() {
        JsonStreamReader reader = new JsonStreamReader("{}");
        if (reader.startObject()) {
            Assert.assertFalse(reader.notEndOfObject());
        }
    }

    @Test
    public void skipValue() {
        {
            JsonStreamReader reader = new JsonStreamReader("42");
            reader.skipValue();
            Assert.assertTrue(reader.end());
        }
        {
            JsonStreamReader reader = new JsonStreamReader("\"x\"");
            reader.skipValue();
            Assert.assertTrue(reader.end());
        }
        {
            JsonStreamReader reader = new JsonStreamReader("[\"x\",42]");
            reader.skipValue();
            Assert.assertTrue(reader.end());
        }
        {
            JsonStreamReader reader = new JsonStreamReader("{\"x\":42}");
            reader.skipValue();
            Assert.assertTrue(reader.end());
        }
    }
    @Test
    public void readObject1() {
        JsonStreamReader reader = new JsonStreamReader("{\"x\":\"z\"}");
        Assert.assertTrue(reader.startObject());
        Assert.assertTrue(reader.notEndOfObject());
        Assert.assertEquals("x", reader.fieldName());
        Assert.assertEquals("z", reader.readString());
        Assert.assertFalse(reader.notEndOfObject());
        Assert.assertTrue(reader.end());
    }
    @Test
    public void readObject2() {
        JsonStreamReader reader = new JsonStreamReader("{\"x\":\"z\",\"z\":123}");
        Assert.assertTrue(reader.startObject());
        Assert.assertTrue(reader.notEndOfObject());
        Assert.assertEquals("x", reader.fieldName());
        Assert.assertEquals("z", reader.readString());
        Assert.assertTrue(reader.notEndOfObject());
        Assert.assertEquals("z", reader.fieldName());
        Assert.assertEquals(123, reader.readInteger());
        Assert.assertFalse(reader.notEndOfObject());
        Assert.assertTrue(reader.end());
    }
    @Test
    public void readObject3() {
        JsonStreamReader reader = new JsonStreamReader("{\"x\":\"z\",\"z\":123.4,\"t\":true,\"f\":false,\"n\":null}");
        Assert.assertTrue(reader.testLackOfNull());;
        Assert.assertTrue(reader.startObject());
        Assert.assertTrue(reader.notEndOfObject());
        Assert.assertEquals("x", reader.fieldName());
        Assert.assertEquals("z", reader.readString());
        Assert.assertTrue(reader.notEndOfObject());
        Assert.assertEquals("z", reader.fieldName());
        Assert.assertTrue(reader.testLackOfNull());;
        Assert.assertEquals(123.4, reader.readDouble(), 0.01);
        Assert.assertTrue(reader.notEndOfObject());
        Assert.assertEquals("t", reader.fieldName());
        Assert.assertTrue(reader.testLackOfNull());;
        Assert.assertEquals(true, reader.readBoolean());
        Assert.assertTrue(reader.notEndOfObject());
        Assert.assertEquals("f", reader.fieldName());
        Assert.assertTrue(reader.testLackOfNull());;
        Assert.assertEquals(false, reader.readBoolean());
        Assert.assertTrue(reader.notEndOfObject());
        Assert.assertEquals("n", reader.fieldName());
        Assert.assertFalse(reader.testLackOfNull());;
        Assert.assertFalse(reader.notEndOfObject());
        Assert.assertTrue(reader.end());
    }
    @Test
    public void readArray() {
        JsonStreamReader reader = new JsonStreamReader("[\"x\",\"z\",123,\"4444444\"]");
        Assert.assertTrue(reader.startArray());
        Assert.assertTrue(reader.notEndOfArray());
        Assert.assertEquals("x", reader.readString());
        Assert.assertTrue(reader.notEndOfArray());
        Assert.assertEquals("z", reader.readString());
        Assert.assertTrue(reader.notEndOfArray());
        Assert.assertEquals(123, reader.readInteger());
        Assert.assertTrue(reader.notEndOfArray());
        Assert.assertEquals(4444444L, reader.readLong());
        Assert.assertFalse(reader.notEndOfArray());
        Assert.assertTrue(reader.end());
    }
    @Test
    public void readNtCLient() {
        JsonStreamReader reader = new JsonStreamReader("{\"agent\":\"z\",\"authority\":\"g\"}");
        NtClient c = reader.readNtClient();
        Assert.assertEquals("z", c.agent);
        Assert.assertEquals("g", c.authority);
    }

    private void assertEcho(String x) {
        JsonStreamReader reader = new JsonStreamReader(x);
        JsonStreamWriter writer = new JsonStreamWriter();
        reader.skipValue(writer);
        Assert.assertEquals(x, writer.toString());
    }

    @Test
    public void echoBattery() {
        assertEcho("{}");
        assertEcho("{\"x\":123}");
        assertEcho("[1,3,true,null,{},[]]");
        assertEcho("[\"x\",{\"y\":123}]");
        assertEcho("\"\\n\\t\\r\\f\\b\\\\\\\"\"");
        assertEcho("\"cake\"");
        assertEcho("true");
        assertEcho("null");
        assertEcho("false");
        assertEcho("\"\\u733f\\u3082\\u6728\\u304b\\u3089\\u843d\\u3061\\u308b\"");
        assertEcho("\"cake\\n\\t\\r\\f\\b\\\\\\\"ninja\"");
    }

    @Test
    public void longString() {
        JsonStreamReader reader = new JsonStreamReader("\"1234zenninja\"");
        Assert.assertEquals("1234zenninja", reader.readString());
    }

    @Test
    public void readBad() {
        try {
            JsonStreamReader reader = new JsonStreamReader("cake");
            reader.skipValue();
            Assert.fail();
        } catch (UnsupportedOperationException use) {

        }
    }

    @Test
    public void readBadString() {
        try {
            JsonStreamReader reader = new JsonStreamReader("\"cake");
            reader.skipValue();
            Assert.fail();
        } catch (UnsupportedOperationException use) {

        }
    }
}

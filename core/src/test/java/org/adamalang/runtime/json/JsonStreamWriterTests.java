/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.json;

import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtDynamic;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class JsonStreamWriterTests {
    @Test
    public void bad_obj() {
        JsonStreamWriter writer = new JsonStreamWriter();
        try {
            writer.writeTree(new JsonStreamReader(""));
            Assert.fail();
        } catch (RuntimeException re) {
            Assert.assertTrue(re.getMessage().startsWith("unexpected object:"));
        }
    }

    @Test
    public void asset() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeNtAsset(new NtAsset(123, "name", "png", 42, "hash", "sheesh"));
        Assert.assertEquals("{\"id\":\"123\",\"size\":\"42\",\"name\":\"name\",\"type\":\"png\",\"md5\":\"hash\",\"sha384\":\"sheesh\"}", writer.toString());
    }

    @Test
    public void tree_empty_map() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(new HashMap<>());
        Assert.assertEquals("{}", writer.toString());
    }

    @Test
    public void tree_empty_array() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(new ArrayList<>());
        Assert.assertEquals("[]", writer.toString());
    }

    @Test
    public void tree_map1() {
        JsonStreamWriter writer = new JsonStreamWriter();
        HashMap<String, Object> map = new HashMap<>();
        map.put("x", 123);
        writer.writeTree(map);
        Assert.assertEquals("{\"x\":123}", writer.toString());
    }

    @Test
    public void tree_array_1() {
        JsonStreamWriter writer = new JsonStreamWriter();
        ArrayList<Object> arr = new ArrayList<>();
        arr.add(123);
        writer.writeTree(arr);
        Assert.assertEquals("[123]", writer.toString());
    }

    @Test
    public void tree_map2() {
        JsonStreamWriter writer = new JsonStreamWriter();
        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("x", 123);
        map.put("y", false);
        writer.writeTree(map);
        Assert.assertEquals("{\"x\":123,\"y\":false}", writer.toString());
    }

    @Test
    public void tree_array_2() {
        JsonStreamWriter writer = new JsonStreamWriter();
        ArrayList<Object> arr = new ArrayList<>();
        arr.add(123);
        arr.add(true);
        writer.writeTree(arr);
        Assert.assertEquals("[123,true]", writer.toString());
    }

    @Test
    public void tree_double() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(1.5);
        Assert.assertEquals("1.5", writer.toString());
    }

    @Test
    public void tree_null() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(null);
        Assert.assertEquals("null", writer.toString());
    }

    @Test
    public void tree_int() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(1);
        Assert.assertEquals("1", writer.toString());
    }

    @Test
    public void tree_long() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(1L);
        Assert.assertEquals("\"1\"", writer.toString());
    }

    @Test
    public void tree_bool() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(true);
        Assert.assertEquals("true", writer.toString());
    }

    @Test
    public void tree_string() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree("hi");
        Assert.assertEquals("\"hi\"", writer.toString());
    }

    @Test
    public void tree_string_escape() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree("hi\n");
        Assert.assertEquals("\"hi\\n\"", writer.toString());
    }

    @Test
    public void emptyObject() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.endObject();
        Assert.assertEquals("{}", w.toString());
    }

    @Test
    public void simpleNtClient1() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.writeNtClient(NtClient.NO_ONE);
        Assert.assertEquals("{\"agent\":\"?\",\"authority\":\"?\"}", w.toString());
    }

    @Test
    public void simpleNtClient2() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.writeNtClient(new NtClient("x", "y"));
        Assert.assertEquals("{\"agent\":\"x\",\"authority\":\"y\"}", w.toString());
    }

    @Test
    public void fieldWithIn() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro(42);
        w.writeInteger(123);
        w.endObject();
        Assert.assertEquals("{\"42\":123}", w.toString());
    }

    @Test
    public void inject() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro(42);
        w.injectJson("[1,2,3]");
        w.endObject();
        Assert.assertEquals("{\"42\":[1,2,3]}", w.toString());
    }

    @Test
    public void injectDynamic() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro(42);
        w.writeNtDynamic(new NtDynamic("[1,2,3]"));
        w.endObject();
        Assert.assertEquals("{\"42\":[1,2,3]}", w.toString());
    }

    @Test
    public void singleIntField() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro("x");
        w.writeInteger(123);
        w.endObject();
        Assert.assertEquals("{\"x\":123}", w.toString());
    }
    @Test
    public void singleLongField() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro("x");
        w.writeLong(123L);
        w.endObject();
        Assert.assertEquals("{\"x\":\"123\"}", w.toString());
    }
    @Test
    public void singleNull() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro("x");
        w.writeNull();
        w.endObject();
        Assert.assertEquals("{\"x\":null}", w.toString());
    }
    @Test
    public void singleDoubleField() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro("xyz");
        w.writeDouble(42.42);
        w.endObject();
        Assert.assertEquals("{\"xyz\":42.42}", w.toString());
    }
    @Test
    public void singleStringField() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro("ninja");
        w.writeString("geez");
        w.endObject();
        Assert.assertEquals("{\"ninja\":\"geez\"}", w.toString());
    }
    @Test
    public void singleBooleanField() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro("caaa");
        w.writeBoolean(false);
        w.endObject();
        Assert.assertEquals("{\"caaa\":false}", w.toString());
    }
    @Test
    public void multipleStrings() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro("f0");
        w.writeString("x");
        w.writeObjectFieldIntro("f2");
        w.writeString("y");
        w.writeObjectFieldIntro("f1");
        w.writeString("z");
        w.writeObjectFieldIntro("f3");
        w.writeString("t");
        w.endObject();
        Assert.assertEquals("{\"f0\":\"x\",\"f2\":\"y\",\"f1\":\"z\",\"f3\":\"t\"}", w.toString());
    }
    @Test
    public void multipleInts() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro("f0");
        w.writeInteger(1);
        w.writeObjectFieldIntro("f2");
        w.writeInteger(2);
        w.writeObjectFieldIntro("f1");
        w.writeInteger(3);
        w.writeObjectFieldIntro("f3");
        w.writeInteger(4);
        w.endObject();
        Assert.assertEquals("{\"f0\":1,\"f2\":2,\"f1\":3,\"f3\":4}", w.toString());
    }
    @Test
    public void multipleDoubles() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro("f0");
        w.writeDouble(1.5);
        w.writeObjectFieldIntro("f2");
        w.writeDouble(2.5);
        w.writeObjectFieldIntro("f1");
        w.writeDouble(3.5);
        w.writeObjectFieldIntro("f3");
        w.writeDouble(4.5);
        w.endObject();
        Assert.assertEquals("{\"f0\":1.5,\"f2\":2.5,\"f1\":3.5,\"f3\":4.5}", w.toString());
    }
    @Test
    public void multipleBools() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro("f0");
        w.writeBoolean(true);
        w.writeObjectFieldIntro("f2");
        w.writeBoolean(false);
        w.writeObjectFieldIntro("f1");
        w.writeBoolean(true);
        w.writeObjectFieldIntro("f3");
        w.writeBoolean(false);
        w.endObject();
        Assert.assertEquals("{\"f0\":true,\"f2\":false,\"f1\":true,\"f3\":false}", w.toString());
    }
    @Test
    public void arrayOfInts() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginArray();
        w.writeInteger(1);
        w.writeInteger(2);
        w.writeInteger(3);
        w.endArray();
        Assert.assertEquals("[1,2,3]", w.toString());
    }
    @Test
    public void arrayOfDoubles() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginArray();
        w.writeDouble(1.5);
        w.writeDouble(2.5);
        w.writeDouble(3.5);
        w.endArray();
        Assert.assertEquals("[1.5,2.5,3.5]", w.toString());
    }

    @Test
    public void arrayOfStrings() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginArray();
        w.writeString("a");
        w.writeString("b");
        w.writeString("c");
        w.endArray();
        Assert.assertEquals("[\"a\",\"b\",\"c\"]", w.toString());
    }
    @Test
    public void arrayOfBools() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginArray();
        w.writeBoolean(true);
        w.writeBoolean(false);
        w.writeBoolean(true);
        w.writeBoolean(false);
        w.endArray();
        Assert.assertEquals("[true,false,true,false]", w.toString());
    }
    @Test
    public void arrayOfObjects() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginArray();
        w.beginObject();
        w.writeObjectFieldIntro("x");
        w.writeBoolean(true);
        w.endObject();
        w.beginObject();
        w.writeObjectFieldIntro("x");
        w.writeString("y");
        w.writeObjectFieldIntro("zzz");
        w.beginObject();
        w.endObject();
        w.endObject();
        w.endArray();
        Assert.assertEquals("[{\"x\":true},{\"x\":\"y\",\"zzz\":{}}]", w.toString());
    }

    @Test
    public void nesting() throws Exception {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginArray();
        w.beginObject();
        w.writeObjectFieldIntro("x");
        w.beginArray();
        w.beginObject();
        w.writeObjectFieldIntro("z");
        w.writeDouble(123);
        w.writeObjectFieldIntro(42);
        w.writeDouble(1235);
        w.writeObjectFieldIntro(50L);
        w.writeBoolean(true);
        w.endObject();
        w.writeString("k");
        w.endArray();
        w.endObject();
        w.endArray();
        Assert.assertEquals("[{\"x\":[{\"z\":123.0,\"42\":1235.0,\"50\":true},\"k\"]}]", w.toString());
    }

    @Test
    public void unicode() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeString("猿も木から落ちる");
        Assert.assertEquals("\"\\u733f\\u3082\\u6728\\u304b\\u3089\\u843d\\u3061\\u308b\"", writer.toString());
    }

    @Test
    public void unicode2() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeString("" + (char) (5 * 256) + "" + (char) (40 * 256 + 5));
        Assert.assertEquals("\"\\u0500\\u2805\"", writer.toString());
    }
}

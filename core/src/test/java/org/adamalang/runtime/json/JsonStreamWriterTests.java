/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class JsonStreamWriterTests {
    @Test
    public void emptyObject() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.endObject();
        Assert.assertEquals("{}", w.toString());
    }
    @Test
    public void singleIntField() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginObject();
        w.writeObjectFieldIntro("x");
        w.writeInt(123);
        w.endObject();
        Assert.assertEquals("{\"x\":123}", w.toString());
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
        w.writeBool(false);
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
        w.writeInt(1);
        w.writeObjectFieldIntro("f2");
        w.writeInt(2);
        w.writeObjectFieldIntro("f1");
        w.writeInt(3);
        w.writeObjectFieldIntro("f3");
        w.writeInt(4);
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
        w.writeBool(true);
        w.writeObjectFieldIntro("f2");
        w.writeBool(false);
        w.writeObjectFieldIntro("f1");
        w.writeBool(true);
        w.writeObjectFieldIntro("f3");
        w.writeBool(false);
        w.endObject();
        Assert.assertEquals("{\"f0\":true,\"f2\":false,\"f1\":true,\"f3\":false}", w.toString());
    }
    @Test
    public void arrayOfInts() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginArray();
        w.writeInt(1);
        w.writeInt(2);
        w.writeInt(3);
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
        w.writeBool(true);
        w.writeBool(false);
        w.writeBool(true);
        w.writeBool(false);
        w.endArray();
        Assert.assertEquals("[true,false,true,false]", w.toString());
    }
    @Test
    public void arrayOfObjects() {
        JsonStreamWriter w = new JsonStreamWriter();
        w.beginArray();
        w.beginObject();
        w.writeObjectFieldIntro("x");
        w.writeBool(true);
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
        w.endObject();
        w.writeString("k");
        w.endArray();
        w.endObject();
        w.endArray();
        Assert.assertEquals("[{\"x\":[{\"z\":123.0},\"k\"]}]", w.toString());
    }
}

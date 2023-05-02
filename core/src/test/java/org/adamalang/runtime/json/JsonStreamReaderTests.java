/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.json;

import org.adamalang.common.Json;
import org.adamalang.runtime.natives.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class JsonStreamReaderTests {
  @Test
  public void bad_obj() {
    JsonStreamReader reader = new JsonStreamReader("[}");
    try {
      reader.readJavaTree();
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals(
          "unexpected token: JsonToken{data='null', type=EndObject}", re.getMessage());
    }
  }

  @Test
  public void emptyIsZero() {
    {
      JsonStreamReader reader = new JsonStreamReader("{\"x\":\"\"}");
      Assert.assertTrue(reader.startObject());
      Assert.assertTrue(reader.notEndOfObject());
      Assert.assertEquals("x", reader.fieldName());
      Assert.assertEquals(0, reader.readInteger());
    }
    {
      JsonStreamReader reader = new JsonStreamReader("{\"x\":\"\"}");
      Assert.assertTrue(reader.startObject());
      Assert.assertTrue(reader.notEndOfObject());
      Assert.assertEquals("x", reader.fieldName());
      Assert.assertTrue(Math.abs(reader.readDouble()) < 0.0001);
    }
    {
      JsonStreamReader reader = new JsonStreamReader("{\"x\":\"\"}");
      Assert.assertTrue(reader.startObject());
      Assert.assertTrue(reader.notEndOfObject());
      Assert.assertEquals("x", reader.fieldName());
      Assert.assertEquals(0, reader.readLong());
    }
  }

  @Test
  public void nullIsZero() {
    {
      JsonStreamReader reader = new JsonStreamReader("{\"x\":null}");
      Assert.assertTrue(reader.startObject());
      Assert.assertTrue(reader.notEndOfObject());
      Assert.assertEquals("x", reader.fieldName());
      Assert.assertEquals(0, reader.readInteger());
    }
    {
      JsonStreamReader reader = new JsonStreamReader("{\"x\":null}");
      Assert.assertTrue(reader.startObject());
      Assert.assertTrue(reader.notEndOfObject());
      Assert.assertEquals("x", reader.fieldName());
      Assert.assertTrue(Math.abs(reader.readDouble()) < 0.0001);
    }
    {
      JsonStreamReader reader = new JsonStreamReader("{\"x\":null}");
      Assert.assertTrue(reader.startObject());
      Assert.assertTrue(reader.notEndOfObject());
      Assert.assertEquals("x", reader.fieldName());
      Assert.assertEquals(0, reader.readLong());
    }
  }

  @Test
  public void dupes() {
    JsonStreamReader reader = new JsonStreamReader("{}");
    HashSet<String> dupes = new HashSet<>();
    dupes.add("x");
    reader.ingestDedupe(dupes);
  }

  @Test
  public void dedupeClient() {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginArray();
    writer.writeNtPrincipal(new NtPrincipal("jack", "jill"));
    writer.writeNtPrincipal(new NtPrincipal("jack", "jill"));
    writer.writeNtPrincipal(new NtPrincipal("jack", "jill"));
    writer.endArray();
    JsonStreamReader reader = new JsonStreamReader(writer.toString());
    Assert.assertTrue(reader.startArray());
    Assert.assertTrue(reader.notEndOfArray());
    NtPrincipal A = reader.readNtPrincipal();
    NtPrincipal B = reader.readNtPrincipal();
    NtPrincipal C = reader.readNtPrincipal();
    Assert.assertFalse(reader.notEndOfArray());
    Assert.assertTrue(A == B && B == C);
  }

  @Test
  public void dupeString() {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginArray();
    writer.writeString("XYZ");
    writer.writeString("XYZ");
    writer.writeString("XYZ");
    writer.endArray();
    JsonStreamReader reader = new JsonStreamReader(writer.toString());
    Assert.assertTrue(reader.startArray());
    Assert.assertTrue(reader.notEndOfArray());
    String A = reader.readString();
    String B = reader.readString();
    String C = reader.readString();
    Assert.assertFalse(reader.notEndOfArray());
    Assert.assertTrue(A == B && B == C);
  }

  @Test
  public void crash() {
    JsonStreamReader reader = new JsonStreamReader("{}");
    reader.fieldName();
    reader.fieldName();
    try {
      reader.fieldName();
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("Unable to satisfy minimum limit", re.getMessage());
    }
  }

  @Test
  public void asset() {
    JsonStreamReader reader =
        new JsonStreamReader(
            "{\"id\":\"123\",\"size\":\"42\",\"name\":\"name\",\"type\":\"png\",\"md5\":\"hash\",\"sha384\":\"sheesh\",\"@gc\":\"@yes\"}");
    NtAsset cmp = new NtAsset("123", "name", "png", 42, "hash", "sheesh");
    NtAsset tst = reader.readNtAsset();
    Assert.assertEquals(cmp, tst);
  }

  @Test
  public void complex() {
    JsonStreamReader reader = new JsonStreamReader("{\"r\":1.2,\"i\":2.4}");
    NtComplex cmp = new NtComplex(1.2, 2.4);
    NtComplex tst = reader.readNtComplex();
    Assert.assertEquals(cmp, tst);
  }

  @Test
  public void asset_ws() {
    JsonStreamReader reader =
        new JsonStreamReader(
            "   { \t \"id\"  \r :  \n  \"123\" \n , \n \"size\" \n : \n \"42\" \n , \n \"name\" \n : \n \"name\",\"type\":\"png\",\"md5\":\"hash\",\"sha384\":\"sheesh\",\"@gc\":\n\"@yes\"}");
    NtAsset cmp = new NtAsset("123", "name", "png", 42, "hash", "sheesh");
    NtAsset tst = reader.readNtAsset();
    Assert.assertEquals(cmp, tst);
  }

  @Test
  public void tree_empty_obj() {
    JsonStreamReader reader = new JsonStreamReader("  {  }  ");
    Object obj = reader.readJavaTree();
    Assert.assertTrue(obj instanceof HashMap);
    Assert.assertTrue(reader.end());
  }

  @Test
  public void tree_obj() {
    JsonStreamReader reader = new JsonStreamReader("{\"x\"  :  123}");
    Object obj = reader.readJavaTree();
    Assert.assertTrue(obj instanceof HashMap);
    Assert.assertTrue(123 == (int) (((HashMap<?, ?>) obj).get("x")));
    Assert.assertTrue(reader.end());
  }

  @Test
  public void tree_empty_array() {
    JsonStreamReader reader = new JsonStreamReader("[]");
    Object obj = reader.readJavaTree();
    Assert.assertTrue(obj instanceof ArrayList);
    Assert.assertTrue(reader.end());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void tree_array() {
    JsonStreamReader reader = new JsonStreamReader("[123]");
    Object obj = reader.readJavaTree();
    Assert.assertTrue(obj instanceof ArrayList);
    Assert.assertTrue(123 == (int) (((ArrayList<Object>) obj).get(0)));
    Assert.assertTrue(reader.end());
  }

  @Test
  public void tree_number_d() {
    JsonStreamReader reader = new JsonStreamReader("1.4");
    Object obj = reader.readJavaTree();
    Assert.assertTrue(1.4 == (Double) obj);
    Assert.assertTrue(reader.end());
  }

  @Test
  public void tree_number_i() {
    JsonStreamReader reader = new JsonStreamReader("12");
    Object obj = reader.readJavaTree();
    Assert.assertTrue(12 == (Integer) obj);
    Assert.assertTrue(reader.end());
  }

  @Test
  public void tree_number_l() {
    JsonStreamReader reader = new JsonStreamReader("4242424242");
    Object obj = reader.readJavaTree();
    Assert.assertTrue(4242424242L == (Long) obj);
    Assert.assertTrue(reader.end());
  }

  @Test
  public void tree_bool_true() {
    JsonStreamReader reader = new JsonStreamReader("true");
    Object obj = reader.readJavaTree();
    Assert.assertTrue((Boolean) obj);
    Assert.assertTrue(reader.end());
  }

  @Test
  public void tree_bool_false() {
    JsonStreamReader reader = new JsonStreamReader("false");
    Object obj = reader.readJavaTree();
    Assert.assertFalse((Boolean) obj);
    Assert.assertTrue(reader.end());
  }

  @Test
  public void tree_null() {
    JsonStreamReader reader = new JsonStreamReader("null");
    Object obj = reader.readJavaTree();
    Assert.assertNull(obj);
    Assert.assertTrue(reader.end());
  }

  @Test
  public void scanOver() {
    JsonStreamReader reader = new JsonStreamReader("{}");
    if (reader.startObject()) {
      Assert.assertFalse(reader.notEndOfObject());
    }
  }
  @Test
  public void skipValueObject() {
    try {
      JsonStreamReader reader = new JsonStreamReader("42");
      reader.mustSkipObject();
      Assert.fail();
    } catch (RuntimeException re) {
    }
    try {
      JsonStreamReader reader = new JsonStreamReader("\"x\"");
      reader.mustSkipObject();
      Assert.fail();
    } catch (RuntimeException re) {
    }
    JsonStreamReader reader = new JsonStreamReader("{}");
    reader.mustSkipObject();
  }
  @Test
  public void skipValueArray() {
    try {
      JsonStreamReader reader = new JsonStreamReader("42");
      reader.mustSkipArray();
      Assert.fail();
    } catch (RuntimeException re) {
    }
    try {
      JsonStreamReader reader = new JsonStreamReader("\"x\"");
      reader.mustSkipArray();
      Assert.fail();
    } catch (RuntimeException re) {
    }
    JsonStreamReader reader = new JsonStreamReader("[]");
    reader.mustSkipArray();
  }
  @Test
  public void skipValue() {
    {
      JsonStreamReader reader = new JsonStreamReader("42");
      Assert.assertEquals("42", reader.readNtDynamic().json);
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
    {
      JsonStreamReader reader = new JsonStreamReader("{\"x\":13.13}");
      reader.skipValue();
      Assert.assertTrue(reader.end());
    }
    {
      JsonStreamReader reader = new JsonStreamReader("{\"x\":42}");
      Assert.assertEquals("{\"x\":42}", reader.readNtDynamic().json);
      Assert.assertTrue(reader.end());
    }
  }

  @Test
  public void mustStart() {
    try {
      JsonStreamReader reader = new JsonStreamReader("42");
      reader.mustStartObject();
      Assert.fail();
    } catch (RuntimeException re) {

    }
    try {
      JsonStreamReader reader = new JsonStreamReader("42");
      reader.mustStartArray();
      Assert.fail();
    } catch (RuntimeException re) {

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
    reader.mustStartObject();
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
    JsonStreamReader reader =
        new JsonStreamReader(
            "{\"x\":\"z\",\"z\":123.4,\"t\":true,\"f\":false,\"n\":null,\"d\":3.14}");
    Assert.assertTrue(reader.testLackOfNull());
    ;
    Assert.assertTrue(reader.startObject());
    Assert.assertTrue(reader.notEndOfObject());
    Assert.assertEquals("x", reader.fieldName());
    Assert.assertEquals("z", reader.readString());
    Assert.assertTrue(reader.notEndOfObject());
    Assert.assertEquals("z", reader.fieldName());
    Assert.assertTrue(reader.testLackOfNull());
    ;
    Assert.assertEquals(123.4, reader.readDouble(), 0.01);
    Assert.assertTrue(reader.notEndOfObject());
    Assert.assertEquals("t", reader.fieldName());
    Assert.assertTrue(reader.testLackOfNull());
    ;
    Assert.assertEquals(true, reader.readBoolean());
    Assert.assertTrue(reader.notEndOfObject());
    Assert.assertEquals("f", reader.fieldName());
    Assert.assertTrue(reader.testLackOfNull());
    ;
    Assert.assertEquals(false, reader.readBoolean());
    Assert.assertTrue(reader.notEndOfObject());
    Assert.assertEquals("n", reader.fieldName());
    Assert.assertFalse(reader.testLackOfNull());
    ;
    Assert.assertTrue(reader.notEndOfObject());
    Assert.assertEquals("d", reader.fieldName());
    Assert.assertTrue(3.14 == reader.readDouble());
    Assert.assertFalse(reader.notEndOfObject());
    Assert.assertTrue(reader.end());
  }

  @Test
  public void readObject4DoubleAsInt() {
    JsonStreamReader reader = new JsonStreamReader("{\"dasi\":3.14}");
    Assert.assertTrue(reader.testLackOfNull());
    ;
    Assert.assertTrue(reader.startObject());
    Assert.assertEquals("dasi", reader.fieldName());
    Assert.assertEquals(3, reader.readInteger());
    Assert.assertFalse(reader.notEndOfObject());
    Assert.assertTrue(reader.end());
  }

  @Test
  public void readArray() {
    JsonStreamReader reader = new JsonStreamReader("[\"x\",\"z\",123,\"4444444\"]");
    reader.mustStartArray();
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
  public void readNtPrincipal() {
    JsonStreamReader reader = new JsonStreamReader("{\"agent\":\"z\",\"authority\":\"g\"}");
    NtPrincipal c = reader.readNtPrincipal();
    Assert.assertEquals("z", c.agent);
    Assert.assertEquals("g", c.authority);
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

  private void assertEcho(String x) {
    JsonStreamReader reader = new JsonStreamReader(x);
    JsonStreamWriter writer = new JsonStreamWriter();
    reader.skipValue(writer);
    Assert.assertEquals(x, writer.toString());
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

  @Test
  public void gcScan() {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginArray();
    writer.beginObject();
    writer.writeObjectFieldIntro("x");
    writer.writeNtAsset(new NtAsset("id-xyz", "name", "type", 42, "md5", "sha"));
    writer.writeObjectFieldIntro("y");
    writer.writeNtAsset(new NtAsset("id-abc", "name", "type", 42, "md5", "sha"));
    writer.writeObjectFieldIntro("dup-y");
    writer.writeNtAsset(new NtAsset("id-abc", "name", "type", 42, "md5", "sha"));
    writer.writeObjectFieldIntro("dup-x");
    writer.writeNtAsset(new NtAsset("id-xyz", "name", "type", 42, "md5", "sha"));
    writer.endObject();
    writer.writeString("nope");
    writer.endArray();
    JsonStreamReader reader = new JsonStreamReader(writer.toString());
    HashSet<String> ids = new HashSet<>();
    reader.populateGarbageCollectedIds(ids);
    Assert.assertEquals(2, ids.size());
    Assert.assertTrue(ids.contains("id-xyz"));
    Assert.assertTrue(ids.contains("id-abc"));
  }

  @Test
  public void readDate1() {
    JsonStreamReader reader = new JsonStreamReader("\"1970/1/23\"");
    NtDate date = reader.readNtDate();
    Assert.assertEquals(1970, date.year);
    Assert.assertEquals(1, date.month);
    Assert.assertEquals(23, date.day);
  }

  @Test
  public void readDate1alt() {
    JsonStreamReader reader = new JsonStreamReader("\"1970-1-23\"");
    NtDate date = reader.readNtDate();
    Assert.assertEquals(1970, date.year);
    Assert.assertEquals(1, date.month);
    Assert.assertEquals(23, date.day);
  }

  @Test
  public void readDate1altFull() {
    JsonStreamReader reader = new JsonStreamReader("\"1970-01-23\"");
    NtDate date = reader.readNtDate();
    Assert.assertEquals(1970, date.year);
    Assert.assertEquals(1, date.month);
    Assert.assertEquals(23, date.day);
  }

  @Test
  public void readDate2() {
    JsonStreamReader reader = new JsonStreamReader("\"1970/07/31\"");
    NtDate date = reader.readNtDate();
    Assert.assertEquals(1970, date.year);
    Assert.assertEquals(7, date.month);
    Assert.assertEquals(31, date.day);
  }

  @Test
  public void readDate3() {
    JsonStreamReader reader = new JsonStreamReader("\"1970\"");
    NtDate date = reader.readNtDate();
    Assert.assertEquals(1970, date.year);
    Assert.assertEquals(1, date.month);
    Assert.assertEquals(1, date.day);
  }

  @Test
  public void readDate4() {
    JsonStreamReader reader = new JsonStreamReader("\"1980/11\"");
    NtDate date = reader.readNtDate();
    Assert.assertEquals(1980, date.year);
    Assert.assertEquals(11, date.month);
    Assert.assertEquals(1, date.day);
  }

  @Test
  public void readTime() {
    JsonStreamReader reader = new JsonStreamReader("\"14:37\"");
    NtTime time = reader.readNtTime();
    Assert.assertEquals(37, time.minute);
    Assert.assertEquals(14, time.hour);
  }

  @Test
  public void readTimeSpan() {
    JsonStreamReader reader = new JsonStreamReader("1235");
    Assert.assertEquals(1235, reader.readNtTimeSpan().seconds, 0.01);
  }

  @Test
  public void readDatetime1() {
    JsonStreamReader reader = new JsonStreamReader("\"2023-04-24T17:57:19.802528800-05:00[America/Chicago]\"");
    Assert.assertEquals(2023, reader.readNtDateTime().dateTime.getYear());
  }

  @Test
  public void readDatetime2() {
    JsonStreamReader reader = new JsonStreamReader("\"2021-04-24T17:57:19.802528800-05:00\"");
    Assert.assertEquals(2021, reader.readNtDateTime().dateTime.getYear());
  }
}

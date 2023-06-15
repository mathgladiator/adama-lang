/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class JsonTests {
  @Test
  public void coverage() throws Exception {
    Json.newJsonObject();
    Json.parseJsonObject("{}");
    boolean failure = true;
    try {
      Json.parseJsonObjectThrows("x");
      failure = false;
    } catch (Exception ex) {

    }
    try {
      Json.parseJsonObjectThrows("[]");
      failure = false;
    } catch (Exception ex) {

    }
    try {
      Json.parseJsonObject("x");
      failure = false;
    } catch (RuntimeException ex) {
    }
    try {
      Json.parseJsonObject("[]");
      failure = false;
    } catch (RuntimeException ex) {
    }
    Json.parseJsonArray("[]");
    try {
      Json.parseJsonArray("{}");
      failure = false;
    } catch (Exception ex) {

    }
    try {
      Json.parseJsonArray("x");
      failure = false;
    } catch (Exception ex) {

    }
    Assert.assertTrue(failure);
  }

  @Test
  public void subfield_bool() {
    Assert.assertNull(Json.readBool(Json.parseJsonObject("{\"x\":\"1234\"}"), "x"));
    Assert.assertNull(Json.readBool(Json.parseJsonObject("{\"x\":123}"), "x"));
    Assert.assertNull(Json.readBool(Json.parseJsonObject("{}"), "x"));
    Assert.assertFalse(Json.readBool(Json.parseJsonObject("{\"x\":false}"), "x"));
    Assert.assertTrue(Json.readBool(Json.parseJsonObject("{\"x\":true}"), "x"));
  }

  @Test
  public void subfield_str() {
    Assert.assertEquals("1234", Json.readString(Json.parseJsonObject("{\"x\":\"1234\"}"), "x"));
    Assert.assertEquals("123", Json.readString(Json.parseJsonObject("{\"x\":123}"), "x"));
    Assert.assertNull(Json.readString(Json.parseJsonObject("{}"), "x"));
  }

  @Test
  public void subfield_lng() {
    Assert.assertEquals(1234, (long) Json.readLong(Json.parseJsonObject("{\"x\":\"1234\"}"), "x"));
    Assert.assertEquals(123L, (long) Json.readLong(Json.parseJsonObject("{\"x\":123}"), "x"));
    Assert.assertNull(Json.readLong(Json.parseJsonObject("{}"), "x"));
    Assert.assertNull(Json.readLong(Json.parseJsonObject("{\"x\":null}"), "x"));
    Assert.assertNull(Json.readLong(Json.parseJsonObject("{\"x\":true}"), "x"));
    Assert.assertNull(Json.readLong(Json.parseJsonObject("{\"x\":\"zep\"}"), "x"));
  }

  @Test
  public void subfield_int() {
    Assert.assertEquals(1234, (int) Json.readInteger(Json.parseJsonObject("{\"x\":\"1234\"}"), "x"));
    Assert.assertEquals(123, (int) Json.readInteger(Json.parseJsonObject("{\"x\":123}"), "x"));
    Assert.assertNull(Json.readInteger(Json.parseJsonObject("{}"), "x"));
    Assert.assertNull(Json.readInteger(Json.parseJsonObject("{\"x\":null}"), "x"));
    Assert.assertNull(Json.readInteger(Json.parseJsonObject("{\"x\":\"zep\"}"), "x"));
  }

  @Test
  public void subfield_objs() {
    Assert.assertNotNull(Json.readObject(Json.parseJsonObject("{\"x\":{}}"), "x"));
    Assert.assertNull(Json.readObject(Json.parseJsonObject("{}"), "x"));
    Assert.assertNotNull(Json.readJsonNode(Json.parseJsonObject("{\"x\":{}}"), "x"));
  }
}

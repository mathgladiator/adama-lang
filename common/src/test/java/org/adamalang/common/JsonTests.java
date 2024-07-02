/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

public class JsonTests {
  @Test
  public void coverage() throws Exception {
    Json.newJsonObject();
    Json.parseJsonObject("{}");
    Json.newJsonArray();
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
  public void just_parse() {
    Json.parse("1");
    Json.parse("[]");
    Json.parse("{}");
    try {
      Json.parse("x");
      Assert.fail();
    } catch (Exception ex) {

    }
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
  public void str_remove2() {
    Assert.assertNull(Json.readStringAndRemove(Json.parseJsonObject("{\"y\":\"1234\"}"), "x"));
    Assert.assertEquals("true", Json.readStringAndRemove(Json.parseJsonObject("{\"x\":true}"), "x"));
  }

  @Test
  public void str_remove1() {
    ObjectNode node = Json.parseJsonObject("{\"x\":\"1234\"}");
    Assert.assertEquals("1234", Json.readStringAndRemove(node, "x"));
    Assert.assertEquals("{}", node.toString());
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
  public void subfield_int_def() {
    Assert.assertEquals(123, Json.readInteger(Json.parseJsonObject("{}"), "x", 123));
    Assert.assertEquals(42, Json.readInteger(Json.parseJsonObject("{\"x\":42}"), "x", 123));
  }

  @Test
  public void subfield_bool_def() {
    Assert.assertEquals(false, Json.readBool(Json.parseJsonObject("{}"), "x", false));
    Assert.assertEquals(true, Json.readBool(Json.parseJsonObject("{}"), "x", true));
    Assert.assertEquals(true, Json.readBool(Json.parseJsonObject("{\"x\":true}"), "x", true));
    Assert.assertEquals(true, Json.readBool(Json.parseJsonObject("{\"x\":true}"), "x", false));
  }

  @Test
  public void subfield_objs() {
    Assert.assertNotNull(Json.readObject(Json.parseJsonObject("{\"x\":{}}"), "x"));
    Assert.assertNull(Json.readObject(Json.parseJsonObject("{}"), "x"));
    Assert.assertNotNull(Json.readJsonNode(Json.parseJsonObject("{\"x\":{}}"), "x"));
  }
}

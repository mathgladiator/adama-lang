/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.io;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class JsonRequestTests {
  public static final ConnectionContext CONTEXT = new ConnectionContext( "http://blah","1.1.1.1", "agent", "assets");

  @Test
  public void noMethod() throws Exception {
    JsonRequest request = new JsonRequest(of("{}"), CONTEXT);
    try {
      request.method();
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(213708, ece.code);
    }
  }

  private static ObjectNode of(String json) throws Exception {
    return Json.parseJsonObject(json);
  }

  @Test
  public void noID() throws Exception {
    JsonRequest request = new JsonRequest(of("{}"), CONTEXT);
    try {
      request.id();
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(233120, ece.code);
    }
  }

  @Test
  public void bothIdAndMethod() throws Exception {
    JsonRequest request = new JsonRequest(of("{\"id\":123,\"method\":\"m\"}"), CONTEXT);
    Assert.assertEquals(123, request.id());
    Assert.assertEquals("m", request.method());
  }

  @Test
  public void getString() throws Exception {
    JsonRequest request = new JsonRequest(of("{\"x\":true,\"y\":null,\"z\":42.5,\"w\":\"xyz\"}"), CONTEXT);
    try {
      request.getString("x", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getString("x", false, 123));
    try {
      request.getString("y", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getString("y", false, 123));
    Assert.assertEquals("42.5", request.getString("z", true, 4));
    Assert.assertEquals("xyz", request.getString("w", true, 42));
    Assert.assertEquals("xyz", request.getString("w", false, 42));
    try {
      request.getString("t", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getString("t", false, 123));
  }

  @Test
  public void getStringNormalize() throws Exception {
    JsonRequest request = new JsonRequest(of("{\"w\":\" XyzNow \"}"), CONTEXT);
    Assert.assertEquals("xyznow", request.getStringNormalize("w", true, 1));
  }

  @Test
  public void getBoolean() throws Exception {
    JsonRequest request = new JsonRequest(of("{\"x\":true,\"y\":null,\"z\":42.5,\"w\":\"xyz\"}"), CONTEXT);
    Assert.assertTrue(request.getBoolean("x", true, 123));
    Assert.assertNull(request.getBoolean("y", false, -1));
    Assert.assertNull(request.getBoolean("z", false, -1));
    try {
      Assert.assertNull(request.getBoolean("y", true, -123));
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(-123, exc.code);
    }
  }

  @Test
  public void logging() throws Exception {
    JsonRequest request = new JsonRequest(of("{\"x\":true,\"y\":null,\"z\":42.5,\"w\":4,\"w2\":\"5\",\"w3\":\"x\"}"), CONTEXT);
    ObjectNode logItem = Json.newJsonObject();
    request.dumpIntoLog(logItem);
    Assert.assertEquals("{\"ip\":\"1.1.1.1\",\"origin\":\"http://blah\"}", logItem.toString());
  }

  @Test
  public void getInteger() throws Exception {
    JsonRequest request =
        new JsonRequest(of("{\"x\":true,\"y\":null,\"z\":42.5,\"w\":4,\"w2\":\"5\",\"w3\":\"x\"}"), CONTEXT);
    try {
      request.getInteger("x", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getInteger("x", false, 123));
    try {
      request.getInteger("y", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getInteger("y", false, 123));
    try {
      request.getInteger("z", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertEquals(4, (int) request.getInteger("w", true, 42));
    Assert.assertEquals(4, (int) request.getInteger("w", false, 42));
    Assert.assertEquals(5, (int) request.getInteger("w2", true, 42));
    Assert.assertEquals(5, (int) request.getInteger("w2", false, 42));
    try {
      request.getInteger("w3", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    try {
      request.getInteger("t", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getInteger("t", false, 123));
  }

  @Test
  public void getLong() throws Exception {
    JsonRequest request =
        new JsonRequest(of("{\"x\":true,\"y\":null,\"z\":42.5,\"w\":4,\"w2\":\"5\",\"w3\":\"x\"}"), CONTEXT);
    try {
      request.getLong("x", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getLong("x", false, 123));
    try {
      request.getLong("y", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getLong("y", false, 123));
    try {
      request.getLong("z", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertEquals(4, (long) request.getLong("w", true, 42));
    Assert.assertEquals(4, (long) request.getLong("w", false, 42));
    Assert.assertEquals(5, (long) request.getLong("w2", true, 42));
    Assert.assertEquals(5, (long) request.getLong("w2", false, 42));
    try {
      request.getLong("w3", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    try {
      request.getLong("t", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getLong("t", false, 123));
  }

  @Test
  public void getObject() throws Exception {
    JsonRequest request = new JsonRequest(of("{\"x\":true,\"y\":null,\"z\":{}}"), CONTEXT);
    try {
      request.getObject("x", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getObject("x", false, 123));
    try {
      request.getObject("y", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getObject("y", false, 123));
    Assert.assertNotNull(request.getObject("z", true, 2));
    Assert.assertNotNull(request.getObject("z", false, 2));
  }

  @Test
  public void getNode() throws Exception {
    JsonRequest request = new JsonRequest(of("{\"x\":true,\"y\":null,\"z1\":{},\"z2\":[]}"), CONTEXT);
    try {
      request.getJsonNode("x", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getObject("x", false, 123));
    try {
      request.getJsonNode("y", true, 123);
    } catch (ErrorCodeException exc) {
      Assert.assertEquals(123, exc.code);
    }
    Assert.assertNull(request.getJsonNode("y", false, 123));
    Assert.assertNotNull(request.getJsonNode("z1", true, 2));
    Assert.assertNotNull(request.getJsonNode("z1", false, 2));
    Assert.assertNotNull(request.getJsonNode("z2", true, 2));
    Assert.assertNotNull(request.getJsonNode("z2", false, 2));
  }
}

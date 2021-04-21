package org.adamalang.api.commands;

import org.adamalang.api.util.Json;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class RequestTests {
  @Test
  public void present() throws ErrorCodeException {
    Request request = new Request(Json.parseJsonObject("{\"method\":\"foo\",\"space\":\"game\",\"id\":42,\"key\":\"10000\",\"entropy\":\"jazz\",\"marker\":\"mmm\",\"channel\":\"ch\",\"message\":{},\"arg\":{}}"));
    Assert.assertEquals("foo", request.method());
    Assert.assertEquals("game", request.space());
    Assert.assertEquals("mmm", request.marker());
    Assert.assertEquals("ch", request.channel());
    Assert.assertEquals("jazz", request.entropy());
    Assert.assertEquals(42, request.id());
    Assert.assertEquals(10000L, request.key());
    Assert.assertEquals("{}", request.json_arg());
    Assert.assertEquals("{}", request.json_message());
  }

  @Test
  public void lng_as_ints() throws ErrorCodeException {
    Request request = new Request(Json.parseJsonObject("{\"key\":10000}"));
    Assert.assertEquals(10000L, request.key());
  }

  @Test
  public void lng_inv() throws ErrorCodeException {
    Request request = new Request(Json.parseJsonObject("{\"key\":\"x\"}"));
    try {
      request.key();
      Assert.fail();
    } catch (ErrorCodeException ece) {
    }
  }

  @Test
  public void json_invalid() throws ErrorCodeException {
    Request request = new Request(Json.parseJsonObject("{\"message\":\"x\",\"arg\":[]}"));
    try {
      request.json_message();
      Assert.fail();
    } catch (ErrorCodeException ece) {
    }
    try {
      request.json_arg();
      Assert.fail();
    } catch (ErrorCodeException ece) {
    }
  }

  @Test
  public void missing() throws ErrorCodeException {
    Request request = new Request(Json.parseJsonObject("{}"));
    try {
      request.method();
      Assert.fail();
    } catch (ErrorCodeException ece) {
    }
    try {
      request.space();
      Assert.fail();
    } catch (ErrorCodeException ece) {
    }
    try {
      request.marker();
      Assert.fail();
    } catch (ErrorCodeException ece) {
    }
    try {
      request.channel();
      Assert.fail();
    } catch (ErrorCodeException ece) {
    }
    Assert.assertNull(request.entropy());
    try {
      request.key();
      Assert.fail();
    } catch (ErrorCodeException ece) {
    }
    try {
      request.id();
      Assert.fail();
    } catch (ErrorCodeException ece) {
    }
  }
}

package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class XWWWFormUrlTests {
  @Test
  public void simple() {
    String result = XWWWFormUrl.encode(Json.parseJsonObject("{}"));
    Assert.assertEquals("", result);
  }

  @Test
  public void one() {
    String result = XWWWFormUrl.encode(Json.parseJsonObject("{\"x\":123}"));
    Assert.assertEquals("x=123", result);
  }

  @Test
  public void two() {
    String result = XWWWFormUrl.encode(Json.parseJsonObject("{\"x\":123,\"y\":\"xyz\"}"));
    Assert.assertEquals("x=123&y=xyz", result);
  }

  @Test
  public void three() {
    String result = XWWWFormUrl.encode(Json.parseJsonObject("{\"x\":4.2,\"y\":\"xyz\",\"z\":true}"));
    Assert.assertEquals("x=4.2&y=xyz&z=true", result);
  }

  @Test
  public void compound() {
    String result = XWWWFormUrl.encode(Json.parseJsonObject("{\"o\":{\"x\":4.2,\"y\":\"xyz\",\"z\":true}}"));
    Assert.assertEquals("o.x=4.2&o.y=xyz&o.z=true", result);
  }
}

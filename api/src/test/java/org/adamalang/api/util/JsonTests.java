package org.adamalang.api.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

public class JsonTests {
  @Test
  public void coverage() throws Exception {
    new Json();
    ObjectNode node = Json.newJsonObject();
    Json.parseJsonObject("{}");
    Json.parseJsonObjectThrows("{}");
  }

  @Test
  public void parsingIssuesNotObject() {
    try {
      Json.parseJsonObjectThrows("[]");
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertEquals("given json is not an ObjectNode at root", ex.getMessage());
    }
  }

  @Test
  public void parsingIssuesBadItem() {
    try {
      Json.parseJsonObject("{");
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertEquals("com.fasterxml.jackson.core.io.JsonEOFException: Unexpected end-of-input: expected close marker for Object (start marker at [Source: (String)\"{\"; line: 1, column: 1])\n" +
              " at [Source: (String)\"{\"; line: 1, column: 2]", ex.getMessage());
    }
  }
}

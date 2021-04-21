/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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

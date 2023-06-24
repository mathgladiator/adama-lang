/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.edhtml.phases.generate;

import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class FieldTests {
  @Test
  public void flow() {
    Field a = new Field("name", "path", "int", new Annotations());
    Field b = new Field("name", "path", "int", new Annotations().of(Json.parseJsonArray("[\"XYZ\"]")));
    Field c = Field.union(a, b);
    HashMap<String, String> map = c.map(new HashMap<>());
    Assert.assertEquals("name", map.get("name"));
  }
}

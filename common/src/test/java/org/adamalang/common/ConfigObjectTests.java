/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

public class ConfigObjectTests {
  @Test
  public void string() {
    ObjectNode root = Json.newJsonObject();
    ConfigObject config = new ConfigObject(root);
    Assert.assertEquals(null, config.strOf("key", null));
    Assert.assertEquals("123", config.strOf("key", "123"));
    root.put("key", "42");
    Assert.assertEquals("42", config.strOf("key", "123"));
    root.putNull("key");
    Assert.assertEquals("123", config.strOf("key", "123"));
    root.remove("key");
    Assert.assertEquals("123", config.strOf("key", "123"));
  }

  @Test
  public void integer() {
    ObjectNode root = Json.newJsonObject();
    ConfigObject config = new ConfigObject(root);
    Assert.assertEquals(42, config.intOf("key", 42));
    root.put("key", 123);
    Assert.assertEquals(123, config.intOf("key", 42));
    root.putNull("key");
    Assert.assertEquals(42, config.intOf("key", 42));
    root.remove("key");
    Assert.assertEquals(42, config.intOf("key", 42));
    root.put("key", 123);
    Assert.assertEquals(123, config.intOf("key", 42));
  }

  @Test
  public void child() {
    ObjectNode root = Json.newJsonObject();
    ConfigObject config = new ConfigObject(root);
    ConfigObject child1 = config.child("key");
    ConfigObject child2 = config.child("key");
    Assert.assertTrue(child1.node == child2.node);
    Assert.assertEquals(42, child1.intOf("key", 42));
    child1.node.put("key", 123);
    Assert.assertEquals(123, child2.intOf("key", 42));
  }
}

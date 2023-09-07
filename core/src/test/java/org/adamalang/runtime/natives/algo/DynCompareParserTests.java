/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.natives.algo;

import org.junit.Assert;
import org.junit.Test;

public class DynCompareParserTests {
  @Test
  public void empty() {
    CompareField[] result = DynCompareParser.parse("");
    Assert.assertEquals(0, result.length);
  }
  @Test
  public void zero_content() {
    CompareField[] result = DynCompareParser.parse(",,   ,,   ,,,");
    Assert.assertEquals(0, result.length);
  }

  @Test
  public void single() {
    CompareField[] result = DynCompareParser.parse("a");
    Assert.assertEquals(1, result.length);
    Assert.assertEquals("a", result[0].name);
    Assert.assertFalse(result[0].desc);
  }
  @Test
  public void single_ws() {
    CompareField[] result = DynCompareParser.parse("   a   ");
    Assert.assertEquals(1, result.length);
    Assert.assertEquals("a", result[0].name);
    Assert.assertFalse(result[0].desc);
  }
  @Test
  public void single_desc() {
    CompareField[] result = DynCompareParser.parse("-a");
    Assert.assertEquals(1, result.length);
    Assert.assertEquals("a", result[0].name);
    Assert.assertTrue(result[0].desc);
  }
  @Test
  public void single_desc_ws() {
    CompareField[] result = DynCompareParser.parse(" -  a   ");
    Assert.assertEquals(1, result.length);
    Assert.assertEquals("a", result[0].name);
    Assert.assertTrue(result[0].desc);
  }

  @Test
  public void trimix() {
    CompareField[] result = DynCompareParser.parse("+abc,-def,gh");
    Assert.assertEquals(3, result.length);
    Assert.assertEquals("abc", result[0].name);
    Assert.assertEquals("def", result[1].name);
    Assert.assertEquals("gh", result[2].name);
    Assert.assertFalse(result[0].desc);
    Assert.assertTrue(result[1].desc);
    Assert.assertFalse(result[2].desc);
  }
}

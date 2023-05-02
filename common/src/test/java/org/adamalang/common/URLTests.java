/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

public class URLTests {
  @Test
  public void spaces_20() throws Exception {
    Assert.assertEquals("hello%20world", URL.encode("hello world", false));
  }

  @Test
  public void path() throws Exception {
    Assert.assertEquals("/ok/now/we/go", URL.encode("/ok/now/we/go", true));
  }

  @Test
  public void keeper() throws Exception {
    Assert.assertEquals("abcdef._~blah/", URL.encode("abcdef._~blah/", true));
  }

  @Test
  public void slasher() throws Exception {
    Assert.assertEquals("//", URL.encode("//", true));
    Assert.assertEquals("%2F%2F", URL.encode("//", false));
  }

  @Test
  public void unicode() throws Exception {
    Assert.assertEquals("%E7%8C%BF%E3%82%82%E6%9C%A8%E3%81%8B%E3%82%89%E8%90%BD%E3%81%A1%E3%82%8B", URL.encode("猿も木から落ちる", false));
  }

  @Test
  public void surrogate() throws Exception {
    Assert.assertEquals("%F0%90%80%80", URL.encode("\ud800\udc00", false));
  }

  @Test
  public void plain() throws Exception {
    Assert.assertTrue(URL.plain('c', false));
    Assert.assertTrue(URL.plain('4', false));
    Assert.assertTrue(URL.plain('C', false));
    Assert.assertTrue(URL.plain('_', false));
    Assert.assertTrue(URL.plain('/', true));
    Assert.assertFalse(URL.plain('/', false));
    Assert.assertTrue(URL.plain('~', false));
    Assert.assertTrue(URL.plain('.', false));
    Assert.assertFalse(URL.plain('@', false));
  }

  @Test
  public void params() {
    Assert.assertEquals("", URL.parameters(null));
    Assert.assertEquals("", URL.parameters(new HashMap<>()));
    Assert.assertEquals("?xyz=abc", URL.parameters(Collections.singletonMap("xyz", "abc")));
    TreeMap<String, String> multi = new TreeMap<>();
    multi.put("a", "b");
    multi.put("c", "d");
    multi.put("e", "f");
    Assert.assertEquals("?a=b&c=d&e=f", URL.parameters(multi));
  }
}

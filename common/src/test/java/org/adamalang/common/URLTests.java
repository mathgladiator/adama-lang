/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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

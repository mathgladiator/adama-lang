/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class LibStringTests {
  @Test
  public void reverse2() {
    Assert.assertEquals("zyx", LibString.reverse("xyz"));
    Assert.assertEquals("zyx", LibString.reverse(new NtMaybe<>("xyz")).get());
    Assert.assertFalse(LibString.reverse(new NtMaybe<>()).has());
  }

  @Test
  public void charOf() {
    Assert.assertEquals("\"", LibString.charOf(34));
    Assert.assertEquals("\"", LibString.charOf(new NtMaybe<>(34)).get());
    Assert.assertFalse(LibString.charOf(new NtMaybe<>()).has());
  }

  @Test
  public void multiply() {
    Assert.assertEquals("xxx", LibString.multiply("x", 3));
    Assert.assertEquals("xxx", LibString.multiply(new NtMaybe<>("x"), 3).get());
    Assert.assertFalse(LibString.multiply(new NtMaybe<>(), 3).has());
  }

  private void assertListEquals(NtList<String> list, String... parts) {
    Assert.assertEquals(parts.length, list.size());
    for (int k = 0; k < parts.length; k++) {
      Assert.assertEquals(parts[k], list.lookup(k).get());
    }
  }

  @Test
  public void split() {
    assertListEquals(LibString.split("a,b,c", ","), "a", "b", "c");
    assertListEquals(LibString.split("aXYbXYc", "XY"), "a", "b", "c");

    assertListEquals(LibString.split(new NtMaybe<>("a,b,c"), ",").get(), "a", "b", "c");
    assertListEquals(LibString.split("aXYbXYc", new NtMaybe<>("XY")).get(), "a", "b", "c");
    assertListEquals(LibString.split(new NtMaybe<>("aXYbXYc"), new NtMaybe<>("XY")).get(), "a", "b", "c");

    Assert.assertFalse(LibString.split(new NtMaybe<>(), new NtMaybe<>("XY")).has());
    Assert.assertFalse(LibString.split(new NtMaybe<>("aXYbXYc"), new NtMaybe<>()).has());
    Assert.assertFalse(LibString.split(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibString.split(new NtMaybe<>(), "XY").has());
    Assert.assertFalse(LibString.split("aXYbXYc", new NtMaybe<>()).has());
    Assert.assertFalse(LibString.split(new NtMaybe<>(), new NtMaybe<>()).has());
  }

  @Test
  public void contains() {
    Assert.assertTrue(LibString.contains("a needle yo", "need"));
    Assert.assertFalse(LibString.contains("a needle yo", "ninja"));
    Assert.assertTrue(LibString.contains(new NtMaybe<>("a needle yo"), "need").get());
    Assert.assertFalse(LibString.contains(new NtMaybe<>("a needle yo"), "ninja").get());
    Assert.assertTrue(LibString.contains("a needle yo", new NtMaybe<>("need")).get());
    Assert.assertFalse(LibString.contains("a needle yo", new NtMaybe<>("ninja")).get());
    Assert.assertTrue(LibString.contains(new NtMaybe<>("a needle yo"), new NtMaybe<>("need")).get());
    Assert.assertFalse(LibString.contains(new NtMaybe<>("a needle yo"), new NtMaybe<>("ninja")).get());
    Assert.assertFalse(LibString.contains(new NtMaybe<>(), "x").has());
    Assert.assertFalse(LibString.contains("x", new NtMaybe<>()).has());
    Assert.assertFalse(LibString.contains(new NtMaybe<>(), new NtMaybe<>()).has());
  }



  @Test
  public void indexOf() {
    Assert.assertEquals(2, (int) LibString.indexOf("a needle yo", "need").get());
    Assert.assertFalse(LibString.indexOf("a needle yo", "ninja").has());
    Assert.assertEquals(2, (int) LibString.indexOf(new NtMaybe<>("a needle yo"), "need").get());
    Assert.assertFalse(LibString.indexOf(new NtMaybe<>("a needle yo"), "ninja").has());
    Assert.assertEquals(2, (int) LibString.indexOf("a needle yo", new NtMaybe<>("need")).get());
    Assert.assertFalse(LibString.indexOf("a needle yo", new NtMaybe<>("ninja")).has());
    Assert.assertEquals(2, (int) LibString.indexOf(new NtMaybe<>("a needle yo"), new NtMaybe<>("need")).get());
    Assert.assertFalse(LibString.indexOf(new NtMaybe<>("a needle yo"), new NtMaybe<>("ninja")).has());
    Assert.assertFalse(LibString.indexOf(new NtMaybe<>(), "x").has());
    Assert.assertFalse(LibString.indexOf("x", new NtMaybe<>()).has());
    Assert.assertFalse(LibString.indexOf(new NtMaybe<>(), new NtMaybe<>()).has());
  }

  @Test
  public void trim() {
    Assert.assertEquals("1", LibString.trim("   1  \r\n\t"));
    Assert.assertEquals("1", LibString.trim(new NtMaybe<>("   1  \n\t")).get());
    Assert.assertFalse(LibString.trim(new NtMaybe<>()).has());
  }

  @Test
  public void trimLeft() {
    Assert.assertEquals("1   ", LibString.trimLeft("   1   "));
    Assert.assertEquals("1   ", LibString.trimLeft(new NtMaybe<>("   1   ")).get());
    Assert.assertFalse(LibString.trimLeft(new NtMaybe<>()).has());
  }

  @Test
  public void trimRight() {
    Assert.assertEquals("   1", LibString.trimRight("   1   "));
    Assert.assertEquals("   1", LibString.trimRight(new NtMaybe<>("   1   ")).get());
    Assert.assertFalse(LibString.trimRight(new NtMaybe<>()).has());
  }

  @Test
  public void upper() {
    Assert.assertEquals("ABCD", LibString.upper("aBcD"));
    Assert.assertEquals("ABCD", LibString.upper(new NtMaybe<>("aBcD")).get());
    Assert.assertFalse(LibString.upper(new NtMaybe<>()).has());
  }

  @Test
  public void lower() {
    Assert.assertEquals("abcd", LibString.lower("aBcD"));
    Assert.assertEquals("abcd", LibString.lower(new NtMaybe<>("aBcD")).get());
    Assert.assertFalse(LibString.lower(new NtMaybe<>()).has());
  }

  @Test
  public void substr() {
    Assert.assertEquals("1", LibString.substr("123456", 0, 1).get());
    Assert.assertEquals("12", LibString.substr("123456", 0, 2).get());
    Assert.assertEquals("123", LibString.substr("123456", 0, 3).get());
    Assert.assertEquals("23", LibString.substr("123456", 1, 3).get());
    Assert.assertEquals("56", LibString.substr("123456", 4, 6).get());
    Assert.assertFalse(LibString.substr("1234", -1, 1).has());
    Assert.assertFalse(LibString.substr("1234", 1, -1).has());
    Assert.assertFalse(LibString.substr("1234", 100, 1).has());
    Assert.assertFalse(LibString.substr("1234", 0, 100).has());
    Assert.assertFalse(LibString.substr(new NtMaybe<>("1234"), -1, 1).has());
    Assert.assertFalse(LibString.substr(new NtMaybe<>("1234"), 0, 1000).has());
    Assert.assertFalse(LibString.substr(new NtMaybe<>(), 0, 1).has());
  }

  @Test
  public void mid() {
    Assert.assertEquals("1", LibString.mid("123456", 1, 1).get());
    Assert.assertEquals("12", LibString.mid("123456", 1, 2).get());
    Assert.assertEquals("123", LibString.mid("123456", 1, 3).get());
    Assert.assertEquals("23", LibString.mid("123456", 2, 2).get());
    Assert.assertEquals("56", LibString.mid("123456", 5, 2).get());
    Assert.assertEquals("56", LibString.mid("123456", 5, 1000).get());
    Assert.assertFalse(LibString.mid("1234", -1, 1).has());
    Assert.assertFalse(LibString.mid("1234", 1, -1).has());
    Assert.assertFalse(LibString.mid(new NtMaybe<>("1234"), -1, 1).has());
    Assert.assertFalse(LibString.mid(new NtMaybe<>(), -1, 1).has());
  }

  @Test
  public void left() {
    Assert.assertEquals("123", LibString.left("123456", 3).get());
    Assert.assertEquals("123", LibString.left(new NtMaybe<>("123456"), 3).get());
    Assert.assertFalse(LibString.left("1234", -1).has());
    Assert.assertFalse(LibString.left(new NtMaybe<>("1234"), -1).has());
    Assert.assertFalse(LibString.left(new NtMaybe<>(), -1).has());
  }

  @Test
  public void right() {
    Assert.assertEquals("456", LibString.right("123456", 3).get());
    Assert.assertEquals("456", LibString.right(new NtMaybe<>("123456"), 3).get());
    Assert.assertFalse(LibString.right("1234", -1).has());
    Assert.assertFalse(LibString.right(new NtMaybe<>("1234"), -1).has());
    Assert.assertFalse(LibString.right(new NtMaybe<>(), 4).has());
  }

  @Test
  public void compare() {
    Assert.assertEquals(0, LibString.compare(null, null));
    Assert.assertEquals(0, LibString.compare("x", "x"));
    Assert.assertEquals(-1, LibString.compare("x", "y"));
    Assert.assertEquals(1, LibString.compare("y", "x"));
    Assert.assertEquals(1, LibString.compare("x", null));
    Assert.assertEquals(-1, LibString.compare(null, "x"));
  }

  @Test
  public void equality() {
    Assert.assertTrue(LibString.equality(null, null));
    Assert.assertTrue(LibString.equality("x", "x"));
    Assert.assertFalse(LibString.equality("x", "y"));
    Assert.assertFalse(LibString.equality("x", null));
    Assert.assertFalse(LibString.equality(null, "y"));
  }

  @Test
  public void mult() {
    Assert.assertEquals("", LibString.multiply("x", 0));
    Assert.assertEquals("x", LibString.multiply("x", 1));
    Assert.assertEquals("xx", LibString.multiply("x", 2));
    Assert.assertEquals("xxx", LibString.multiply("x", 3));
    Assert.assertEquals("xxxxx", LibString.multiply("x", 5));
  }

  @Test
  public void of() {
    Assert.assertEquals("1", LibString.of(1));
    Assert.assertEquals("1", LibString.of(1L));
    Assert.assertEquals("1.5", LibString.of(1.5));
    Assert.assertEquals("true", LibString.of(true));
  }

  @Test
  public void reverse() {
    Assert.assertEquals("zen", LibString.reverse("nez"));
  }
}

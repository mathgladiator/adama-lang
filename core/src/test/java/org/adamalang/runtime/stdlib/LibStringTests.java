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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

public class LibStringTests {

  @Test
  public void listmaybe_data() {
    ArrayList<NtMaybe<String>> storage = new ArrayList<>();
    storage.add(new NtMaybe<>());
    storage.add(new NtMaybe<>("x"));
    storage.add(new NtMaybe<>());
    storage.add(new NtMaybe<>("y"));
    storage.add(new NtMaybe<>());
    NtList<NtMaybe<String>> list = new ArrayNtList<>(storage);
    Assert.assertEquals("x, y", LibString.joinMaybes(list, ", ").get());
  }

  @Test
  public void listmaybe_empty() {
    ArrayList<NtMaybe<String>> storage = new ArrayList<>();
    storage.add(new NtMaybe<>());
    storage.add(new NtMaybe<>());
    storage.add(new NtMaybe<>());
    NtList<NtMaybe<String>> list = new ArrayNtList<>(storage);
    Assert.assertFalse(LibString.joinMaybes(list, ", ").has());
  }

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
  public void starts() {
    Assert.assertTrue(LibString.startsWith("xyz", "xy"));
    Assert.assertFalse(LibString.startsWith("zxyz", "xy"));
    Assert.assertTrue(LibString.startsWith(new NtMaybe<>("xyz"), "xy").get());
    Assert.assertFalse(LibString.startsWith(new NtMaybe<>("zxyz"), "xy").get());
    Assert.assertFalse(LibString.startsWith(new NtMaybe<>(), "xy").has());
  }

  @Test
  public void ends() {
    Assert.assertTrue(LibString.endsWith("xyz", "yz"));
    Assert.assertFalse(LibString.endsWith("zxyz", "xy"));
    Assert.assertTrue(LibString.endsWith(new NtMaybe<>("xyz"), "yz").get());
    Assert.assertFalse(LibString.endsWith(new NtMaybe<>("zxyz"), "xy").get());
    Assert.assertFalse(LibString.endsWith(new NtMaybe<>(), "xy").has());
  }

  @Test
  public void multiply() {
    Assert.assertEquals("xxx", LibString.multiply("x", 3));
    Assert.assertEquals("xxx", LibString.multiply(new NtMaybe<>("x"), 3).get());
    Assert.assertFalse(LibString.multiply(new NtMaybe<>(), 3).has());
  }

  @Test
  public void split() {
    assertListEquals(LibString.split("a,b,c", ","), "a", "b", "c");
    assertListEquals(LibString.split("aXYbXYc", "XY"), "a", "b", "c");

    assertListEquals(LibString.split(new NtMaybe<>("a,b,c"), ",").get(), "a", "b", "c");
    assertListEquals(LibString.split("aXYbXYc", new NtMaybe<>("XY")).get(), "a", "b", "c");
    assertListEquals(
        LibString.split(new NtMaybe<>("aXYbXYc"), new NtMaybe<>("XY")).get(), "a", "b", "c");

    Assert.assertFalse(LibString.split(new NtMaybe<>(), new NtMaybe<>("XY")).has());
    Assert.assertFalse(LibString.split(new NtMaybe<>("aXYbXYc"), new NtMaybe<>()).has());
    Assert.assertFalse(LibString.split(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibString.split(new NtMaybe<>(), "XY").has());
    Assert.assertFalse(LibString.split("aXYbXYc", new NtMaybe<>()).has());
    Assert.assertFalse(LibString.split(new NtMaybe<>(), new NtMaybe<>()).has());
  }

  private void assertListEquals(NtList<String> list, String... parts) {
    Assert.assertEquals(parts.length, list.size());
    for (int k = 0; k < parts.length; k++) {
      Assert.assertEquals(parts[k], list.lookup(k).get());
    }
  }

  @Test
  public void contains() {
    Assert.assertTrue(LibString.contains("a needle yo", "need"));
    Assert.assertFalse(LibString.contains("a needle yo", "ninja"));
    Assert.assertTrue(LibString.contains(new NtMaybe<>("a needle yo"), "need").get());
    Assert.assertFalse(LibString.contains(new NtMaybe<>("a needle yo"), "ninja").get());
    Assert.assertTrue(LibString.contains("a needle yo", new NtMaybe<>("need")).get());
    Assert.assertFalse(LibString.contains("a needle yo", new NtMaybe<>("ninja")).get());
    Assert.assertTrue(
        LibString.contains(new NtMaybe<>("a needle yo"), new NtMaybe<>("need")).get());
    Assert.assertFalse(
        LibString.contains(new NtMaybe<>("a needle yo"), new NtMaybe<>("ninja")).get());
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
    Assert.assertEquals(
        2, (int) LibString.indexOf(new NtMaybe<>("a needle yo"), new NtMaybe<>("need")).get());
    Assert.assertFalse(
        LibString.indexOf(new NtMaybe<>("a needle yo"), new NtMaybe<>("ninja")).has());
    Assert.assertFalse(LibString.indexOf(new NtMaybe<>(), "x").has());
    Assert.assertFalse(LibString.indexOf("x", new NtMaybe<>()).has());
    Assert.assertFalse(LibString.indexOf(new NtMaybe<>(), new NtMaybe<>()).has());

    Assert.assertEquals(2, (int) LibString.indexOf("a needle yo", "need", 1).get());
    Assert.assertFalse(LibString.indexOf("a needle yo", "ninja", 1).has());
    Assert.assertEquals(2, (int) LibString.indexOf(new NtMaybe<>("a needle yo"), "need", 1).get());
    Assert.assertFalse(LibString.indexOf(new NtMaybe<>("a needle yo"), "ninja", 1).has());
    Assert.assertEquals(2, (int) LibString.indexOf("a needle yo", new NtMaybe<>("need"), 1).get());
    Assert.assertFalse(LibString.indexOf("a needle yo", new NtMaybe<>("ninja"), 1).has());
    Assert.assertEquals(
        2, (int) LibString.indexOf(new NtMaybe<>("a needle yo"), new NtMaybe<>("need"), 1).get());
    Assert.assertFalse(
        LibString.indexOf(new NtMaybe<>("a needle yo"), new NtMaybe<>("ninja"), 1).has());
    Assert.assertFalse(LibString.indexOf(new NtMaybe<>(), "x", 1).has());
    Assert.assertFalse(LibString.indexOf("x", new NtMaybe<>(), 1).has());
    Assert.assertFalse(LibString.indexOf(new NtMaybe<>(), new NtMaybe<>(), 1).has());
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
  public void roman() {
    Assert.assertEquals("I", LibString.to_roman(1));
    Assert.assertEquals("CDLXIV", LibString.to_roman(464));
    Assert.assertEquals("II", LibString.to_roman(2));
    Assert.assertEquals("CCXCVI", LibString.to_roman(296));
    Assert.assertEquals("III", LibString.to_roman(3));
    Assert.assertEquals("CXXVI", LibString.to_roman(126));
    Assert.assertEquals("IV", LibString.to_roman(4));
    Assert.assertEquals("DCXII", LibString.to_roman(612));
    Assert.assertEquals("V", LibString.to_roman(5));
    Assert.assertEquals("CCLXXXVIII", LibString.to_roman(288));
    Assert.assertEquals("VI", LibString.to_roman(6));
    Assert.assertEquals("CDLXIX", LibString.to_roman(469));
    Assert.assertEquals("VII", LibString.to_roman(7));
    Assert.assertEquals("DCLX", LibString.to_roman(660));
    Assert.assertEquals("VIII", LibString.to_roman(8));
    Assert.assertEquals("DXLIV", LibString.to_roman(544));
    Assert.assertEquals("IX", LibString.to_roman(9));
    Assert.assertEquals("DCLXXXII", LibString.to_roman(682));
    Assert.assertEquals("X", LibString.to_roman(10));
    Assert.assertEquals("XLI", LibString.to_roman(41));
    Assert.assertEquals("XI", LibString.to_roman(11));
    Assert.assertEquals("CCXXVI", LibString.to_roman(226));
    Assert.assertEquals("XII", LibString.to_roman(12));
    Assert.assertEquals("CCLIX", LibString.to_roman(259));
    Assert.assertEquals("XIII", LibString.to_roman(13));
    Assert.assertEquals("CCCXCIX", LibString.to_roman(399));
    Assert.assertEquals("XIV", LibString.to_roman(14));
    Assert.assertEquals("DCCXX", LibString.to_roman(720));
    Assert.assertEquals("XV", LibString.to_roman(15));
    Assert.assertEquals("CCCXXI", LibString.to_roman(321));
    Assert.assertEquals("XVI", LibString.to_roman(16));
    Assert.assertEquals("CCCIV", LibString.to_roman(304));
    Assert.assertEquals("XVII", LibString.to_roman(17));
    Assert.assertEquals("CCCXVI", LibString.to_roman(316));
    Assert.assertEquals("XVIII", LibString.to_roman(18));
    Assert.assertEquals("CCCXIX", LibString.to_roman(319));
    Assert.assertEquals("XIX", LibString.to_roman(19));
    Assert.assertEquals("DCXXIX", LibString.to_roman(629));
    Assert.assertEquals("XX", LibString.to_roman(20));
    Assert.assertEquals("DCXVII", LibString.to_roman(617));
    Assert.assertEquals("XXI", LibString.to_roman(21));
    Assert.assertEquals("CDXXIX", LibString.to_roman(429));
    Assert.assertEquals("XXII", LibString.to_roman(22));
    Assert.assertEquals("CDXVI", LibString.to_roman(416));
    Assert.assertEquals("XXIII", LibString.to_roman(23));
    Assert.assertEquals("CCCLXXIV", LibString.to_roman(374));
    Assert.assertEquals("XXIV", LibString.to_roman(24));
    Assert.assertEquals("LXXII", LibString.to_roman(72));
    Assert.assertEquals("XXV", LibString.to_roman(25));
    Assert.assertEquals("CX", LibString.to_roman(110));
    Assert.assertEquals("XXVI", LibString.to_roman(26));
    Assert.assertEquals("CCLXXIX", LibString.to_roman(279));
    Assert.assertEquals("XXVII", LibString.to_roman(27));
    Assert.assertEquals("DCCCXXXII", LibString.to_roman(832));
    Assert.assertEquals("XXVIII", LibString.to_roman(28));
    Assert.assertEquals("MX", LibString.to_roman(1010));
    Assert.assertEquals("XXIX", LibString.to_roman(29));
    Assert.assertEquals("CMXLVI", LibString.to_roman(946));
    Assert.assertEquals("XXX", LibString.to_roman(30));
    Assert.assertEquals("CCCIX", LibString.to_roman(309));
    Assert.assertEquals("XXXI", LibString.to_roman(31));
    Assert.assertEquals("DCCCXLVI", LibString.to_roman(846));
    Assert.assertEquals("XXXII", LibString.to_roman(32));
    Assert.assertEquals("LXXXI", LibString.to_roman(81));
    Assert.assertEquals("XXXIII", LibString.to_roman(33));
    Assert.assertEquals("DCCCVII", LibString.to_roman(807));
    Assert.assertEquals("XXXIV", LibString.to_roman(34));
    Assert.assertEquals("CXCVIII", LibString.to_roman(198));
    Assert.assertEquals("XXXV", LibString.to_roman(35));
    Assert.assertEquals("CCXXXVII", LibString.to_roman(237));
    Assert.assertEquals("XXXVI", LibString.to_roman(36));
    Assert.assertEquals("CXXIV", LibString.to_roman(124));
    Assert.assertEquals("XXXVII", LibString.to_roman(37));
    Assert.assertEquals("CDXXV", LibString.to_roman(425));
    Assert.assertEquals("XXXVIII", LibString.to_roman(38));
    Assert.assertEquals("MXXXIII", LibString.to_roman(1033));
    Assert.assertEquals("XXXIX", LibString.to_roman(39));
    Assert.assertEquals("CDXX", LibString.to_roman(420));
    Assert.assertEquals("XL", LibString.to_roman(40));
    Assert.assertEquals("CLXXXI", LibString.to_roman(181));
    Assert.assertEquals("XLI", LibString.to_roman(41));
    Assert.assertEquals("LVIII", LibString.to_roman(58));
    Assert.assertEquals("XLII", LibString.to_roman(42));
    Assert.assertEquals("CMLXIII", LibString.to_roman(963));
    Assert.assertEquals("XLIII", LibString.to_roman(43));
    Assert.assertEquals("CDXXVI", LibString.to_roman(426));
    Assert.assertEquals("XLIV", LibString.to_roman(44));
    Assert.assertEquals("DXXXIV", LibString.to_roman(534));
    Assert.assertEquals("XLV", LibString.to_roman(45));
    Assert.assertEquals("DCCXXXI", LibString.to_roman(731));
    Assert.assertEquals("XLVI", LibString.to_roman(46));
    Assert.assertEquals("CCXVI", LibString.to_roman(216));
    Assert.assertEquals("XLVII", LibString.to_roman(47));
    Assert.assertEquals("CLXXXIV", LibString.to_roman(184));
    Assert.assertEquals("XLVIII", LibString.to_roman(48));
    Assert.assertEquals("DCXCVII", LibString.to_roman(697));
    Assert.assertEquals("XLIX", LibString.to_roman(49));
    Assert.assertEquals("CLXXXVIII", LibString.to_roman(188));
    Assert.assertEquals("L", LibString.to_roman(50));
    Assert.assertEquals("LXXX", LibString.to_roman(80));
    Assert.assertEquals("LI", LibString.to_roman(51));
    Assert.assertEquals("CCCXXV", LibString.to_roman(325));
    Assert.assertEquals("LII", LibString.to_roman(52));
    Assert.assertEquals("CXXXIX", LibString.to_roman(139));
    Assert.assertEquals("LIII", LibString.to_roman(53));
    Assert.assertEquals("LXIII", LibString.to_roman(63));
    Assert.assertEquals("LIV", LibString.to_roman(54));
    Assert.assertEquals("CLIV", LibString.to_roman(154));
    Assert.assertEquals("LV", LibString.to_roman(55));
    Assert.assertEquals("DXXVIII", LibString.to_roman(528));
    Assert.assertEquals("LVI", LibString.to_roman(56));
    Assert.assertEquals("CDLX", LibString.to_roman(460));
    Assert.assertEquals("LVII", LibString.to_roman(57));
    Assert.assertEquals("DCCCIII", LibString.to_roman(803));
    Assert.assertEquals("LVIII", LibString.to_roman(58));
    Assert.assertEquals("DCLXXX", LibString.to_roman(680));
    Assert.assertEquals("LIX", LibString.to_roman(59));
    Assert.assertEquals("DCCCVIII", LibString.to_roman(808));
    Assert.assertEquals("LX", LibString.to_roman(60));
    Assert.assertEquals("CCCLXXIV", LibString.to_roman(374));
    Assert.assertEquals("LXI", LibString.to_roman(61));
    Assert.assertEquals("DCCLXIV", LibString.to_roman(764));
    Assert.assertEquals("LXII", LibString.to_roman(62));
    Assert.assertEquals("DCCCXXXVIII", LibString.to_roman(838));
    Assert.assertEquals("LXIII", LibString.to_roman(63));
    Assert.assertEquals("CCCXXXVI", LibString.to_roman(336));
    Assert.assertEquals("LXIV", LibString.to_roman(64));
    Assert.assertEquals("CCXLVIII", LibString.to_roman(248));
    Assert.assertEquals("LXV", LibString.to_roman(65));
    Assert.assertEquals("CDXL", LibString.to_roman(440));
    Assert.assertEquals("LXVI", LibString.to_roman(66));
    Assert.assertEquals("CCCXXVIII", LibString.to_roman(328));
    Assert.assertEquals("LXVII", LibString.to_roman(67));
    Assert.assertEquals("DCLIV", LibString.to_roman(654));
    Assert.assertEquals("LXVIII", LibString.to_roman(68));
    Assert.assertEquals("MXXII", LibString.to_roman(1022));
    Assert.assertEquals("LXIX", LibString.to_roman(69));
    Assert.assertEquals("DCCLXXXI", LibString.to_roman(781));
    Assert.assertEquals("LXX", LibString.to_roman(70));
    Assert.assertEquals("DCXLVII", LibString.to_roman(647));
    Assert.assertEquals("LXXI", LibString.to_roman(71));
    Assert.assertEquals("DCCVI", LibString.to_roman(706));
    Assert.assertEquals("LXXII", LibString.to_roman(72));
    Assert.assertEquals("MLIV", LibString.to_roman(1054));
    Assert.assertEquals("LXXIII", LibString.to_roman(73));
    Assert.assertEquals("CIX", LibString.to_roman(109));
    Assert.assertEquals("LXXIV", LibString.to_roman(74));
    Assert.assertEquals("DCXXXI", LibString.to_roman(631));
    Assert.assertEquals("LXXV", LibString.to_roman(75));
    Assert.assertEquals("CMVIII", LibString.to_roman(908));
    Assert.assertEquals("LXXVI", LibString.to_roman(76));
    Assert.assertEquals("DCCV", LibString.to_roman(705));
    Assert.assertEquals("LXXVII", LibString.to_roman(77));
    Assert.assertEquals("DCCCIII", LibString.to_roman(803));
    Assert.assertEquals("LXXVIII", LibString.to_roman(78));
    Assert.assertEquals("DCXXXII", LibString.to_roman(632));
    Assert.assertEquals("LXXIX", LibString.to_roman(79));
    Assert.assertEquals("DXVIII", LibString.to_roman(518));
    Assert.assertEquals("LXXX", LibString.to_roman(80));
    Assert.assertEquals("CLV", LibString.to_roman(155));
    Assert.assertEquals("LXXXI", LibString.to_roman(81));
    Assert.assertEquals("M", LibString.to_roman(1000));
    Assert.assertEquals("LXXXII", LibString.to_roman(82));
    Assert.assertEquals("MLV", LibString.to_roman(1055));
    Assert.assertEquals("LXXXIII", LibString.to_roman(83));
    Assert.assertEquals("CMXXVIII", LibString.to_roman(928));
    Assert.assertEquals("LXXXIV", LibString.to_roman(84));
    Assert.assertEquals("MXLII", LibString.to_roman(1042));
    Assert.assertEquals("LXXXV", LibString.to_roman(85));
    Assert.assertEquals("CDXXXVIII", LibString.to_roman(438));
    Assert.assertEquals("LXXXVI", LibString.to_roman(86));
    Assert.assertEquals("CV", LibString.to_roman(105));
    Assert.assertEquals("LXXXVII", LibString.to_roman(87));
    Assert.assertEquals("MLI", LibString.to_roman(1051));
    Assert.assertEquals("LXXXVIII", LibString.to_roman(88));
    Assert.assertEquals("CMXXXV", LibString.to_roman(935));
    Assert.assertEquals("LXXXIX", LibString.to_roman(89));
    Assert.assertEquals("CLXXXII", LibString.to_roman(182));
    Assert.assertEquals("XC", LibString.to_roman(90));
    Assert.assertEquals("DXXXI", LibString.to_roman(531));
    Assert.assertEquals("XCI", LibString.to_roman(91));
    Assert.assertEquals("DCCCV", LibString.to_roman(805));
    Assert.assertEquals("XCII", LibString.to_roman(92));
    Assert.assertEquals("CDXII", LibString.to_roman(412));
    Assert.assertEquals("XCIII", LibString.to_roman(93));
    Assert.assertEquals("MLXVIII", LibString.to_roman(1068));
    Assert.assertEquals("XCIV", LibString.to_roman(94));
    Assert.assertEquals("MXLIX", LibString.to_roman(1049));
    Assert.assertEquals("XCV", LibString.to_roman(95));
    Assert.assertEquals("DCIII", LibString.to_roman(603));
    Assert.assertEquals("XCVI", LibString.to_roman(96));
    Assert.assertEquals("DCCLXXI", LibString.to_roman(771));
    Assert.assertEquals("XCVII", LibString.to_roman(97));
    Assert.assertEquals("CDLXIV", LibString.to_roman(464));
    Assert.assertEquals("XCVIII", LibString.to_roman(98));
    Assert.assertEquals("MLXXXI", LibString.to_roman(1081));
  }

  @Test
  public void reverse() {
    Assert.assertEquals("zen", LibString.reverse("nez"));
  }
}

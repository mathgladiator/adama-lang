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

public class HexTests {

  @Test
  public void flow_lower() {
    Assert.assertEquals("000102030405060708090a0b0c0d0e0f101112", Hex.of(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18}));
  }

  @Test
  public void flow_upper() {
    Assert.assertEquals("000102030405060708090A0B0C0D0E0F101112", Hex.of_upper(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18}));
  }

  @Test
  public void single_of1() {
    Assert.assertEquals("ef", Hex.of((byte) (14 * 16 + 15)));
    Assert.assertEquals("EF", Hex.of_upper((byte) (14 * 16 + 15)));
  }

  @Test
  public void single_of2() {
    Assert.assertEquals("ef", Hex.of((byte) (14 * 16 + 15)));
    Assert.assertEquals("EF", Hex.of_upper((byte) (14 * 16 + 15)));
  }

  @Test
  public void singleBad() {
    Assert.assertEquals(0, Hex.single('x'));
  }

  @Test
  public void singles_from_digit() {
    Assert.assertEquals(0, Hex.single('0'));
    Assert.assertEquals(1, Hex.single('1'));
    Assert.assertEquals(2, Hex.single('2'));
    Assert.assertEquals(3, Hex.single('3'));
    Assert.assertEquals(4, Hex.single('4'));
    Assert.assertEquals(5, Hex.single('5'));
    Assert.assertEquals(6, Hex.single('6'));
    Assert.assertEquals(7, Hex.single('7'));
    Assert.assertEquals(8, Hex.single('8'));
    Assert.assertEquals(9, Hex.single('9'));
  }

  @Test
  public void singles_from_alpha() {
    Assert.assertEquals(10, Hex.single('a'));
    Assert.assertEquals(10, Hex.single('A'));
    Assert.assertEquals(11, Hex.single('b'));
    Assert.assertEquals(11, Hex.single('B'));
    Assert.assertEquals(12, Hex.single('c'));
    Assert.assertEquals(12, Hex.single('C'));
    Assert.assertEquals(13, Hex.single('d'));
    Assert.assertEquals(13, Hex.single('D'));
    Assert.assertEquals(14, Hex.single('e'));
    Assert.assertEquals(14, Hex.single('E'));
    Assert.assertEquals(15, Hex.single('f'));
    Assert.assertEquals(15, Hex.single('F'));
  }

  @Test
  public void inverse() {
    byte[] check = Hex.from("000102030405060708090a0b0c0d0e0f101112");
    for (var k = 0; k < check.length; k++) {
      Assert.assertEquals((byte) k, check[k]);
    }
  }
}

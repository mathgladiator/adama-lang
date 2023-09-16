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

public class SlashStringArrayEncoderTests {
  @Test
  public void simple() {
    String packed = SlashStringArrayEncoder.encode("a", "b", "c");
    Assert.assertEquals("a/b/c", packed);
    String[] unpacked = SlashStringArrayEncoder.decode(packed);
    Assert.assertEquals("a", unpacked[0]);
    Assert.assertEquals("b", unpacked[1]);
    Assert.assertEquals("c", unpacked[2]);
  }

  @Test
  public void unicode() {
    String packed = SlashStringArrayEncoder.encode("abcdef", "猿も木から落ちる", "안 녕");
    Assert.assertEquals("abcdef/猿も木から落ちる/안 녕", packed);
    String[] unpacked = SlashStringArrayEncoder.decode(packed);
    Assert.assertEquals("abcdef", unpacked[0]);
    Assert.assertEquals("猿も木から落ちる", unpacked[1]);
    Assert.assertEquals("안 녕", unpacked[2]);
  }

  @Test
  public void escaping() {
    String packed = SlashStringArrayEncoder.encode("-/-/", "---", "///");
    Assert.assertEquals("---/---//------/-/-/-/", packed);
    String[] unpacked = SlashStringArrayEncoder.decode(packed);
    Assert.assertEquals("-/-/", unpacked[0]);
    Assert.assertEquals("---", unpacked[1]);
    Assert.assertEquals("///", unpacked[2]);
  }

  @Test
  public void empty() {
    String packed = SlashStringArrayEncoder.encode("", "", "");
    Assert.assertEquals("//", packed);
    String[] unpacked = SlashStringArrayEncoder.decode(packed);
    Assert.assertEquals("", unpacked[0]);
    Assert.assertEquals("", unpacked[1]);
    Assert.assertEquals("", unpacked[2]);
  }

  @Test
  public void solo() {
    String packed = SlashStringArrayEncoder.encode("");
    Assert.assertEquals("", packed);
    String[] unpacked = SlashStringArrayEncoder.decode(packed);
    Assert.assertEquals("", unpacked[0]);
  }
}

package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class SlashStringArrayEncoderTests {
  @Test
  public void simple() {
    String packed = SlashStringArrayEncoder.encode("a","b","c");
    Assert.assertEquals("a/b/c", packed);
    String[] unpacked = SlashStringArrayEncoder.decode(packed);
    Assert.assertEquals("a", unpacked[0]);
    Assert.assertEquals("b", unpacked[1]);
    Assert.assertEquals("c", unpacked[2]);
  }

  @Test
  public void unicode() {
    String packed = SlashStringArrayEncoder.encode("abcdef","猿も木から落ちる","안 녕");
    Assert.assertEquals("abcdef/猿も木から落ちる/안 녕", packed);
    String[] unpacked = SlashStringArrayEncoder.decode(packed);
    Assert.assertEquals("abcdef", unpacked[0]);
    Assert.assertEquals("猿も木から落ちる", unpacked[1]);
    Assert.assertEquals("안 녕", unpacked[2]);
  }

  @Test
  public void escaping() {
    String packed = SlashStringArrayEncoder.encode("-/-/","---","///");
    Assert.assertEquals("---/---//------/-/-/-/", packed);
    String[] unpacked = SlashStringArrayEncoder.decode(packed);
    Assert.assertEquals("-/-/", unpacked[0]);
    Assert.assertEquals("---", unpacked[1]);
    Assert.assertEquals("///", unpacked[2]);
  }

  @Test
  public void empty() {
    String packed = SlashStringArrayEncoder.encode("","","");
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

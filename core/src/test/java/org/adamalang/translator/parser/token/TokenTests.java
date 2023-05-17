/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.parser.token;

import org.junit.Assert;
import org.junit.Test;

public class TokenTests {
  @Test
  public void coverageEquals() {
    Token a1 = Token.WRAP("a");
    Token a2 = Token.WRAP("a");
    Token b = Token.WRAP("b");
    Assert.assertNotEquals(a1, "a");
    Assert.assertEquals(a1, a1);
    Assert.assertEquals(a1, a2);
    Assert.assertNotEquals(a1, b);
    Assert.assertNotEquals(a2, b);
  }

  @Test
  public void coverageCmp() {
    Token a = Token.WRAP("a");
    Token b = Token.WRAP("b");
    Assert.assertTrue(a.compareTo(b) < 0);
    Assert.assertTrue(b.compareTo(a) > 0);
    Assert.assertTrue(b.compareTo(b) == 0);
  }

  @Test
  public void coverageHashcode() {
    Token a1 = Token.WRAP("a");
    Token a2 = Token.WRAP("a");
    Assert.assertEquals(-293309790, a1.hashCode());
    Assert.assertEquals(a1.hashCode(), a2.hashCode());
  }

  @Test
  public void isKeyword() {
    Token t = new Token("source", "key", MajorTokenType.Keyword, null, 0, 0, 0, 0, 0, 0);
    Assert.assertTrue(t.isKeyword());
    Assert.assertFalse(t.isNumberLiteral());
    Assert.assertFalse(t.isNumberLiteralDouble());
    Assert.assertFalse(t.isNumberLiteralInteger());
  }

  @Test
  public void isDouble() {
    Token t = new Token("source", "key", MajorTokenType.NumberLiteral, MinorTokenType.NumberIsDouble, 0, 0, 0, 0, 0, 0);
    Assert.assertFalse(t.isKeyword());
    Assert.assertTrue(t.isNumberLiteral());
    Assert.assertTrue(t.isNumberLiteralDouble());
    Assert.assertFalse(t.isNumberLiteralInteger());
  }

  @Test
  public void isInt() {
    Token t = new Token("source", "key", MajorTokenType.NumberLiteral, MinorTokenType.NumberIsInteger, 0, 0, 0, 0, 0, 0);
    Assert.assertFalse(t.isKeyword());
    Assert.assertTrue(t.isNumberLiteral());
    Assert.assertFalse(t.isNumberLiteralDouble());
    Assert.assertTrue(t.isNumberLiteralInteger());
  }
}

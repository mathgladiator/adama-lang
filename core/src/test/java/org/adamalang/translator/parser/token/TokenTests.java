/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
    Assert.assertEquals(977085218, a1.hashCode());
    Assert.assertEquals(a1.hashCode(), a2.hashCode());
  }

  @Test
  public void isKeyword() {
    Token t = new Token("source", "key", MajorTokenType.Keyword, null, 0, 0, 0, 0);
    Assert.assertTrue(t.isKeyword());
  }
}

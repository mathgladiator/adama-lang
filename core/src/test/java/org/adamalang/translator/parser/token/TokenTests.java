/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
    Assert.assertEquals(178794309, a1.hashCode());
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

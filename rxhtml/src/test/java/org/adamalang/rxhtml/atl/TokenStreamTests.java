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
package org.adamalang.rxhtml.atl;

import org.adamalang.rxhtml.atl.TokenStream;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class TokenStreamTests {

  @Test
  public void simple_text() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("xyz").iterator();
    assertNextIsText(it, "xyz");
    assertNoNext(it);
  }

  private void assertNextIsText(Iterator<TokenStream.Token> it, String value) throws Exception {
    Assert.assertTrue(it.hasNext());
    TokenStream.Token next = it.next();
    Assert.assertEquals(next.type, TokenStream.Type.Text);
    Assert.assertEquals(next.base, value);
  }

  public void assertNoNext(Iterator<TokenStream.Token> it) {
    Assert.assertFalse(it.hasNext());
  }

  @Test
  public void simple_variable() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("{x}").iterator();
    assertNextIsVariable(it, "x", TokenStream.Modifier.None);
    assertNoNext(it);
  }

  private void assertNextIsVariable(Iterator<TokenStream.Token> it, String value, TokenStream.Modifier mod, String... transforms) throws Exception {
    Assert.assertTrue(it.hasNext());
    TokenStream.Token next = it.next();
    Assert.assertEquals(TokenStream.Type.Variable, next.type);
    Assert.assertEquals(mod, next.mod);
    Assert.assertEquals(value, next.base);
    Assert.assertEquals(transforms.length, next.transforms.length);
    for (int k = 0; k < transforms.length; k++) {
      Assert.assertEquals(transforms[k], next.transforms[k]);
    }
  }

  @Test
  public void variable_mods() throws Exception {
    {
      assertNextIsVariable(TokenStream.tokenize("{x}").iterator(), "x", TokenStream.Modifier.None);
      assertNextIsVariable(TokenStream.tokenize("{  x  }").iterator(), "x", TokenStream.Modifier.None);
      assertNextIsVariable(TokenStream.tokenize("{! x }").iterator(), "x", TokenStream.Modifier.Not);
      assertNextIsVariable(TokenStream.tokenize("{!x}").iterator(), "x", TokenStream.Modifier.Not);
      assertNextIsVariable(TokenStream.tokenize("{  # x  }").iterator(), "# x", TokenStream.Modifier.None);
      assertNextIsVariable(TokenStream.tokenize("{# x}").iterator(), "# x", TokenStream.Modifier.None);
      assertNextIsVariable(TokenStream.tokenize("{  / x  }").iterator(), "/ x", TokenStream.Modifier.None);
      assertNextIsVariable(TokenStream.tokenize("{/ x}").iterator(), "/ x", TokenStream.Modifier.None);
    }
  }

  @Test
  public void simple_variable_w_1_transform() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("{x|y}").iterator();
    assertNextIsVariable(it, "x", TokenStream.Modifier.None, "y");
    assertNoNext(it);
  }

  @Test
  public void simple_variable_w_2_transform() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("{x|y|z}").iterator();
    assertNextIsVariable(it, "x", TokenStream.Modifier.None, "y", "z");
    assertNoNext(it);
  }

  @Test
  public void simple_condition() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("[x]").iterator();
    assertNextIsCondition(it, "x", TokenStream.Modifier.None);
    assertNoNext(it);
  }

  private void assertNextIsCondition(Iterator<TokenStream.Token> it, String value, TokenStream.Modifier mod, String... transforms) throws Exception {
    Assert.assertTrue(it.hasNext());
    TokenStream.Token next = it.next();
    Assert.assertEquals(next.type, TokenStream.Type.Condition);
    Assert.assertEquals(next.mod, mod);
    Assert.assertEquals(next.base, value);
    Assert.assertEquals(next.transforms.length, transforms.length);
    for (int k = 0; k < transforms.length; k++) {
      Assert.assertEquals(next.transforms[k], transforms[k]);
    }
  }

  @Test
  public void condition_mods() throws Exception {
    {
      assertNextIsCondition(TokenStream.tokenize("[x]").iterator(), "x", TokenStream.Modifier.None);
      assertNextIsCondition(TokenStream.tokenize("[!x]").iterator(), "x", TokenStream.Modifier.Not);
      assertNextIsCondition(TokenStream.tokenize("[#x]").iterator(), "x", TokenStream.Modifier.Else);
      assertNextIsCondition(TokenStream.tokenize("[ x ]").iterator(), "x", TokenStream.Modifier.None);
      assertNextIsCondition(TokenStream.tokenize("[# x ]").iterator(), "x", TokenStream.Modifier.Else);
      assertNextIsCondition(TokenStream.tokenize("[! x ]").iterator(), "x", TokenStream.Modifier.Not);
      assertNextIsCondition(TokenStream.tokenize("[/x]").iterator(), "x", TokenStream.Modifier.End);
      assertNextIsCondition(TokenStream.tokenize("[/ x ]").iterator(), "x", TokenStream.Modifier.End);
    }
  }

  @Test
  public void simple_condition_w_1_transform() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("[x|y]").iterator();
    assertNextIsCondition(it, "x", TokenStream.Modifier.None, "y");
    assertNoNext(it);
  }

  @Test
  public void simple_condition_w_2_transform() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("[x|y|z]").iterator();
    assertNextIsCondition(it, "x", TokenStream.Modifier.None, "y", "z");
    assertNoNext(it);
  }

  @Test
  public void compound_1() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("hi {name}, how are you? [good]a[#good]b[/good]c").iterator();
    assertNextIsText(it, "hi ");
    assertNextIsVariable(it, "name", TokenStream.Modifier.None);
    assertNextIsText(it, ", how are you? ");
    assertNextIsCondition(it, "good", TokenStream.Modifier.None);
    assertNextIsText(it, "a");
    assertNextIsCondition(it, "good", TokenStream.Modifier.Else);
    assertNextIsText(it, "b");
    assertNextIsCondition(it, "good", TokenStream.Modifier.End);
    assertNextIsText(it, "c");
    assertNoNext(it);
  }

  @Test
  public void escaping1() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("`[`]`{`}").iterator();
    assertNextIsText(it, "[]{}");
    assertNoNext(it);
  }

  @Test
  public void escaping2() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("a`{`}b").iterator();
    assertNextIsText(it, "a{}b");
    assertNoNext(it);
  }

  @Test
  public void escaping3() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("a`[`]b").iterator();
    assertNextIsText(it, "a[]b");
    assertNoNext(it);
  }

  @Test
  public void escaping4() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("[a`{]").iterator();
    assertNextIsCondition(it, "a`{", TokenStream.Modifier.None);
    assertNoNext(it);
  }

  @Test
  public void escaping5() throws Exception {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("{a`{}").iterator();
    assertNextIsVariable(it, "a`{", TokenStream.Modifier.None);
    assertNoNext(it);
  }
}

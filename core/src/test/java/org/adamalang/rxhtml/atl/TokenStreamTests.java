/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl;

import org.adamalang.translator.parser.token.TokenEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class TokenStreamTests {

  private void assertNextIsText(Iterator<TokenStream.Token> it, String value) {
    Assert.assertTrue(it.hasNext());
    TokenStream.Token next = it.next();
    Assert.assertEquals(next.type, TokenStream.Type.Text);
    Assert.assertEquals(next.base, value);
  }

  private void assertNextIsVariable(Iterator<TokenStream.Token> it, String value, TokenStream.Modifier mod, String... transforms) {
    Assert.assertTrue(it.hasNext());
    TokenStream.Token next = it.next();
    Assert.assertEquals(next.type, TokenStream.Type.Variable);
    Assert.assertEquals(next.mod, mod);
    Assert.assertEquals(next.base, value);
    Assert.assertEquals(next.transforms.length, transforms.length);
    for (int k = 0; k < transforms.length; k++) {
      Assert.assertEquals(next.transforms[k], transforms[k]);
    }
  }

  private void assertNextIsCondition(Iterator<TokenStream.Token> it, String value, TokenStream.Modifier mod, String... transforms) {
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

  public void assertNoNext(Iterator<TokenStream.Token> it) {
    Assert.assertFalse(it.hasNext());
  }

  @Test
  public void simple_text() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("xyz").iterator();
    assertNextIsText(it, "xyz");
    assertNoNext(it);
  }

  @Test
  public void simple_variable() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("{x}").iterator();
    assertNextIsVariable(it, "x", TokenStream.Modifier.None);
    assertNoNext(it);
  }

  @Test
  public void variable_mods() {
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
  public void simple_variable_w_1_transform() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("{x|y}").iterator();
    assertNextIsVariable(it, "x", TokenStream.Modifier.None, "y");
    assertNoNext(it);
  }

  @Test
  public void simple_variable_w_2_transform() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("{x|y|z}").iterator();
    assertNextIsVariable(it, "x", TokenStream.Modifier.None, "y", "z");
    assertNoNext(it);
  }

  @Test
  public void simple_condition() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("[x]").iterator();
    assertNextIsCondition(it, "x", TokenStream.Modifier.None);
    assertNoNext(it);
  }

  @Test
  public void condition_mods() {
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
  public void simple_condition_w_1_transform() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("[x|y]").iterator();
    assertNextIsCondition(it, "x", TokenStream.Modifier.None, "y");
    assertNoNext(it);
  }

  @Test
  public void simple_condition_w_2_transform() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("[x|y|z]").iterator();
    assertNextIsCondition(it, "x", TokenStream.Modifier.None, "y", "z");
    assertNoNext(it);
  }

  @Test
  public void compound_1() {
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
  public void escaping1() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("`[`]`{`}").iterator();
    assertNextIsText(it, "[]{}");
    assertNoNext(it);
  }

  @Test
  public void escaping2() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("a`{`}b").iterator();
    assertNextIsText(it, "a{}b");
    assertNoNext(it);
  }

  @Test
  public void escaping3() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("a`[`]b").iterator();
    assertNextIsText(it, "a[]b");
    assertNoNext(it);
  }

  @Test
  public void escaping4() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("[a`{]").iterator();
    assertNextIsCondition(it, "a{", TokenStream.Modifier.None);
    assertNoNext(it);
  }

  @Test
  public void escaping5() {
    Iterator<TokenStream.Token> it = TokenStream.tokenize("{a`{}").iterator();
    assertNextIsVariable(it, "a{", TokenStream.Modifier.None);
    assertNoNext(it);
  }
}

package org.adamalang.rxhtml.tree;

import org.adamalang.translator.parser.token.Token;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

public class AttributeTests {
  @Test
  public void guard_regex_single() {
    String test = "hi there [x] [y] [/y] [/x]";
    Matcher matcher = Attribute.GUARD_INITIATE.matcher(test);
    Assert.assertTrue(matcher.find());
    Assert.assertEquals("[x]", matcher.group());
    Assert.assertTrue(matcher.find());
    Assert.assertEquals("[y]", matcher.group());
    Assert.assertFalse(matcher.find());
  }

  @Test
  public void guard_regex_special() {
    String test = "blah [_] zan [a0_1] hsd";
    Matcher matcher = Attribute.GUARD_INITIATE.matcher(test);
    Assert.assertTrue(matcher.find());
    Assert.assertEquals("[_]", matcher.group());
    Assert.assertTrue(matcher.find());
    Assert.assertEquals("[a0_1]", matcher.group());
    Assert.assertFalse(matcher.find());
  }

  @Test
  public void depends() {
    Attribute attribute = new Attribute(Token.WRAP("attr"), Token.WRAP("="), Token.WRAP("Hi [x] there [/x]"));
    Assert.assertEquals(1, attribute.guards.length);
    Assert.assertEquals("x", attribute.guards[0]);
  }
}

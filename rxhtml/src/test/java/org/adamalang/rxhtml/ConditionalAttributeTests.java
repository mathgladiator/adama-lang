package org.adamalang.rxhtml;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

public class ConditionalAttributeTests {

  @Test
  public void guard_regex_single() {
    String test = "hi there [x] [y] [/y] [/x]";
    Matcher matcher = ConditionalAttribute.INITIATE.matcher(test);
    Assert.assertTrue(matcher.find());
    Assert.assertEquals("[x]", matcher.group());
    Assert.assertTrue(matcher.find());
    Assert.assertEquals("[y]", matcher.group());
    Assert.assertFalse(matcher.find());
  }

  @Test
  public void guard_regex_special() {
    String test = "blah [_] zan [a0_1] hsd";
    Matcher matcher = ConditionalAttribute.INITIATE.matcher(test);
    Assert.assertTrue(matcher.find());
    Assert.assertEquals("[_]", matcher.group());
    Assert.assertTrue(matcher.find());
    Assert.assertEquals("[a0_1]", matcher.group());
    Assert.assertFalse(matcher.find());
  }
}

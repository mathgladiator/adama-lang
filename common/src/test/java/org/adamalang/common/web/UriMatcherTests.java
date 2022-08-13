package org.adamalang.common.web;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.function.Function;

public class UriMatcherTests {
  @Test
  public void flow_simple() {
    ArrayList<Function<String, Boolean>> matchers = new ArrayList<>();
    matchers.add((s) -> true);
    matchers.add((s) -> "yes".equals(s));
    UriMatcher matcher = new UriMatcher(matchers, false);
    Assert.assertFalse(matcher.matches("/yo"));
    Assert.assertTrue(matcher.matches("/yo/yes"));
    Assert.assertTrue(matcher.matches("/asfasfgsagasg124sxz/yes"));
    Assert.assertFalse(matcher.matches("/asfasfgsagasg124sxz/no"));
  }

  @Test
  public void flow_star() {
    ArrayList<Function<String, Boolean>> matchers = new ArrayList<>();
    matchers.add((s) -> true);
    matchers.add((s) -> "yes".equals(s));
    UriMatcher matcher = new UriMatcher(matchers, true);
    Assert.assertFalse(matcher.matches("/yo"));
    Assert.assertTrue(matcher.matches("/yo/yes"));
    Assert.assertTrue(matcher.matches("/yo/yes/yes-123541254asdg"));
    Assert.assertTrue(matcher.matches("/asfasfgsagasg124sxz/yes/asfggfs"));
    Assert.assertFalse(matcher.matches("/asfasfgsagasg124sxz/no/xgxgssdf"));
  }
}

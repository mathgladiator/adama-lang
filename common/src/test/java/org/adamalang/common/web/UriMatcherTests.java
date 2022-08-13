/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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
    UriMatcher matcher = new UriMatcher("name", matchers, false);
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
    UriMatcher matcher = new UriMatcher("name", matchers, true);
    Assert.assertFalse(matcher.matches("/yo"));
    Assert.assertTrue(matcher.matches("/yo/yes"));
    Assert.assertTrue(matcher.matches("/yo/yes/yes-123541254asdg"));
    Assert.assertTrue(matcher.matches("/asfasfgsagasg124sxz/yes/asfggfs"));
    Assert.assertFalse(matcher.matches("/asfasfgsagasg124sxz/no/xgxgssdf"));
  }
}

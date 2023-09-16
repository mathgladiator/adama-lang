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

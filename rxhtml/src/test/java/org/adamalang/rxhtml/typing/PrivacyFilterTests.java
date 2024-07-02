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
package org.adamalang.rxhtml.typing;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class PrivacyFilterTests {
  @Test
  public void flow0() {
    PrivacyFilter pv = new PrivacyFilter("".split(Pattern.quote(",")));
    Assert.assertEquals(0, pv.allowed.size());
  }

  @Test
  public void flow1() {
    PrivacyFilter pv = new PrivacyFilter("is_admin".split(Pattern.quote(",")));
    Assert.assertEquals(1, pv.allowed.size());
    Assert.assertTrue(pv.allowed.contains("is_admin"));
  }

  @Test
  public void flow2() {
    PrivacyFilter pv = new PrivacyFilter("is_admin,is_staff".split(Pattern.quote(",")));
    Assert.assertEquals(2, pv.allowed.size());
    Assert.assertTrue(pv.allowed.contains("is_admin"));
    Assert.assertTrue(pv.allowed.contains("is_staff"));
  }
}

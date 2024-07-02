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
package org.adamalang.runtime.sys.web;

import org.junit.Assert;
import org.junit.Test;

public class WebPathTests {
  @Test
  public void flow() {
    WebPath router = new WebPath("/xyz/kstar/123");
    Assert.assertEquals("xyz", router.at(0).fragment);
    Assert.assertEquals("kstar", router.at(1).fragment);
    Assert.assertEquals("123", router.at(2).fragment);
    Assert.assertNull(router.at(5));
  }

  @Test
  public void root() {
    WebPath router = new WebPath("/");
    Assert.assertEquals("", router.at(0).fragment);
    Assert.assertNull(router.at(1));
  }

  @Test
  public void bigtail() {
    WebPath router = new WebPath("/xyz/123/wtf");
    Assert.assertEquals("xyz", router.at(0).fragment);
    Assert.assertEquals("xyz/123/wtf", router.at(0).tail());
    Assert.assertEquals("123", router.at(1).fragment);
    Assert.assertEquals("123/wtf", router.at(1).tail());
    Assert.assertEquals("wtf", router.at(2).fragment);
    Assert.assertEquals("wtf", router.at(2).tail());
  }
}

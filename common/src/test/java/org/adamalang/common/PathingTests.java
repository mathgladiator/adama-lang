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
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class PathingTests {

  @Test
  public void normal() {
    Assert.assertEquals("x/y", Pathing.normalize("x\\y"));
  }

  @Test
  public void max_suffix() {
    Assert.assertEquals("", Pathing.maxSharedSuffix("x/y/u", "z/y/v"));
    Assert.assertEquals("y", Pathing.maxSharedSuffix("x/y", "z/y"));
    Assert.assertEquals("y/x", Pathing.maxSharedSuffix("x/y/x", "z/y/x"));
    Assert.assertEquals("abc/def/ghi", Pathing.maxSharedSuffix("x/abc/def/ghi", "z/abc/def/ghi"));
  }

  @Test
  public void max_prefix() {
    Assert.assertEquals("", Pathing.maxSharedPrefix("x/y/u", "z/y/v"));
    Assert.assertEquals("y", Pathing.maxSharedPrefix("y/1", "y/2/3"));
    Assert.assertEquals("x/y", Pathing.maxSharedPrefix("x/y/z", "x/y/x"));
    Assert.assertEquals("abc/def/ghi", Pathing.maxSharedPrefix("abc/def/ghi/y", "abc/def/ghi/x"));
  }

  @Test
  public void remove_last() {
    Assert.assertEquals("x", Pathing.removeLast("x"));
    Assert.assertEquals("x", Pathing.removeLast("x/y"));
    Assert.assertEquals("x/y", Pathing.removeLast("x/y/z"));
  }

  @Test
  public void remove_common() {
    Assert.assertEquals("z", Pathing.removeCommonRootFromB("x/y", "x/y/z"));
  }
}

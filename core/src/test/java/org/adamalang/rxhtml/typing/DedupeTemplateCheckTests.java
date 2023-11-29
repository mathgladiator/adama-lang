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
package org.adamalang.rxhtml.typing;

import org.junit.Assert;
import org.junit.Test;

public class DedupeTemplateCheckTests {

  @Test
  public void sanity() {
    DedupeTemplateCheck a = new DedupeTemplateCheck("a", "b");
    DedupeTemplateCheck b = new DedupeTemplateCheck("c", "d");
    DedupeTemplateCheck c = new DedupeTemplateCheck("a", "e");
    Assert.assertEquals(4066, a.hashCode());
    Assert.assertEquals(4130, b.hashCode());
    Assert.assertEquals(4069, c.hashCode());
    Assert.assertEquals(a, a);
    Assert.assertNotEquals(a, b);
    Assert.assertNotEquals(a, c);
    Assert.assertEquals(a, new DedupeTemplateCheck("a", "b"));
    Assert.assertNotEquals(a, null);
    Assert.assertNotEquals(a, "xyz");
    Assert.assertEquals(-2, a.compareTo(b));
    Assert.assertEquals(2, b.compareTo(c));
    Assert.assertEquals(3, c.compareTo(a));
  }
}

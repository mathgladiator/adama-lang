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
package org.adamalang.runtime.natives.algo;

import org.junit.Assert;
import org.junit.Test;

public class DynCompareParserTests {
  @Test
  public void empty() {
    CompareField[] result = DynCompareParser.parse("");
    Assert.assertEquals(0, result.length);
  }
  @Test
  public void zero_content() {
    CompareField[] result = DynCompareParser.parse(",,   ,,   ,,,");
    Assert.assertEquals(0, result.length);
  }

  @Test
  public void single() {
    CompareField[] result = DynCompareParser.parse("a");
    Assert.assertEquals(1, result.length);
    Assert.assertEquals("a", result[0].name);
    Assert.assertFalse(result[0].desc);
  }
  @Test
  public void single_ws() {
    CompareField[] result = DynCompareParser.parse("   a   ");
    Assert.assertEquals(1, result.length);
    Assert.assertEquals("a", result[0].name);
    Assert.assertFalse(result[0].desc);
  }
  @Test
  public void single_desc() {
    CompareField[] result = DynCompareParser.parse("-a");
    Assert.assertEquals(1, result.length);
    Assert.assertEquals("a", result[0].name);
    Assert.assertTrue(result[0].desc);
  }
  @Test
  public void single_desc_ws() {
    CompareField[] result = DynCompareParser.parse(" -  a   ");
    Assert.assertEquals(1, result.length);
    Assert.assertEquals("a", result[0].name);
    Assert.assertTrue(result[0].desc);
  }

  @Test
  public void trimix() {
    CompareField[] result = DynCompareParser.parse("+abc,-def,gh");
    Assert.assertEquals(3, result.length);
    Assert.assertEquals("abc", result[0].name);
    Assert.assertEquals("def", result[1].name);
    Assert.assertEquals("gh", result[2].name);
    Assert.assertFalse(result[0].desc);
    Assert.assertTrue(result[1].desc);
    Assert.assertFalse(result[2].desc);
  }
}

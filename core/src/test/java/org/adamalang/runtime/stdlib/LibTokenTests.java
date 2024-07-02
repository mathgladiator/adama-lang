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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class LibTokenTests {
  @Test
  public void prod_int() {
    ArrayList<Integer> input = new ArrayList<>();
    input.add(5);
    input.add(1);
    input.add(3);
    input.add(2);
    input.add(1);
    input.add(3);
    input.add(2);
    input.add(1);
    input.add(3);
    input.add(2);
    input.add(1);
    input.add(3);
    input.add(2);
    input.add(0);
    int[] result = LibToken.sortAndUniqueAsIntTokens(new ArrayNtList<>(input));
    Assert.assertEquals(5, result.length);
    Assert.assertEquals(0, result[0]);
    Assert.assertEquals(1, result[1]);
    Assert.assertEquals(2, result[2]);
    Assert.assertEquals(3, result[3]);
    Assert.assertEquals(5, result[4]);
  }

  @Test
  public void prod_str() {
    ArrayList<String> input = new ArrayList<>();
    input.add("X");
    input.add("Y");
    input.add("a");
    input.add("c");
    input.add("b");
    input.add("A");
    input.add("C");
    input.add("B");
    input.add("y");
    input.add("X");
    String[] result = LibToken.normalizeSortAndUniqueAsStringTokens(new ArrayNtList<>(input));
    Assert.assertEquals(5, result.length);
    Assert.assertEquals("a", result[0]);
    Assert.assertEquals("b", result[1]);
    Assert.assertEquals("c", result[2]);
    Assert.assertEquals("x", result[3]);
    Assert.assertEquals("y", result[4]);
  }

  @Test
  public void intersect_int_tokens() {
    int[] a = new int[] {1, 2, 4, 5, 7, 9};
    int[] b = new int[] {3, 5, 6, 8, 9};
    int[] c = LibToken.intersect(a, b);
    Assert.assertEquals(2, c.length);
    Assert.assertEquals(5, c[0]);
    Assert.assertEquals(9, c[1]);
  }

  @Test
  public void intersect_string_tokens() {
    String[] a = new String[] {"1", "2", "4", "5", "7", "9"};
    String[] b = new String[] {"3", "5", "6", "8", "9"};
    String[] c = LibToken.intersect(a, b);
    Assert.assertEquals(2, c.length);
    Assert.assertEquals("5", c[0]);
    Assert.assertEquals("9", c[1]);
  }
}

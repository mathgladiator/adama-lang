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
package org.adamalang.runtime.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class SubGraphTests {

  @Test
  public void mem() {
    SubGraph sg = new SubGraph();
    Assert.assertEquals(2048, sg.memory());
    sg.put(5, 10);
    Assert.assertEquals(2368, sg.memory());
    sg.put(5, 15);
    Assert.assertEquals(2432, sg.memory());
    sg.put(2, 1);
    Assert.assertEquals(2752, sg.memory());
    sg.remove(5, 10);
    Assert.assertEquals(2688, sg.memory());
    sg.remove(5, 15);
    Assert.assertEquals(2368, sg.memory());
    sg.remove(5, 6);
    Assert.assertEquals(2368, sg.memory());
    sg.remove(2, 1);
    Assert.assertEquals(2048, sg.memory());
  }

  @Test
  public void empty() {
    SubGraph sg = new SubGraph();
    sg.put(5, 10);
    sg.put(2, 7);
    sg.put(2, 4);
    TreeSet<Integer> input = new TreeSet<>();
    TreeSet<Integer> a = sg.traverse(input);
    Assert.assertEquals(0, a.size());
  }

  @Test
  public void union() {
    SubGraph sg = new SubGraph();
    sg.put(5, 10);
    sg.put(2, 7);
    sg.put(2, 4);
    sg.put(5, 4);
    TreeSet<Integer> input = new TreeSet<>();
    input.add(2);
    input.add(5);
    input.add(10);
    TreeSet<Integer> a = sg.traverse(input);
    Assert.assertEquals(3, a.size());
    Assert.assertTrue(a.contains(10));
    Assert.assertTrue(a.contains(7));
    Assert.assertTrue(a.contains(4));
  }

  @Test
  public void single() {
    SubGraph sg = new SubGraph();
    sg.put(5, 10);
    sg.put(2, 7);
    sg.put(2, 4);
    sg.put(5, 4);
    TreeSet<Integer> input = new TreeSet<>();
    input.add(2);
    TreeSet<Integer> a = sg.traverse(input);
    Assert.assertEquals(2, a.size());
    Assert.assertTrue(a.contains(7));
    Assert.assertTrue(a.contains(4));
  }
}

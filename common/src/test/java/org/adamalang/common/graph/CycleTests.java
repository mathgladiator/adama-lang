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
package org.adamalang.common.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class CycleTests {
  @Test
  public void empty() {
    Assert.assertNull(Cycle.detect(new HashMap<>()));
  }

  @Test
  public void look_back() {
    TreeMap<String, Set<String>> graph = new TreeMap<>();
    graph.put("A", D("B", "C"));
    graph.put("B", D("C", "Y"));
    graph.put("X", D("Y", "Z"));
    graph.put("Y", D("Z"));
    Assert.assertNull(Cycle.detect(graph));
  }

  private static Set<String> D(String... ds) {
    TreeSet<String> depends = new TreeSet<>();
    Collections.addAll(depends, ds);
    return depends;
  }

  @Test
  public void sample_1_nocycle() {
    HashMap<String, Set<String>> graph = new HashMap<>();
    graph.put("A", D("B"));
    graph.put("B", D("C"));
    graph.put("X", D("Y"));
    graph.put("Y", D("Z"));
    Assert.assertNull(Cycle.detect(graph));
  }

  @Test
  public void sample_2_cycle() {
    HashMap<String, Set<String>> graph = new HashMap<>();
    graph.put("A", D("B"));
    graph.put("B", D("A"));
    Assert.assertEquals("A, B, A", Cycle.detect(graph));
  }

  @Test
  public void sample_3_cycle() {
    HashMap<String, Set<String>> graph = new HashMap<>();
    graph.put("A", D("A"));
    Assert.assertEquals("A, A", Cycle.detect(graph));
  }

  @Test
  public void sample_4_cycle() {
    HashMap<String, Set<String>> graph = new HashMap<>();
    graph.put("X", D("Y"));
    graph.put("Y", D("Z"));
    graph.put("Y", D("A"));
    graph.put("A", D("B"));
    graph.put("B", D("Y"));
    Assert.assertEquals("A, B, Y, A", Cycle.detect(graph));
  }

  @Test
  public void sample_5_nocycle_big() {
    HashMap<String, Set<String>> graph = new HashMap<>();
    graph.put("A", D("B"));
    graph.put("A", D("C"));
    graph.put("A", D("D"));
    graph.put("A", D("E"));
    graph.put("A", D("F"));
    graph.put("A", D("G"));
    graph.put("B", D("C"));
    graph.put("B", D("D"));
    graph.put("B", D("E"));
    graph.put("B", D("F"));
    graph.put("B", D("G"));
    graph.put("C", D("D"));
    graph.put("C", D("E"));
    graph.put("C", D("F"));
    graph.put("C", D("G"));
    graph.put("D", D("E"));
    graph.put("E", D("F"));
    Assert.assertNull(Cycle.detect(graph));
  }
}

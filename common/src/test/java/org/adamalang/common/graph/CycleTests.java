package org.adamalang.common.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class CycleTests {
  private static Set<String> D(String... ds) {
    TreeSet<String> depends = new TreeSet<>();
    for (String d : ds) {
      depends.add(d);
    }
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

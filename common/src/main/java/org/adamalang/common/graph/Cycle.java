package org.adamalang.common.graph;

import java.util.*;

/* a cycle detector in a graph */
public class Cycle {
  private final Map<String, Set<String>> graph;
  private final TreeSet<String> visited;
  private final Stack<String> stack;
  private final ArrayDeque<String> remain;

  private Cycle(Map<String, Set<String>> graph) {
    this.graph = graph;
    this.visited = new TreeSet<>();
    this.stack = new Stack<>();
    this.remain = new ArrayDeque<>(graph.keySet());
  }

  private String walk(String at) {
    if (stack.contains(at)) {
      stack.push(at);
      return String.join(", ", stack.toArray(new String[stack.size()]));
    }
    if (visited.contains(at)) {
      return null;
    }
    visited.add(at);
    Set<String> depends = graph.get(at);
    if (depends != null) {
      stack.push(at);
      try {
        for (String depend : depends) {
          String result =walk(depend);
          if (result != null) {
            return result;
          }
        }
      } finally {
        stack.pop();
      }
    }
    return null;
  }

  private String detect() {
    if (!remain.isEmpty()) {
      String result = walk(remain.poll());
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  public static String detect(Map<String, Set<String>> graph) {
    return new Cycle(graph).detect();
  }
}

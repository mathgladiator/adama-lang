/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
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

  public static String detect(Map<String, Set<String>> graph) {
    return new Cycle(graph).detect();
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
          String result = walk(depend);
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
      return result;
    }
    return null;
  }
}

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

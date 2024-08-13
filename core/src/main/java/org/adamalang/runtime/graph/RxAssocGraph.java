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
package org.adamalang.runtime.graph;

import java.util.*;

/** within a graph, this represents all the edges for a single assoc */
public class RxAssocGraph {
  private final HashMap<Integer, TreeMap<Integer, Integer>> edges;
  private final ArrayList<DifferentialEdgeTracker<?>> partials;

  public RxAssocGraph() {
    this.edges = new HashMap<>();
    this.partials = new ArrayList<>();
  }

  public void incr(int from, int to) {
    TreeMap<Integer, Integer> right = edges.get(from);
    if (right == null) {
      right = new TreeMap<>();
      edges.put(from, right);
    }
    Integer prior = right.get(to);
    if (prior == null) {
      prior = 0;
    }
    right.put(to, prior + 1);
  }

  public void decr(int from, int to) {
    TreeMap<Integer, Integer> right = edges.get(from);
    if (right != null) {
      Integer prior = right.get(to);
      if (prior != null) {
        if (prior > 1) {
          right.put(to, prior - 1);
        } else {
          right.remove(to);
        }
      }
      if (right.size() == 0) {
        edges.remove(from);
      }
    }
  }

  public long memory() {
    int mem = 2048;
    for (TreeMap<Integer, Integer> set : edges.values()) {
      mem += 256 + set.size() * 64;
    }
    return mem;
  }

  public TreeSet<Integer> traverse(TreeSet<Integer> left) {
    TreeSet<Integer> right = new TreeSet<>();
    for (int l : left) {
      TreeMap<Integer, Integer> pr = edges.get(l);
      if (pr != null) {
        right.addAll(pr.keySet());
      }
    }
    if (partials.size() > 0) {
      for (DifferentialEdgeTracker<?> p : partials) {
        p.traverseInvalid(left, right);
      }
    }
    return right;
  }

  public void compute() {
    Iterator<DifferentialEdgeTracker<?>> pit = partials.iterator();
    while (pit.hasNext()) {
      DifferentialEdgeTracker<?> det = pit.next();
      if (det.alive()) {
        det.compute();
      } else {
        pit.remove();
        det.kill();
      }
    }
  }

  public void registerTracker(DifferentialEdgeTracker<?> partial) {
    partials.add(partial);
  }
}

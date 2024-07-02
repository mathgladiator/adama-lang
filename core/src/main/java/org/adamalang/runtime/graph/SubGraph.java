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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/** within a graph, this represents all the edges for a single assoc */
public class SubGraph {
  private final HashMap<Integer, TreeSet<Integer>> edges;
  private final ArrayList<HasPartialGraph> partials;

  public SubGraph() {
    this.edges = new HashMap<>();
    this.partials = new ArrayList<>();
  }

  public void remove(int from, int to) {
    TreeSet<Integer> right = edges.get(from);
    if (right != null) {
      right.remove(to);
      if (right.size() == 0) {
        edges.remove(from);
      }
    }
  }

  public void put(int from, int to) {
    TreeSet<Integer> dest = edges.get(from);
    if (dest == null) {
      dest = new TreeSet<>();
      edges.put(from, dest);
    }
    dest.add(to);
  }

  public long memory() {
    int mem = 2048;
    for (TreeSet<Integer> set : edges.values()) {
      mem += 256 + set.size() * 64;
    }
    return mem;
  }

  public TreeSet<Integer> traverse(TreeSet<Integer> left) {
    TreeSet<Integer> right = new TreeSet<>();
    for (int l : left) {
      TreeSet<Integer> pr = edges.get(l);
      if (pr != null) {
        right.addAll(pr);
      }
    }
    if (partials.size() > 0) {
      SubGraph partial = new SubGraph();
      for (HasPartialGraph p : partials) {
        p.populate(partial);
      }
      right.addAll(partial.traverse(left));
    }
    return right;
  }

  public void compute() {
    for(HasPartialGraph partial : partials) {
      partial.compute();
    }
    partials.clear();
  }

  public void link(HasPartialGraph partial) {
    partials.add(partial);
  }
}

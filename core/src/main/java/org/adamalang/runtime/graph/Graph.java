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

import java.util.TreeMap;

/** a graph connects records from within a document by associations */
public class Graph {
  private TreeMap<Short, SubGraph> assocs;

  public Graph() {
    this.assocs = new TreeMap<>();
  }

  public SubGraph getOrCreate(short assoc) {
    SubGraph graph = assocs.get(assoc);
    if (graph == null) {
      graph = new SubGraph();
      assocs.put(assoc, graph);
    }
    return graph;
  }

  public long memory() {
    long mem = 0;
    for (SubGraph sg : assocs.values()) {
      mem += sg.memory();
    }
    return mem;
  }

  public void compute() {
    for (SubGraph sg : assocs.values()) {
      sg.compute();
    }
  }
}

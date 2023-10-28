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

import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.reactives.RxRecordBase;
import org.adamalang.runtime.reactives.RxTable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;

/** an assoc table is a container of edges between two tables drive by a source */
public class AssocTable<B extends RxRecordBase<B>, F extends RxRecordBase<F>, T extends RxRecordBase<T>> {
  private final RxTable<B> source;
  private final Function<B, Integer> computeFromId;
  private final Function<B, Integer> computeToId;

  private class EdgeCache {
    private final int from;
    private final int to;

    private EdgeCache(int from, int to) {
      this.from = from;
      this.to = to;
    }
  }

  private final HashMap<Integer, EdgeCache> edgeCache;
  private final HashSet<Integer> invalid;
  private boolean linked;
  private final SubGraph graph;

  public AssocTable(RxTable<B> source, SubGraph graph, Function<B, Integer> computeFromId, Function<B, Integer> computeToId) {
    this.source = source;
    this.graph = graph;
    this.computeFromId = computeFromId;
    this.computeToId = computeToId;
    this.edgeCache = new HashMap<>();
    this.invalid = new HashSet<>();
    this.linked = false;
  }

  public void change(int id) {
    EdgeCache ec = edgeCache.remove(id);
    if (ec != null) {
      graph.remove(ec.from, ec.to);
    }
    invalid.add(id);
    if (!linked) {
      linked = true;
      // graph.link(this);
    }
  }

  public void commit() {
    for (Integer id : invalid) {
      B row = source.getById(id);
      if (row != null) {
        Integer from = computeFromId.apply(row);
        if (from != null) {
          Integer to = computeToId.apply(row);
          if (to != null) {
            edgeCache.put(id, new EdgeCache(from, to));
            graph.put(from, to);
          }
        }
      }
    }
  }
}

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

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.reactives.RxRecordBase;
import org.adamalang.runtime.reactives.RxTable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** an assoc table is a container of edges between two tables drive by a source */
public class DifferentialEdgeTracker<B extends RxRecordBase<B>> implements RxChild, HasPartialGraph {
  private final RxTable<B> source;
  private final EdgeMaker<B> maker;

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
  private final RxAssocGraph graph;

  public DifferentialEdgeTracker(RxTable<B> source, RxAssocGraph graph, EdgeMaker<B> maker) {
    this.source = source;
    this.graph = graph;
    this.maker = maker;
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
      graph.link(this);
    }
  }
  private void fill(RxAssocGraph partial, boolean cache) {
    for (Integer id : invalid) {
      B row = source.getById(id);
      if (row != null && row.__isAlive()) {
        Integer from = maker.from(row);
        if (from != null) {
          Integer to = maker.to(row);
          if (to != null) {
            if (cache) {
              edgeCache.put(id, new EdgeCache(from, to));
            }
            partial.put(from, to);
          }
        }
      }
    }
  }

  @Override
  public void compute() {
    fill(graph, true);
    invalid.clear();
    linked = false;
  }

  @Override
  public void populate(RxAssocGraph partial) {
    fill(partial, false);
  }

  public void removeAll() {
    for (Map.Entry<Integer, EdgeCache> entry : edgeCache.entrySet()) {
      invalid.add(entry.getKey());
      graph.remove(entry.getValue().from, entry.getValue().to);
    }
    edgeCache.clear();
    if (!linked) {
      linked = true;
      graph.link(this);
    }
  }

  @Override
  public boolean __raiseInvalid() {
    removeAll();
    return source.__isAlive();
  }

  public long memory() {
    return 256 + edgeCache.size() * 64 + invalid.size() * 32;
  }
}

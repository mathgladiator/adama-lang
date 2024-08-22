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

import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.runtime.reactives.RxMapGuard;
import org.adamalang.runtime.reactives.RxRecordBase;
import org.adamalang.runtime.reactives.RxTable;
import org.adamalang.runtime.reactives.maps.MapGuardTarget;
import org.adamalang.runtime.reactives.maps.MapPubSub;

import java.util.*;

/** within a graph, this represents all the edges for a single assoc */
public class RxAssocGraph<TyTo extends RxRecordBase<TyTo>> implements MapGuardTarget {
  private final HashMap<Integer, TreeMap<Integer, Integer>> edges;
  private final ArrayList<DifferentialEdgeTracker<?, ?>> partials;
  private final ArrayList<RxTable<TyTo>> tables;
  private final MapPubSub<Integer> pubsub;
  private final Stack<RxMapGuard> guardsInflight;
  private RxMapGuard activeGuard;


  public RxAssocGraph() {
    this.edges = new HashMap<>();
    this.partials = new ArrayList<>();
    this.tables = new ArrayList<>();
    this.pubsub = new MapPubSub<>(null);
    this.guardsInflight = new Stack<>();
    this.activeGuard = null;
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
    pubsub.changed(from);
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
    pubsub.changed(from);
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
      for (DifferentialEdgeTracker<?, ?> p : partials) {
        p.traverseInvalid(left, right);
      }
    }
    return right;
  }

  public void compute() {
    Iterator<DifferentialEdgeTracker<?, ?>> pit = partials.iterator();
    while (pit.hasNext()) {
      DifferentialEdgeTracker<?, ?> det = pit.next();
      if (det.alive()) {
        det.compute();
      } else {
        pit.remove();
        det.kill();
      }
    }
  }

  public void registerTracker(DifferentialEdgeTracker<?, ?> partial) {
    partials.add(partial);
  }

  public void registerTo(RxTable<TyTo> table) {
    tables.add(table);
  }

  public void __settle(Set<Integer> __viewers) {
    Iterator<RxTable<TyTo>> it = tables.iterator();
    while (it.hasNext()) {
      if (!it.next().__isAlive()) {
        it.remove();
      }
    }
  }

  public NtList<TyTo> map(NtList<? extends RxRecordBase<?>> list) {
    TreeSet<Integer> ids = new TreeSet<>();
    for (RxRecordBase<?> item : list) {
      int from = item.__id();
      ids.add(from);
      if (activeGuard != null) {
        activeGuard.readKey(from);
      }
    }
    ArrayList<TyTo> output = new ArrayList<>();
    TreeSet<Integer> result = traverse(ids);
    for (Integer out : result) {
      for (RxTable<TyTo> table : tables) {
        TyTo candidate = table.getById(out);
        if (candidate != null) {
          table.readPrimaryKey(out);
          output.add(candidate);
          break;
        }
      }
    }
    return new ArrayNtList<>(output);
  }

  @Override
  public void pushGuard(RxMapGuard guard) {
    guardsInflight.push(guard);
    activeGuard = guard;
  }

  @Override
  public void popGuard() {
    guardsInflight.pop();
    if (guardsInflight.empty()) {
      activeGuard = null;
    } else {
      activeGuard = guardsInflight.peek();
    }
  }
}

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
package org.adamalang.translator.env.topo;

import java.util.*;

/** a simple class for building a topological order */
public class TopologicalSort<T> {
  public class TopoValue {
    public final T value;
    public final Set<String> dependencies;
    private boolean handled;

    public TopoValue(T value, Set<String> dependencies) {
      this.value = value;
      this.dependencies = dependencies;
      this.handled = dependencies == null;
    }
  }
  private final HashMap<String, TopoValue> values;
  private final ArrayList<T> result;
  private final ArrayDeque<String> remain;
  private final TreeSet<String> cycles;

  public TopologicalSort() {
    this.values = new HashMap<>();
    this.result = new ArrayList<>();
    this.remain = new ArrayDeque<>();
    this.cycles = new TreeSet<>();
  }

  /** add a single item */
  public void add(String key, T value, Set<String> rawDependencies) {
    Set<String> dependencies = rawDependencies != null ? (rawDependencies.isEmpty() ? null : rawDependencies) : null;
    TopoValue val = new TopoValue(value, dependencies);
    values.put(key, val);

    if (dependencies != null) {
      if (allDependenciesHandled(dependencies)) {
        dependencies = null;
      }
    }

    if (dependencies == null) {
      val.handled = true;
      result.add(value);
    } else {
      remain.add(key);
    }
  }

  private boolean allDependenciesHandled(Set<String> dependencies) {
    for (String depend : dependencies) {
      TopoValue exists = values.get(depend);
      if (exists == null) {
        return false;
      }
      if (!exists.handled) {
        return false;
      }
    }
    return true;
  }

  /** core algorithm to insert items requiring dependencies first */
  private void insert(String key, String butNot) {
    if (key.equals(butNot)) {
      cycles.add(key);
      return;
    }

    TopoValue val = values.get(key);
    if (val == null) {
      return;
    }
    if (val.handled) {
      return;
    }
    val.handled = true;

    // make sure all the children are present
    for (String depend : val.dependencies) {
      insert(depend, butNot == null ? key : butNot);
    }

    result.add(val.value);
    remain.remove(key);
  }

  /** drain remaining items */
  private void drainRemain() {
    while (!remain.isEmpty()) {
      insert(remain.removeFirst(), null);
    }
  }

  /** get the results sorted */
  public ArrayList<T> sort() {
    drainRemain();
    return result;
  }

  /** get any elements part of a cycle */
  public Collection<String> cycles() {
    return cycles;
  }
}

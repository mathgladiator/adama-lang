/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
    values.put(key, new TopoValue(value, dependencies));
    if (dependencies == null) {
      // immediately spit out a "free" result
      result.add(value);
    } else {
      remain.add(key);
    }
  }

  /** core algorithm to insert items requiring dependencies first */
  private void insert(String key, String butNot) {
    if (key.equals(butNot)) {
      cycles.add(key);
      return;
    }

    TopoValue val = values.get(key);
    if (val.handled) {
      return;
    }

    // make sure all the children are present
    for (String depend : val.dependencies) {
      insert(depend, butNot == null ? key : butNot);
    }

    val.handled = true;
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

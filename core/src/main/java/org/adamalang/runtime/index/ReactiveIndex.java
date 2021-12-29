/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.index;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import org.adamalang.runtime.reactives.RxRecordBase;

/** an index of a single column of data */
public class ReactiveIndex<Ty extends RxRecordBase> {
  /** a data structure which is precise; we know that the given item is in this
   * bucket for SURE */
  private final HashMap<Integer, TreeSet<Ty>> index;
  /** as things change, we lose certainty of where items exist and have a grab-all
   * bucket; this is an optimization such that indexing happens between operations */
  private final TreeSet<Ty> unknowns;

  public ReactiveIndex(final TreeSet<Ty> unknowns) {
    this.index = new HashMap<>();
    this.unknowns = unknowns;
  }

  /** add the item to the given index (via value `at`) */
  public void add(final int at, final Ty item) {
    var set = index.get(at);
    if (set == null) {
      set = new TreeSet<>();
      index.put(at, set);
    }
    set.add(item);
  }

  /** delete the item from the given index (via value `at`) */
  public boolean delete(final int at, final Ty item) {
    final var set = index.get(at);
    final var result = set.remove(item);
    if (set.size() == 0) {
      index.remove(at);
    }
    return result;
  }

  /** remove the item from the unknowns */
  public void delete(final Ty item) {
    unknowns.remove(item);
  }

  /** get the index */
  public TreeSet<Ty> of(final int at) {
    return index.get(at);
  }

  /** remove the item from the index */
  public void remove(final int at, final Ty item) {
    if (delete(at, item)) {
      unknowns.add(item);
    }
  }

  /** (approx) how many bytes of memory does this index use */
  public long memory() {
    long sum = 64;
    for (Map.Entry<Integer, TreeSet<Ty>> entry : index.entrySet()) {
      sum += entry.getValue().size() * 20 + 20;
    }
    return sum;
  }
}

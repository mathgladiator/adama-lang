/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.index;

import java.util.HashMap;
import java.util.TreeSet;
import org.adamalang.runtime.reactives.RxRecordBase;

/** an index of a single column of data */
public class ReactiveIndex<Ty extends RxRecordBase> {
  /** a data structure which is precise; we know that the given item is in this bucket for SURE */
  private final HashMap<Integer, TreeSet<Ty>> index;

  /** as things change, we lose certainty of where items exist and have a grab-all bucket */
  private final TreeSet<Ty> unknowns;

  public ReactiveIndex(final TreeSet<Ty> unknowns) {
    this.index = new HashMap<>();
    this.unknowns = unknowns;
  }

  public void add(final int at, final Ty item) {
    var set = index.get(at);
    if (set == null) {
      set = new TreeSet<>();
      index.put(at, set);
    }
    set.add(item);
  }

  public boolean delete(final int at, final Ty item) {
    final var set = index.get(at);
    final var result = set.remove(item);
    if (set.size() == 0) {
      index.remove(at);
    }
    return result;
  }

  public void delete(final Ty item) {
    unknowns.remove(item);
  }

  public TreeSet<Ty> of(final int value) {
    return index.get(value);
  }

  public void remove(final int at, final Ty item) {
    if (delete(at, item)) {
      unknowns.add(item);
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.index;

import org.adamalang.runtime.contracts.IndexQuerySet;
import org.adamalang.runtime.reactives.RxRecordBase;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/** an index of a single column of data */
public class ReactiveIndex<Ty extends RxRecordBase> {
  /** a data structure which is precise; we know that the given item is in this bucket for SURE */
  private final TreeMap<Integer, TreeSet<Ty>> index;
  /**
   * as things change, we lose certainty of where items exist and have a grab-all bucket; this is an
   * optimization such that indexing happens between operations
   */
  private final TreeSet<Ty> unknowns;

  public ReactiveIndex(final TreeSet<Ty> unknowns) {
    this.index = new TreeMap<>();
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

  /** remove the item from the unknowns */
  public void delete(final Ty item) {
    unknowns.remove(item);
  }

  /** get the index */
  public TreeSet<Ty> of(final int at, IndexQuerySet.LookupMode mode) {
    switch (mode) {
      case LessThan: {
        TreeSet<Ty> values = new TreeSet<>();
        Iterator<Map.Entry<Integer, TreeSet<Ty>>> it = index.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<Integer, TreeSet<Ty>> entry = it.next();
          if (entry.getKey() < at) {
            values.addAll(entry.getValue());
          } else {
            break;
          }
        }
        if (values.size() == 0) {
          return null;
        }
        return values;
      }
      case LessThanOrEqual: {
        TreeSet<Ty> values = new TreeSet<>();
        Iterator<Map.Entry<Integer, TreeSet<Ty>>> it = index.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<Integer, TreeSet<Ty>> entry = it.next();
          if (entry.getKey() <= at) {
            values.addAll(entry.getValue());
          } else {
            break;
          }
        }
        if (values.size() == 0) {
          return null;
        }
        return values;
      }
      case GreaterThan: {
        TreeSet<Ty> values = new TreeSet<>();
        Iterator<Map.Entry<Integer, TreeSet<Ty>>> it = index.descendingMap().entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<Integer, TreeSet<Ty>> entry = it.next();
          if (entry.getKey() > at) {
            values.addAll(entry.getValue());
          } else {
            break;
          }
        }
        if (values.size() == 0) {
          return null;
        }
        return values;
      }
      case GreaterThanOrEqual: {
        TreeSet<Ty> values = new TreeSet<>();
        Iterator<Map.Entry<Integer, TreeSet<Ty>>> it = index.descendingMap().entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<Integer, TreeSet<Ty>> entry = it.next();
          if (entry.getKey() >= at) {
            values.addAll(entry.getValue());
          } else {
            break;
          }
        }
        if (values.size() == 0) {
          return null;
        }
        return values;
      }
      default:
        return index.get(at);
    }
  }

  /** remove the item from the index */
  public void remove(final int at, final Ty item) {
    if (delete(at, item)) {
      unknowns.add(item);
    }
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

  /** (approx) how many bytes of memory does this index use */
  public long memory() {
    long sum = 64;
    for (Map.Entry<Integer, TreeSet<Ty>> entry : index.entrySet()) {
      sum += entry.getValue().size() * 20 + 20;
    }
    return sum;
  }
}

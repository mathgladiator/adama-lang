/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.index;

import org.adamalang.runtime.contracts.IndexQuerySet;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/** shared library for evaluating the look up mode against an index */

public class EvaluateLookupMode {
  /** get the index */
  public static <Ty> TreeSet<Ty> of(TreeMap<Integer, TreeSet<Ty>> index, final int at, IndexQuerySet.LookupMode mode) {
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

}

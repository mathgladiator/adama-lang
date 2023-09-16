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

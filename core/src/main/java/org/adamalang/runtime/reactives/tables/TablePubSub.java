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
package org.adamalang.runtime.reactives.tables;

import org.adamalang.runtime.contracts.RxParent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;

/** simple pubsub fanout for TableSubscription's under a parent  */
public class TablePubSub implements TableSubscription {
  private final RxParent owner;
  private final ArrayList<TableSubscription> subscriptions;
  private final TreeSet<Integer> filter;
  private final TreeSet<IndexInvalidCacheHit> filterIndex;

  class IndexInvalidCacheHit implements Comparable<IndexInvalidCacheHit> {
    public final int column;
    public final int value;

    public IndexInvalidCacheHit(int column, int value) {
      this.column = column;
      this.value = value;
    }

    @Override
    public int compareTo(IndexInvalidCacheHit o) {
      int delta = Integer.compare(value, o.value);
      if (delta == 0) {
        return Integer.compare(column, o.column);
      }
      return delta;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      IndexInvalidCacheHit that = (IndexInvalidCacheHit) o;
      return column == that.column && value == that.value;
    }

    @Override
    public int hashCode() {
      return Objects.hash(column, value);
    }
  }

  public TablePubSub(RxParent owner) {
    this.owner = owner;
    this.subscriptions = new ArrayList<>();
    this.filter = new TreeSet<>();
    this.filterIndex = new TreeSet<>();
  }

  public int count() {
    return subscriptions.size();
  }

  public void subscribe(TableSubscription ts) {
    subscriptions.add(ts);
  }

  @Override
  public boolean alive() {
    if (owner != null) {
      return owner.__isAlive();
    }
    return true;
  }

  @Override
  public boolean primary(int primaryKey) {
    if (filter.contains(primaryKey)) {
      return false;
    }
    filter.add(primaryKey);
    for (TableSubscription ts : subscriptions) {
      ts.primary(primaryKey);
    }
    return true;
  }

  @Override
  public void index(int field, int value) {
    IndexInvalidCacheHit hit = new IndexInvalidCacheHit(field, value);
    if (filterIndex.contains(hit)) {
      return;
    }
    filterIndex.add(hit);
    for (TableSubscription ts : subscriptions) {
      ts.index(field, value);
    }
  }

  public void settle() {
    filter.clear();
    filterIndex.clear();
  }

  public void gc() {
    Iterator<TableSubscription> it = subscriptions.iterator();
    while (it.hasNext()) {
      if (!it.next().alive()) {
        it.remove();
      }
    }
  }

  public long __memory() {
    return 128 * subscriptions.size() + 2048;
  }
}

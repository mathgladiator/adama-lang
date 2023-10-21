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
package org.adamalang.runtime.natives.lists;

import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.reactives.RxRecordBase;
import org.adamalang.runtime.reactives.RxTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/** adapts a table to a list; the birthplace for the query optimized stuff */
public class SelectorRxObjectList<Ty extends RxRecordBase<Ty>> implements NtList<Ty> {
  private final RxTable<Ty> table;
  private WhereClause<Ty> filter;
  private ArrayList<Ty> finalized;

  public SelectorRxObjectList(final RxTable<Ty> table) {
    this.table = table;
    this.filter = null;
  }

  @Override
  public void __delete() {
    ensureFinalized();
    for (final Ty item : finalized) {
      item.__delete();
    }
    table.__raiseDirty();
  }

  private void ensureFinalized() {
    if (this.finalized == null) {
      int cost = 0;
      finalized = new ArrayList<>();
      if (filter != null) {
        for (final Ty item : table.scan(filter)) {
          cost++;
          if (item.__isDying()) {
            continue;
          }
          if (filter.test(item)) {
            finalized.add(item);
          }
        }
      } else {
        table.readAll();
        for (final Ty item : table) {
          cost++;
          if (item.__isDying()) {
            continue;
          }
          finalized.add(item);
        }
      }
      table.__cost(cost);
    }
  }

  @Override
  public NtList<Ty> get() {
    return this;
  }

  @Override
  public NtMaybe<Ty> lookup(final int k) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).lookup(k);
  }

  @Override
  public NtMaybe<Ty> lookup(NtMaybe<Integer> k) {
    if (k.has()) {
      return lookup(k.get());
    }
    return new NtMaybe<>();
  }

  @Override
  public void map(final Consumer<Ty> t) {
    ensureFinalized();
    for (final Ty item : finalized) {
      t.accept(item);
    }
  }

  @Override
  public <R> NtList<R> mapFunction(Function<Ty, R> foo) {
    ensureFinalized();
    ArrayList<R> result = new ArrayList<>();
    for (final Ty item : finalized) {
      result.add(foo.apply(item));
    }
    return new ArrayNtList<>(result);
  }

  @Override
  public NtList<Ty> orderBy(final boolean done, final Comparator<Ty> cmp) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).orderBy(true, cmp);
  }

  @Override
  public <TIn, TOut> NtMap<TIn, TOut> reduce(final Function<Ty, TIn> domain, final Function<NtList<Ty>, TOut> reducer) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).reduce(domain, reducer);
  }

  @Override
  public NtList<Ty> shuffle(final boolean done, final Random rng) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).shuffle(true, rng);
  }

  @Override
  public int size() {
    // should this be optimized... the deletion mechanism kind of sucks
    ensureFinalized();
    return finalized.size();
  }

  @Override
  public NtList<Ty> skip(final boolean done, final int skip) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).skip(true, skip);
  }

  @Override
  public NtList<Ty> limit(final boolean done, final int limit) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).limit(true, limit);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Ty[] toArray(final Function<Integer, Object> arrayMaker) {
    ensureFinalized();
    return finalized.toArray((Ty[]) arrayMaker.apply(finalized.size()));
  }

  @Override
  public <Out> NtList<Out> transform(final Function<Ty, Out> t) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).transform(t);
  }

  @Override
  public NtList<Ty> where(final boolean done, final WhereClause<Ty> filter) {
    if (filter.getPrimaryKey() != null) {
      table.readPrimaryKey(filter.getPrimaryKey());
      final var primary = table.getById(filter.getPrimaryKey());
      finalized = new ArrayList<>(0);
      if (primary != null) {
        if (!primary.__isDying()) {
          finalized.add(primary);
        }
      }
      return new ArrayNtList<>(finalized).where(true, filter);
    }
    this.filter = filter;
    ensureFinalized();
    return new ArrayNtList<>(finalized);
  }

  @Override
  public Iterator<Ty> iterator() {
    ensureFinalized();
    return finalized.iterator();
  }
}

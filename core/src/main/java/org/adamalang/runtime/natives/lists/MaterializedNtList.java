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

import org.adamalang.runtime.contracts.IndexQuerySet;
import org.adamalang.runtime.contracts.MultiIndexable;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.index.EvaluateLookupMode;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.natives.NtMaybe;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class MaterializedNtList<Ty extends MultiIndexable> implements NtList<Ty> {
  private final NtList<Ty> list;
  private final TreeMap<Integer, Ty> byIndex;
  private final TreeMap<Integer, TreeSet<Integer>>[] indicies;

  public MaterializedNtList(NtList<Ty> list, int numberIndicies) {
    this.list = list;
    this.byIndex = new TreeMap<>();
    this.indicies = new TreeMap[numberIndicies];
    for (int k = 0; k < numberIndicies; k++) {
      this.indicies[k] = new TreeMap<>();
    }
    int id = 0;
    for (Ty item : list) {
      byIndex.put(id, item);
      int[] vals = item.__getIndexValues();
      for (int k = 0; k < vals.length; k++) {
        TreeSet<Integer> indexValues = indicies[k].get(vals[k]);
        if (indexValues == null) {
          indexValues = new TreeSet<>();
          indicies[k].put(vals[k], indexValues);
        }
        indexValues.add(id);
      }
      id++;
    }
  }

  @Override
  public void __delete() {
    list.__delete();;
  }

  @Override
  public NtList<Ty> get() {
    return list;
  }

  @Override
  public NtMaybe<Ty> lookup(int k) {
    return list.lookup(k);
  }

  @Override
  public NtMaybe<Ty> lookup(NtMaybe<Integer> k) {
    return list.lookup(k);
  }

  @Override
  public void map(Consumer<Ty> t) {
    list.map(t);
  }

  @Override
  public <R> NtList<R> mapFunction(Function<Ty, R> foo) {
    return list.mapFunction(foo);
  }

  @Override
  public NtList<Ty> orderBy(boolean done, Comparator<Ty> cmp) {
    return list.orderBy(done, cmp);
  }

  @Override
  public <TIn, TOut> NtMap<TIn, TOut> reduce(Function<Ty, TIn> domain, Function<NtList<Ty>, TOut> reducer) {
    return list.reduce(domain, reducer);
  }

  @Override
  public NtList<Ty> shuffle(boolean done, Random rng) {
    return list.shuffle(done, rng);
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public NtList<Ty> skip(boolean done, int skip) {
    return list.skip(done, skip);
  }

  @Override
  public NtList<Ty> limit(boolean done, int limit) {
    return list.limit(done, limit);
  }

  @Override
  public Ty[] toArray(Function<Integer, Object> arrayMaker) {
    return list.toArray(arrayMaker);
  }

  @Override
  public <Out> NtList<Out> transform(Function<Ty, Out> t) {
    return list.transform(t);
  }

  @Override
  public NtList<Ty> where(boolean done, WhereClause<Ty> filter) {
    MaterializedIndexQuerySet miqs = new MaterializedIndexQuerySet();
    filter.scopeByIndicies(miqs);
    if (miqs.all) {
      return list.where(done, filter);
    } else {
      ArrayList<Ty> results = new ArrayList<>();
      for (int id : miqs.ids) {
        Ty item = byIndex.get(id);
        if (filter.test(item)) {
          results.add(item);
        }
      }
      return new ArrayNtList<>(results);
    }
  }

  @Override
  public Iterator<Ty> iterator() {
    return list.iterator();
  }

  private TreeSet<Integer> of(int column, int value, IndexQuerySet.LookupMode mode) {
    return EvaluateLookupMode.of(indicies[column], value, mode);
  }

  public class MaterializedIndexQuerySet implements IndexQuerySet {
    private boolean all;
    private TreeSet<Integer> ids;

    public MaterializedIndexQuerySet() {
      this.all = true;
      this.ids = null;
    }

    @Override
    public void intersect(int column, int value, LookupMode mode) {
      if (all) {
        all = false;
        ids = of(column, value, mode);
      } else {
        TreeSet<Integer> result = new TreeSet<>();
        TreeSet<Integer> a = ids;
        TreeSet<Integer> b = of(column, value, mode);
        if (b.size() < a.size()) {
          a = b;
          b = ids;
        }
        for (Integer x : a) {
          if (b.contains(x)) {
            result.add(x);
          }
        }
        ids = result;
      }
    }

    public void push() {
    }

    @Override
    public void finish() {
    }
  }
}

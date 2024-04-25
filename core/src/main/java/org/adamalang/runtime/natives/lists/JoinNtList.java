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

import org.adamalang.runtime.contracts.Ranker;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.natives.NtMaybe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public class JoinNtList<Ty> implements NtList<Ty> {
  private final NtList<Ty> left;
  private final NtList<Ty> right;

  public JoinNtList(NtList<Ty> left, NtList<Ty> right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public void __delete() {
    left.__delete();
    right.__delete();
  }

  @Override
  public NtList<Ty> get() {
    return this;
  }

  @Override
  public NtMaybe<Ty> lookup(int k) {
    int n = left.size();
    if (k < n) {
      return left.lookup(k);
    } else {
      return right.lookup(k - n);
    }
  }

  @Override
  public NtMaybe<Ty> lookup(NtMaybe<Integer> k) {
    if (k.has()) {
      return lookup(k.get());
    } else {
      return new NtMaybe<>();
    }
  }

  @Override
  public void map(Consumer<Ty> t) {
    left.map(t);
    right.map(t);
  }

  @Override
  public <R> NtList<R> mapFunction(Function<Ty, R> foo) {
    return new JoinNtList<>(left.mapFunction(foo), right.mapFunction(foo));
  }

  private ArrayNtList<Ty> materialize() {
    ArrayList<Ty> vals = new ArrayList<>();
    for (Ty val : left) {
      vals.add(val);
    }
    for (Ty val : right) {
      vals.add(val);
    }
    return new ArrayNtList<>(vals);
  }

  @Override
  public NtList<Ty> orderBy(boolean done, Comparator<Ty> cmp) {
    return materialize().orderBy(done, cmp);
  }

  @Override
  public <TIn, TOut> NtMap<TIn, TOut> reduce(Function<Ty, TIn> domain, Function<NtList<Ty>, TOut> reducer) {
    return materialize().reduce(domain, reducer);
  }

  @Override
  public NtList<Ty> shuffle(boolean done, Random rng) {
    return materialize().shuffle(done, rng);
  }

  @Override
  public int size() {
    return left.size() + right.size();
  }

  @Override
  public NtList<Ty> skip(boolean done, int skip) {
    int n = left.size();
    if (skip < n) {
      return new JoinNtList<>(left.skip(done, skip), right);
    } else {
      return right.skip(done, skip - n);
    }
  }

  @Override
  public NtList<Ty> limit(boolean done, int limit) {
    int n = left.size();
    if (limit < n) {
      return left.limit(done, limit);
    } else {
      return new JoinNtList<>(left, right.limit(done, limit - n));
    }
  }

  @Override
  public Ty[] toArray(Function<Integer, Object> arrayMaker) {
    Ty[] arr = (Ty[]) arrayMaker.apply(left.size() + right.size());
    int at = 0;
    Iterator<Ty> it = iterator();
    while (it.hasNext()) {
      arr[at] = it.next();
      at++;
    }
    return arr;
  }

  @Override
  public <Out> NtList<Out> transform(Function<Ty, Out> t) {
    return new JoinNtList<>(left.transform(t), right.transform(t));
  }

  @Override
  public NtList<Ty> where(boolean done, WhereClause<Ty> filter) {
    return new JoinNtList<>(left.where(done, filter), right.where(done, filter));
  }

  @Override
  public Iterator<Ty> iterator() {
    return new Iterator<Ty>() {
      Iterator<Ty> a = left.iterator();
      Iterator<Ty> b = right.iterator();
      boolean useA = true;

      @Override
      public boolean hasNext() {
        if (useA) {
          if (a.hasNext()) {
            return true;
          } else {
            useA = false;
          }
        }
        return b.hasNext();
      }

      @Override
      public Ty next() {
        if (useA) {
          return a.next();
        } else {
          return b.next();
        }
      }
    };
  }

  @Override
  public <KeyT> NtList<Ty> unique(ListUniqueMode mode, Function<Ty, KeyT> extract) {
    return materialize().unique(mode, extract);
  }

  @Override
  public NtList<Ty> rank(Ranker<Ty> ranker) {
    ArrayList<Ty> sum = new ArrayList<>();
    for (Ty item : left) {
      sum.add(item);
    }
    for (Ty item : right) {
      sum.add(item);
    }
    return new ArrayNtList<>(sum).rank(ranker);
  }
}

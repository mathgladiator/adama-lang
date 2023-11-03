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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/** a list backed by nothing */
public class EmptyNtList<T> implements NtList<T> {
  public EmptyNtList() {
  }

  @Override
  public void __delete() {
  }

  @Override
  public NtList<T> get() {
    return this;
  }

  @Override
  public NtMaybe<T> lookup(final int k) {
    return new NtMaybe<>();
  }

  @Override
  public NtMaybe<T> lookup(NtMaybe<Integer> k) {
    return new NtMaybe<>();
  }

  @Override
  public void map(final Consumer<T> t) {
  }

  @Override
  public <R> NtList<R> mapFunction(Function<T, R> foo) {
    return new EmptyNtList<>();
  }

  @Override
  public NtList<T> orderBy(final boolean done, final Comparator<T> cmp) {
    return this;
  }

  @Override
  public <TIn, TOut> NtMap<TIn, TOut> reduce(final Function<T, TIn> domain, final Function<NtList<T>, TOut> reducer) {
    return new NtMap<>();
  }

  @Override
  public NtList<T> shuffle(final boolean done, final Random rng) {
    return this;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public NtList<T> skip(final boolean done, final int skip) {
    return this;
  }

  @Override
  public NtList<T> limit(final boolean done, final int limit) {
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T[] toArray(final Function<Integer, Object> arrayMaker) {
    return (T[]) arrayMaker.apply(0);
  }

  @Override
  public <Out> NtList<Out> transform(final Function<T, Out> t) {
    return new EmptyNtList<>();
  }

  @Override
  public NtList<T> where(final boolean done, final WhereClause<T> filter) {
    return this;
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        return false;
      }

      @Override
      public T next() {
        return null;
      }
    };
  }

  @Override
  public <KeyT> NtList<T> unique(ListUniqueMode mode, Function<T, KeyT> extract) {
    return this;
  }
}

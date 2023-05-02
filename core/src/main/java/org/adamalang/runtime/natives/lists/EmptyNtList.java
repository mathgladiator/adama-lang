/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
}

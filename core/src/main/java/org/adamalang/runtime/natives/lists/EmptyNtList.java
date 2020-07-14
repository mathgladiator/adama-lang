/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives.lists;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import org.adamalang.runtime.contracts.Bridge;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.natives.NtMaybe;

/** a list backed by nothing */
public class EmptyNtList<T> implements NtList<T> {
  private final Bridge<T> bridge;

  public EmptyNtList(final Bridge<T> bridge) {
    this.bridge = bridge;
  }

  @Override
  public void __delete() {
  }

  @Override
  public NtList<T> get() {
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
  public NtMaybe<T> lookup(final int k) {
    return new NtMaybe<>();
  }

  @Override
  public void map(final Consumer<T> t) {
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
  public NtList<T> skipAndLimit(final boolean done, final int skip, final int limit) {
    return this;
  }

  @Override
  public T[] toArray() {
    return bridge.makeArray(0);
  }

  @Override
  public <Out> NtList<Out> transform(final Function<T, Out> t, final Bridge<Out> newBridge) {
    return new EmptyNtList<>(newBridge);
  }

  @Override
  public NtList<T> where(final boolean done, final WhereClause<T> filter) {
    return this;
  }
}

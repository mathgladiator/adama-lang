/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.contracts.WhereClause;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/** the core list abstraction */
public interface NtList<Ty> extends Iterable<Ty> {
  void __delete();

  NtList<Ty> get();

  NtMaybe<Ty> lookup(int k);

  NtMaybe<Ty> lookup(NtMaybe<Integer> k);

  void map(Consumer<Ty> t);

  <R> NtList<R> mapFunction(Function<Ty, R> foo);

  NtList<Ty> orderBy(boolean done, Comparator<Ty> cmp);

  <TIn, TOut> NtMap<TIn, TOut> reduce(Function<Ty, TIn> domain, Function<NtList<Ty>, TOut> reducer);

  NtList<Ty> shuffle(boolean done, Random rng);

  int size();

  NtList<Ty> skip(boolean done, int skip);

  NtList<Ty> limit(boolean done, int limit);

  Ty[] toArray(Function<Integer, Object> arrayMaker);

  <Out> NtList<Out> transform(Function<Ty, Out> t);

  NtList<Ty> where(boolean done, WhereClause<Ty> filter);
}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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

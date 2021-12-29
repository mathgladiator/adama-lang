/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.natives;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import org.adamalang.runtime.contracts.WhereClause;

/** the core list abstraction */
public interface NtList<Ty> extends Iterable<Ty> {
  public void __delete();
  public NtList<Ty> get();
  public NtMaybe<Ty> lookup(int k);
  public void map(Consumer<Ty> t);
  public NtList<Ty> orderBy(boolean done, Comparator<Ty> cmp);
  public <TIn, TOut> NtMap<TIn, TOut> reduce(Function<Ty, TIn> domain, Function<NtList<Ty>, TOut> reducer);
  public NtList<Ty> shuffle(boolean done, Random rng);
  public int size();
  public NtList<Ty> skipAndLimit(boolean done, int skip, int limit);
  public Ty[] toArray(Function<Integer, Object> arrayMaker);
  public <Out> NtList<Out> transform(Function<Ty, Out> t);
  public NtList<Ty> where(boolean done, WhereClause<Ty> filter);
}

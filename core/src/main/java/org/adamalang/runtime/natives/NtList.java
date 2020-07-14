/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import org.adamalang.runtime.contracts.Bridge;
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
  public Ty[] toArray();
  public <Out> NtList<Out> transform(Function<Ty, Out> t, Bridge<Out> bridge);
  public NtList<Ty> where(boolean done, WhereClause<Ty> filter);
}

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
package org.adamalang.runtime.natives;

import org.adamalang.runtime.contracts.Ranker;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.natives.lists.ListUniqueMode;

import java.util.Comparator;
import java.util.Random;
import java.util.function.BiFunction;
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

  <KeyT> NtList<Ty> unique(ListUniqueMode mode, Function<Ty, KeyT> extract);

  NtList<Ty> rank(Ranker<Ty> ranker);
}

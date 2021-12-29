/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.natives.lists;

import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.reactives.RxRecordBase;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/** a list backed by an array */
public class ArrayNtList<Ty> implements NtList<Ty> {
  private final ArrayList<Ty> list;

  public ArrayNtList(final ArrayList<Ty> list) {
    this.list = list;
  }

  @Override
  public void __delete() {
    for (final Ty item : list) {
      if (item instanceof RxRecordBase) {
        ((RxRecordBase) item).__delete();
      }
    }
  }

  @Override
  public NtList<Ty> get() {
    return this;
  }

  @Override
  public NtMaybe<Ty> lookup(final int k) {
    final var result = new NtMaybe<Ty>();
    if (0 <= k && k < list.size()) {
      result.set(list.get(k));
    }
    return result;
  }

  @Override
  public void map(final Consumer<Ty> t) {
    for (final Ty item : list) {
      t.accept(item);
    }
  }

  @Override
  public NtList<Ty> orderBy(final boolean done, final Comparator<Ty> cmp) {
    list.sort(cmp);
    return this;
  }

  @Override
  public <TIn, TOut> NtMap<TIn, TOut> reduce(
      final Function<Ty, TIn> domainExtract, final Function<NtList<Ty>, TOut> reducer) {
    final var map = new NtMap<TIn, TOut>();
    final var shredded = new TreeMap<TIn, ArrayList<Ty>>();
    for (final Ty item : list) {
      final var domain = domainExtract.apply(item);
      var bucket = shredded.get(domain);
      if (bucket == null) {
        bucket = new ArrayList<>();
        shredded.put(domain, bucket);
      }
      bucket.add(item);
    }
    for (final Map.Entry<TIn, ArrayList<Ty>> entry : shredded.entrySet()) {
      final var value = reducer.apply(new ArrayNtList<>(entry.getValue()));
      map.storage.put(entry.getKey(), value);
    }
    return map;
  }

  @Override
  public NtList<Ty> shuffle(final boolean done, final Random rng) {
    for (var k = list.size() - 1; k >= 0; k--) {
      final var swapWith = rng.nextInt(list.size());
      if (swapWith != k) {
        final var t = list.get(k);
        list.set(k, list.get(swapWith));
        list.set(swapWith, t);
      }
    }
    return this;
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public NtList<Ty> skipAndLimit(final boolean done, final int skip, final int limit) {
    final var next = new ArrayList<Ty>(limit);
    final var it = iterator();
    for (var k = 0; k < skip && it.hasNext(); k++) {
      it.next();
    }
    for (var k = 0; k < limit && it.hasNext(); k++) {
      next.add(it.next());
    }
    return new ArrayNtList<>(next);
  }

  @Override
  public Iterator<Ty> iterator() {
    return list.iterator();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Ty[] toArray(final Function<Integer, Object> arrayMaker) {
    return list.toArray((Ty[]) arrayMaker.apply(list.size()));
  }

  @Override
  public <Out> NtList<Out> transform(final Function<Ty, Out> t) {
    final var output = new ArrayList<Out>(list.size());
    for (final Ty item : list) {
      output.add(t.apply(item));
    }
    return new ArrayNtList<>(output);
  }

  @Override
  public NtList<Ty> where(final boolean done, final WhereClause<Ty> filter) {
    final var next = new ArrayList<Ty>();
    for (final Ty item : list) {
      if (filter.test(item)) {
        next.add(item);
      }
    }
    return new ArrayNtList<>(next);
  }
}

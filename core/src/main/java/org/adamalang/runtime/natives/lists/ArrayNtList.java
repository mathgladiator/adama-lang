/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives.lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import org.adamalang.runtime.contracts.Bridge;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.reactives.RxRecordBase;

/** a list backed by an array */
public class ArrayNtList<Ty> implements NtList<Ty> {
  private final Bridge<Ty> bridge;
  private final ArrayList<Ty> list;

  public ArrayNtList(final ArrayList<Ty> list, final Bridge<Ty> bridge) {
    this.list = list;
    this.bridge = bridge;
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
  public Iterator<Ty> iterator() {
    return list.iterator();
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
  public <TIn, TOut> NtMap<TIn, TOut> reduce(final Function<Ty, TIn> domainExtract, final Function<NtList<Ty>, TOut> reducer) {
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
      final var value = reducer.apply(new ArrayNtList<>(entry.getValue(), null));
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
    return new ArrayNtList<>(next, bridge);
  }

  @Override
  public Ty[] toArray() {
    return list.toArray(bridge.makeArray(list.size()));
  }

  @Override
  public <Out> NtList<Out> transform(final Function<Ty, Out> t, final Bridge<Out> bridge) {
    final var output = new ArrayList<Out>(list.size());
    for (final Ty item : list) {
      output.add(t.apply(item));
    }
    return new ArrayNtList<>(output, bridge);
  }

  @Override
  public NtList<Ty> where(final boolean done, final WhereClause<Ty> filter) {
    final var next = new ArrayList<Ty>();
    for (final Ty item : list) {
      if (filter.test(item)) {
        next.add(item);
      }
    }
    return new ArrayNtList<>(next, bridge);
  }
}

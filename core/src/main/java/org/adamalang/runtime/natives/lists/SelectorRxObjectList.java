/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.natives.lists;

import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.reactives.RxRecordBase;
import org.adamalang.runtime.reactives.RxTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/** adapts a table to a list; the birthplace for the query optimized stuff */
public class SelectorRxObjectList<Ty extends RxRecordBase<Ty>> implements NtList<Ty> {
  private final RxTable<Ty> table;
  private WhereClause<Ty> filter;
  private ArrayList<Ty> finalized;

  public SelectorRxObjectList(final RxTable<Ty> table) {
    this.table = table;
    this.filter = null;
  }

  @Override
  public void __delete() {
    ensureFinalized();
    for (final Ty item : finalized) {
      item.__delete();
    }
    table.__raiseDirty();
  }

  private void ensureFinalized() {
    if (this.finalized == null) {
      finalized = new ArrayList<>();
      if (filter != null) {
        if (table.document.__monitor != null && table.document.__monitor.shouldMeasureTableColumnIndexEffectiveness()) {
          final var clause = filter.getIndices();
          final var effectiveness = makeEffectiveness(clause);
          var TOTAL = 0;
          String[] columns = null;
          for (final Ty item : table.scan(filter)) {
            if (item.__isDying()) {
              continue;
            }
            final var TEST = crazyCandidate(clause, item.__getIndexValues(), effectiveness);
            if (columns == null) {
              columns = item.__getIndexColumns();
            }
            TOTAL++;
            if (TEST) {
              if (filter.test(item)) {
                finalized.add(item);
              }
            }
          }
          if (columns != null) {
            for (var candidate = 0; candidate < effectiveness.length; candidate++) {
              table.document.__monitor.registerTableColumnIndexEffectiveness(table.className, columns[clause[2 * candidate]], TOTAL, effectiveness[candidate]);
            }
          }
        } else {
          for (final Ty item : table.scan(filter)) {
            if (item.__isDying()) {
              continue;
            }
            if (filter.test(item)) {
              finalized.add(item);
            }
          }
        }
      } else {
        for (final Ty item : table) {
          if (item.__isDying()) {
            continue;
          }
          finalized.add(item);
        }
      }
    }
  }

  private static int[] makeEffectiveness(final int[] clause) {
    final var x = new int[clause.length / 2];
    for (var k = 0; k < x.length; k++) {
      x[k] = 0;
    }
    return x;
  }

  private static boolean crazyCandidate(final int[] clause, final int[] value, final int[] effectiveness) {
    var result = true;
    for (var k = 0; k + 1 < clause.length; k += 2) {
      if (value[clause[k]] != clause[k + 1]) {
        effectiveness[k / 2]++;
        result = false;
      }
    }
    return result;
  }

  @Override
  public NtList<Ty> get() {
    return this;
  }

  @Override
  public NtMaybe<Ty> lookup(final int k) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).lookup(k);
  }

  @Override
  public NtMaybe<Ty> lookup(NtMaybe<Integer> k) {
    if (k.has()) {
      return lookup(k.get());
    }
    return new NtMaybe<>();
  }

  @Override
  public void map(final Consumer<Ty> t) {
    ensureFinalized();
    for (final Ty item : finalized) {
      t.accept(item);
    }
  }

  @Override
  public <R> NtList<R> mapFunction(Function<Ty, R> foo) {
    ensureFinalized();
    ArrayList<R> result = new ArrayList<>();
    for (final Ty item : finalized) {
      result.add(foo.apply(item));
    }
    return new ArrayNtList<>(result);
  }

  @Override
  public NtList<Ty> orderBy(final boolean done, final Comparator<Ty> cmp) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).orderBy(true, cmp);
  }

  @Override
  public <TIn, TOut> NtMap<TIn, TOut> reduce(final Function<Ty, TIn> domain, final Function<NtList<Ty>, TOut> reducer) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).reduce(domain, reducer);
  }

  @Override
  public NtList<Ty> shuffle(final boolean done, final Random rng) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).shuffle(true, rng);
  }

  @Override
  public int size() {
    // should this be optimized... the deletion mechanism kind of sucks
    ensureFinalized();
    return finalized.size();
  }

  @Override
  public NtList<Ty> skip(final boolean done, final int skip) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).skip(true, skip);
  }

  @Override
  public NtList<Ty> limit(final boolean done, final int limit) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).limit(true, limit);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Ty[] toArray(final Function<Integer, Object> arrayMaker) {
    ensureFinalized();
    return finalized.toArray((Ty[]) arrayMaker.apply(finalized.size()));
  }

  @Override
  public <Out> NtList<Out> transform(final Function<Ty, Out> t) {
    ensureFinalized();
    return new ArrayNtList<>(finalized).transform(t);
  }

  @Override
  public NtList<Ty> where(final boolean done, final WhereClause<Ty> filter) {
    if (filter.getPrimaryKey() != null) {
      final var primary = table.getById(filter.getPrimaryKey());
      if (primary != null) {
        finalized = new ArrayList<>(0);
        if (!primary.__isDying()) {
          finalized.add(primary);
        }
        return new ArrayNtList<>(finalized).where(true, filter);
      }
    }
    this.filter = filter;
    ensureFinalized();
    return new ArrayNtList<>(finalized);
  }

  @Override
  public Iterator<Ty> iterator() {
    ensureFinalized();
    return finalized.iterator();
  }
}

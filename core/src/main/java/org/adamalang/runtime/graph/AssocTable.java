package org.adamalang.runtime.graph;

import org.adamalang.runtime.reactives.RxRecordBase;
import org.adamalang.runtime.reactives.RxTable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

/** an assoc table is a container of edges between two tables drive by a source */
public class AssocTable<B extends RxRecordBase<B>, F extends RxRecordBase<F>, T extends RxRecordBase<T>> {
  private final RxTable<B> source;
  private final RxTable<F> from;
  private final RxTable<T> to;

  private final Function<B, Integer> computeFromId;
  private final Function<B, Integer> computeToId;

  private class EdgeCache {
    private int from;
    private int to;
  }

  private final HashMap<Integer, EdgeCache> edgeCache;
  private final HashSet<Integer> invalid;

  public AssocTable(RxTable<B> source, RxTable<F> from, RxTable<T> to, Function<B, Integer> computeFromId, Function<B, Integer> computeToId) {
    this.source = source;
    this.from = from;
    this.to = to;
    this.computeFromId = computeFromId;
    this.computeToId = computeToId;
    this.edgeCache = new HashMap<>();
    this.invalid = new HashSet<>();
  }

  public void link() {

  }

  public void add(int id) {

  }

  public void change(int id) {

  }

  public void delete(int id) {

  }

  public void cache() {

  }
}

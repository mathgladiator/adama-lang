package org.adamalang.runtime.reactives.tables;

import org.adamalang.runtime.contracts.RxChild;

import java.util.TreeMap;
import java.util.TreeSet;

public class TableListener implements TableSubscription {
  private boolean scoped;
  private TreeMap<String, TreeSet<Integer>> indices;
  private boolean invalidated;
  private RxChild child;
  private boolean alive;
  private int primaryKey;

  public TableListener(RxChild child) {
    this.scoped = false;
    this.indices = null;
    this.invalidated = false;
    this.child = child;
    this.alive = true;
    this.primaryKey = 0;
  }

  public void reset() {
    this.scoped = false;
    this.indices = null;
    this.invalidated = true;
    this.primaryKey = 0;
  }

  // TODO: keep track of
  public void scopeToIndex(String field, int value) {
    scoped = true;
    if (this.indices == null) {
      this.indices = new TreeMap<>();
    }
    TreeSet<Integer> prior = this.indices.get(field);
    if (prior == null) {
      prior = new TreeSet<>();
      this.indices.put(field, prior);
    }
    prior.add(value);
  }

  public void scopeToPrimaryKey(int pkey) {
    scoped = true;
    this.primaryKey = pkey;
  }

  @Override
  public boolean alive() {
    return alive;
  }

  @Override
  public void add(int primaryKey) {

  }

  @Override
  public void change(int primaryKey) {

  }

  @Override
  public void index(int primaryKey, String field, int value) {

  }

  @Override
  public void remove(int primaryKey) {

  }
}

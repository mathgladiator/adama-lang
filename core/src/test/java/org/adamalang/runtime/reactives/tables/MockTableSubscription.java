package org.adamalang.runtime.reactives.tables;

import java.util.ArrayList;

public class MockTableSubscription implements TableSubscription {
  public boolean alive;
  public ArrayList<String> publishes;

  public MockTableSubscription() {
    this.alive = true;
    this.publishes = new ArrayList<>();
  }

  @Override
  public boolean alive() {
    return alive;
  }

  @Override
  public void primary(int primaryKey) {
    publishes.add("PKEY:" + primaryKey);
  }

  @Override
  public void index(int primaryKey, int index, int value) {
    publishes.add("IDX[" + primaryKey + "];" + index + "=" + value);
  }

  @Override
  public void all() {
    publishes.add("ALL");
  }
}

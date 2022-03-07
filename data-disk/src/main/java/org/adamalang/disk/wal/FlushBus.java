package org.adamalang.disk.wal;

import org.adamalang.common.Callback;
import org.adamalang.common.SimpleExecutor;

import java.util.ArrayList;

public class FlushBus {
  private ArrayList<Callback<?>> callbacks;
  private SimpleExecutor executor;

  public FlushBus(SimpleExecutor executor) {
    this.callbacks = new ArrayList<>();
    this.executor = executor;
  }

  public void flush(Callback<?> callback) {
  }
}

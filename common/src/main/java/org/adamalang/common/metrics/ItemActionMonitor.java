package org.adamalang.common.metrics;

public interface ItemActionMonitor {
  /** start an operation */
  ItemActionMonitorInstance start();

  /** the operation must success or fail. We also capture common bugs */
  interface ItemActionMonitorInstance {
    void executed();

    void rejected();

    void timeout();
  }
}

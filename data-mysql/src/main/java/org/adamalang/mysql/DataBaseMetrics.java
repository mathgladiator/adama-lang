/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.RequestResponseMonitor;

public class DataBaseMetrics {
  public final RequestResponseMonitor transaction;
  public final Runnable valid_exception;
  public final CallbackMonitor finder_find;
  public final CallbackMonitor finder_bind;
  public final CallbackMonitor finder_backup;
  public final CallbackMonitor finder_free;
  public final CallbackMonitor finder_delete;
  public final CallbackMonitor finder_list;
  public final Runnable capacity_duplicate;

  public DataBaseMetrics(MetricsFactory factory) {
    transaction = factory.makeRequestResponseMonitor("database_transaction");
    valid_exception = factory.counter("database_valid_exception");
    finder_find = factory.makeCallbackMonitor("database_finder_find");
    finder_bind = factory.makeCallbackMonitor("database_finder_bind");
    finder_backup = factory.makeCallbackMonitor("database_finder_backup");
    finder_free = factory.makeCallbackMonitor("database_finder_free");
    finder_delete = factory.makeCallbackMonitor("database_finder_delete");
    finder_list = factory.makeCallbackMonitor("database_finder_list");
    capacity_duplicate = factory.counter("database_capacity_duplicate");
  }
}

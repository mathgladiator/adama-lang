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
package org.adamalang.mysql;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.RequestResponseMonitor;

public class DataBaseMetrics {
  public final RequestResponseMonitor transaction;
  public final RequestResponseMonitor transaction_simple;
  public final Runnable valid_exception;
  public final CallbackMonitor finder_find;
  public final CallbackMonitor finder_bind;
  public final CallbackMonitor finder_backup;
  public final CallbackMonitor finder_free;
  public final CallbackMonitor finder_delete;
  public final CallbackMonitor finder_list;
  public final Runnable capacity_duplicate;
  public final Runnable metrics_success;
  public final Runnable metrics_failure;

  public DataBaseMetrics(MetricsFactory factory) {
    transaction = factory.makeRequestResponseMonitor("database_transaction");
    transaction_simple = factory.makeRequestResponseMonitor("database_transaction_simple");
    valid_exception = factory.counter("database_valid_exception");
    finder_find = factory.makeCallbackMonitor("database_finder_find");
    finder_bind = factory.makeCallbackMonitor("database_finder_bind");
    finder_backup = factory.makeCallbackMonitor("database_finder_backup");
    finder_free = factory.makeCallbackMonitor("database_finder_free");
    finder_delete = factory.makeCallbackMonitor("database_finder_delete");
    finder_list = factory.makeCallbackMonitor("database_finder_list");
    capacity_duplicate = factory.counter("database_capacity_duplicate");
    metrics_success = factory.counter("metrics_success");
    metrics_failure = factory.counter("metrics_failure");
  }
}

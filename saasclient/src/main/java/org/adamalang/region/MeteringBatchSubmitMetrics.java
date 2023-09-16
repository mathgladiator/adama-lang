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
package org.adamalang.region;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.MetricsFactory;

/** metrics for converting a metering batch to a message to an Adama document */
public class MeteringBatchSubmitMetrics {

  public final CallbackMonitor metering_batch_submit_find;
  public final CallbackMonitor metering_batch_submit_send;
  public final Runnable metering_batch_happy;
  public final Runnable metering_batch_lost;
  public final Runnable metering_batch_exception;

  public MeteringBatchSubmitMetrics(MetricsFactory factory) {
    this.metering_batch_submit_find = factory.makeCallbackMonitor("metering_batch_submit_find");
    this.metering_batch_submit_send = factory.makeCallbackMonitor("metering_batch_submit_send");
    this.metering_batch_lost = factory.counter("metering_batch_lost");
    this.metering_batch_happy = factory.counter("metering_batch_happy");
    this.metering_batch_exception = factory.counter("metering_batch_exception");
  }
}

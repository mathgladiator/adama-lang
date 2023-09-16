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
package org.adamalang.caravan;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

public class CaravanMetrics {
  public Runnable caravan_waste;
  public Runnable caravan_seq_off;
  public Inflight caravan_datalog_loss;

  public CaravanMetrics(MetricsFactory factory) {
    this.caravan_waste = factory.counter("caravan_waste");
    this.caravan_seq_off = factory.counter("caravan_seq_off");
    this.caravan_datalog_loss = factory.inflight("alarm_caravan_datalog_loss");
  }
}

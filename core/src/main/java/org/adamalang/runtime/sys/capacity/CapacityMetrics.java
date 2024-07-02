/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.sys.capacity;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

/** metrics for the capacity agent */
public class CapacityMetrics {
  public final Inflight shield_active_new_documents;
  public final Inflight shield_active_existing_connections;
  public final Inflight shield_active_messages;

  public final Inflight shield_count_hosts;
  public final Inflight shield_count_metering;
  public final Runnable shield_heat;

  public CapacityMetrics(MetricsFactory factory) {
    this.shield_active_new_documents = factory.inflight("alarm_shield_active_new_documents");
    this.shield_active_existing_connections = factory.inflight("alarm_shield_active_existing_connections");
    this.shield_active_messages = factory.inflight("alarm_shield_active_messages");
    this.shield_count_hosts = factory.inflight("shield_count_hosts");
    this.shield_count_metering = factory.inflight("shield_count_metering");
    this.shield_heat = factory.counter("shield_heat");
  }
}

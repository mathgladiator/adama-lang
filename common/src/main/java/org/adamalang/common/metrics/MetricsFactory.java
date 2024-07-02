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
package org.adamalang.common.metrics;

/**
 * the front-door for all metrics; all metrics are known in the first few ms of the process starting
 * up
 */
public interface MetricsFactory {
  /** produce a monitor for request response style operations */
  RequestResponseMonitor makeRequestResponseMonitor(String name);

  /** produce a monitor for a stream operation */
  StreamMonitor makeStreamMonitor(String name);

  /** produce a monitor for a callback */
  CallbackMonitor makeCallbackMonitor(String name);

  /** produce a counter */
  Runnable counter(String name);

  /** produce an inflight measurement */
  Inflight inflight(String name);

  /** produce a monitor for an item action queue */
  ItemActionMonitor makeItemActionMonitor(String name);

  /** kick of a dashboard page */
  void page(String name, String title);

  /** within a page group metrics under a section */
  void section(String title);
}

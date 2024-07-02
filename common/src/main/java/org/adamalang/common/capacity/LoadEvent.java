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
package org.adamalang.common.capacity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** a load event fires when an associated metric is elevated */
public class LoadEvent {
  private final Logger LOGGER = LoggerFactory.getLogger(LoadEvent.class);
  private final String name;
  private final BoolConsumer event;
  private final double threshold;
  private boolean active;

  public LoadEvent(String name, double threshold, BoolConsumer event) {
    this.name = name;
    this.threshold = threshold;
    this.event = event;
    this.active = false;
  }

  /** provide a sample of some metric */
  public void at(double metric) {
    boolean next = metric > threshold;
    if (active != next) {
      active = next;
      event.accept(active);
      LOGGER.error("load-event:" + name + " @ " + metric + " > " + threshold);
    }
  }
}

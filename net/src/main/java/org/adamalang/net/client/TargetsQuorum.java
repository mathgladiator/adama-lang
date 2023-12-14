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
package org.adamalang.net.client;

import java.util.Collection;
import java.util.TreeSet;
import java.util.function.Consumer;

/** Ensures that clients are available from both gossip and the hosts databases */
public class TargetsQuorum {
  private final LocalRegionClientMetrics metrics;
  private final Consumer<Collection<String>> destination;
  private final TreeSet<String> fromDatabase;
  private final TreeSet<String> fromGossip;
  private long created;

  public TargetsQuorum(LocalRegionClientMetrics metrics, Consumer<Collection<String>> destination) {
    this.metrics = metrics;
    this.destination = destination;
    this.fromGossip = new TreeSet<>();
    this.fromDatabase = new TreeSet<>();
    this.created = System.currentTimeMillis();
  }

  public void deliverDatabase(Collection<String> targets) {
    fromDatabase.clear();
    fromDatabase.addAll(targets);
    reconcileAndShip();
  }

  public void deliverGossip(Collection<String> targets) {
    fromGossip.clear();
    fromGossip.addAll(targets);
    reconcileAndShip();
  }

  public void reconcileAndShip() {
    TreeSet<String> union = new TreeSet<>();
    for (String x : fromDatabase) {
      union.add(x);
    }
    for (String x : fromGossip) {
      union.add(x);
    }
    metrics.client_host_set_database_size.set(fromDatabase.size());
    metrics.client_host_set_gossip_size.set(fromGossip.size());
    destination.accept(union);
  }
}

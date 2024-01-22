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
package org.adamalang.runtime.reactives.maps;

import org.adamalang.runtime.contracts.RxParent;

import java.util.*;

/** simple pubsub fanout for MapSubscriptions's under a parent  */
public class MapPubSub<DomainTy> implements MapSubscription<DomainTy> {
  private final RxParent owner;
  private final ArrayList<MapSubscription<DomainTy>> subscriptions;
  private final HashSet<DomainTy> seen;

  public MapPubSub(RxParent owner) {
    this.owner = owner;
    this.subscriptions = new ArrayList<>();
    this.seen = new HashSet<>();
  }

  @Override
  public boolean alive() {
    if (owner != null) {
      return owner.__isAlive();
    }
    return true;
  }

  @Override
  public boolean changed(DomainTy key) {
    if (seen.contains(key)) {
      return false;
    }
    seen.add(key);
    for (MapSubscription<DomainTy> subscription : subscriptions) {
      subscription.changed(key);
    }
    return true;
  }

  public void settle() {
    seen.clear();
  }

  public int count() {
    return subscriptions.size();
  }

  public void subscribe(MapSubscription ms) {
    subscriptions.add(ms);
  }

  public long __memory() {
    return 128 * subscriptions.size() + 1024;
  }

  public void gc() {
    Iterator<MapSubscription<DomainTy>> it = subscriptions.iterator();
    while (it.hasNext()) {
      if (!it.next().alive()) {
        it.remove();
      }
    }
  }
}

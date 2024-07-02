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
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.reactives.maps.MapSubscription;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** filters invalidation events based on the keys within a map */
public class RxMapGuard<DomainTy> implements MapSubscription<DomainTy> {
  private final RxDependent owner;

  private TreeMap<Integer, FireGate> children;
  private FireGate current;
  private FireGate root;

  /** gate standing between invalidations and a child's view */
  private class FireGate {
    private boolean all;
    private TreeSet<DomainTy> keys;
    private boolean viewFired;

    public FireGate() {
      this.all = false;
      this.keys = null;
      this.viewFired = false;
    }

    private void raiseFireAndReset() {
      this.all = false;
      this.keys = null;
      this.viewFired = true;
      owner.invalidateParent();
    }

    private void justReset() {
      this.all = false;
      this.keys = null;
      this.viewFired = false;
    }

    public boolean key(DomainTy key) {
      if (viewFired) {
        return false;
      }
      if (all) {
        raiseFireAndReset();
        return true;
      }
      if (keys != null) {
        if (keys.contains(key)) {
          raiseFireAndReset();
          return true;
        }
      }
      return false;
    }
  }

  public RxMapGuard(RxDependent owner) {
    this.owner = owner;
    this.children = null;
    this.root = new FireGate();
    this.current = root;
  }

  @Override
  public boolean alive() {
    if (owner != null) {
      return owner.alive();
    }
    return true;
  }

  /** reset the state */
  public void reset() {
    root.justReset();
    root.viewFired = false;
    current = root;
  }

  public void resetView(int viewId) {
    FireGate cv = new FireGate();
    current = cv;
    if (children == null) {
      children = new TreeMap<>();
    }
    children.put(viewId, cv);
  }

  public void finishView() {
    current = root;
  }

  public boolean isFired(int viewId) {
    if (children != null) {
      FireGate cv = children.get(viewId);
      if (cv != null) {
        return cv.viewFired;
      }
    }
    return false;
  }

  @Override
  public boolean changed(DomainTy key) {
    if (children != null) {
      for (Map.Entry<Integer, FireGate> cv : children.entrySet()) {
        cv.getValue().key(key);
      }
    } else {
      if (root.key(key)) {
        owner.__raiseInvalid();
      }
    }
    return false;
  }

  /** [capture] everything was read, so optimize for just that */
  public void readAll() {
    current.all = true;
  }

  /** [capture] a primary key was read */
  public void readKey(DomainTy pkey) {
    if (current.all) {
      return;
    }
    if (current.keys == null) {
      current.keys = new TreeSet<>();
    }
    current.keys.add(pkey);
  }

  public void __settle(Set<Integer> views) {
    // TODO: debounce and call cleanup child views
  }
}

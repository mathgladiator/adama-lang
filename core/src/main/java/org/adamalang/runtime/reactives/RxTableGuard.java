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
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.reactives.tables.TableSubscription;

import java.util.*;

/** this filters incoming events against was read to minimize invalidations */
public class RxTableGuard implements TableSubscription {
  private final RxDependent owner;
  private boolean all;
  private TreeSet<Integer> primaryKeys;
  private TreeMap<Integer, TreeSet<Integer>> indices;
  private boolean fired;
  private TreeMap<Integer, ChildViewGate> children;
  private ChildViewGate current;

  /** gate standing between invalidations and a child's view */
  private class ChildViewGate {
    private TreeSet<Integer> primaryKeys;
    private TreeMap<Integer, TreeSet<Integer>> indices;
    private boolean viewFired = false;

    public void index(int primaryKey, int field, int value) {
      if (viewFired) {
        return;
      }
      primary(primaryKey);
      if (indices != null) {
        TreeSet<Integer> vals = indices.get(field);
        if (vals != null) {
          if (vals.contains(value)) {
            viewFired = true;
            primaryKeys = null;
            indices = null;
          }
        }
      }
    }

    public void primary(int primaryKey) {
      if (viewFired) {
        return;
      }
      if (primaryKeys != null) {
        if (primaryKeys.contains(primaryKey)) {
          viewFired = true;
          primaryKeys = null;
          indices = null;
        }
      }
    }
  }

  public RxTableGuard(RxDependent owner) {
    this.owner = owner;
    this.all = false;
    this.primaryKeys = null;
    this.indices = null;
    this.fired = false;
    this.children = null;
  }

  @Override
  public boolean alive() {
    if (owner != null) {
      return owner.alive();
    }
    return true;
  }

  /** there was a change in an index */
  @Override
  public void index(int primaryKey, int field, int value) {
    if (fired) {
      return;
    }
    if (all) {
      fireAndCleanup();
      return;
    }
    if (children != null) {
      for (Map.Entry<Integer, ChildViewGate> cv : children.entrySet()) {
        cv.getValue().index(primaryKey, field, value);
      }
    } else {
      if (indices != null) {
        TreeSet<Integer> vals = indices.get(field);
        if (vals != null) {
          if (vals.contains(value)) {
            fireAndCleanup();
          }
        }
      }
    }
  }

  /** there was a change in a primary key */
  @Override
  public void primary(int primaryKey) {
    if (fired) {
      return;
    }
    if (all) {
      fireAndCleanup();
      return;
    }
    if (children != null) {
      for (Map.Entry<Integer, ChildViewGate> cv : children.entrySet()) {
        cv.getValue().primary(primaryKey);
      }
    } else if (primaryKeys != null) {
      if (primaryKeys.contains(primaryKey)) {
        fireAndCleanup();
      }
    }
  }

  /** there was a change at a macro level */
  @Override
  public void all() {
    if (fired) {
      return;
    }
    if (all) {
      fireAndCleanup();
    }
  }

  /** fire a macro change and invalidate the entire guard */
  public void fireAndCleanup() {
    if (fired) {
      return;
    }
    all = true;
    fired = true;
    children = null;
    owner.__raiseInvalid();
    resetInnerState();
  }

  /** reset the state */
  public void reset() {
    fired = false;
    resetInnerState();
  }

  /** clean up and reset back to an initial state */
  private void resetInnerState() {
    all = false;
    if (primaryKeys != null) {
      primaryKeys.clear();
    }
    if (indices != null) {
      indices.clear();
    }
  }

  /** [capture] everything was read, so optimize for just that */
  public void readAll() {
    all = true;
    primaryKeys = null;
    indices = null;
    children = null;
  }

  /** [capture] a primary key was read */
  public void readPrimaryKey(int pkey) {
    if (all) { // any change is going to fire, so don't optimize anymore
      return;
    }
    if (current != null) { // route to a child view
      if (current.primaryKeys == null) {
        current.primaryKeys = new TreeSet<>();
      }
      current.primaryKeys.add(pkey);
    } else {
      if (primaryKeys == null) {
        primaryKeys = new TreeSet<>();
      }
      primaryKeys.add(pkey);
    }
  }

  /** [capture] an index value was read */
  public void readIndexValue(int index, int value) {
    if (all) { // any change is going to fire, so don't optimize anymore
      return;
    }
    TreeSet<Integer> vals;
    if (current != null) { // route to a child view
      if (current.indices != null) {
        current.indices = new TreeMap<>();
      }
      vals = current.indices.get(index);
      if (vals == null) {
        vals = new TreeSet<>();
        current.indices.put(index, vals);
      }
    } else {
      if (indices == null) {
        indices = new TreeMap<>();
      }
      vals = indices.get(index);
      if (vals == null) {
        vals = new TreeSet<>();
        indices.put(index, vals);
      }
    }
    vals.add(value);
  }

  public void resetView(int viewId) {
    // reset the fired state
    fired = false;
    ChildViewGate cv = new ChildViewGate();
    current = cv;
    if (children == null) {
      children = new TreeMap<>();
    }
    children.put(viewId, cv);
  }

  public void finishView() {
    current = null;
  }

  public boolean isFired(int viewId) {
    if (children != null) {
      ChildViewGate cv = children.get(viewId);
      if (cv != null) {
        return cv.viewFired;
      }
    }
    return false;
  }

  public void cleanupChildViews(Set<Integer> views) {
    if (children != null) {
      Iterator<Map.Entry<Integer, ChildViewGate>> it = children.entrySet().iterator();
      while (it.hasNext()) {
        if (!views.contains(it.next().getKey())) {
          it.remove();
        }
      }
    }
  }
}

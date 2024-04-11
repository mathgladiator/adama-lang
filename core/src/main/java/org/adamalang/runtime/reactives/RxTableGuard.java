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
  private TreeMap<Integer, FireGate> children;
  private FireGate current;
  private FireGate root;

  /** gate standing between invalidations and a child's view */
  private class FireGate {
    private boolean all;
    private TreeSet<Integer> primaryKeys;
    private TreeMap<Integer, TreeSet<Integer>> indices;
    private boolean viewFired;

    public FireGate() {
      this.all = false;
      this.primaryKeys = null;
      this.indices = null;
      this.viewFired = false;
    }

    private void justReset() {
      this.all = false;
      this.primaryKeys = null;
      this.indices = null;
      this.viewFired = false;
    }

    private void raiseFireAndReset() {
      this.all = false;
      this.primaryKeys = null;
      this.indices = null;
      this.viewFired = true;
      owner.invalidateParent();
    }

    public boolean index(int field, int value) {
      if (viewFired) {
        return false;
      }
      if (all) {
        raiseFireAndReset();
        return true;
      }
      if (indices != null) {
        TreeSet<Integer> vals = indices.get(field);
        if (vals != null) {
          if (vals.contains(value)) {
            raiseFireAndReset();
            return true;
          }
        }
      }
      return false;
    }

    public boolean primary(int primaryKey) {
      if (viewFired) {
        return false;
      }
      if (all) {
        raiseFireAndReset();
        return true;
      }
      if (primaryKeys != null) {
        if (primaryKeys.contains(primaryKey)) {
          raiseFireAndReset();
          return true;
        }
      }
      return false;
    }
  }

  public RxTableGuard(RxDependent owner) {
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

  /** there was a change in an index */
  @Override
  public void index(int field, int value) {
    if (children != null) {
      for (Map.Entry<Integer, FireGate> cv : children.entrySet()) {
        cv.getValue().index(field, value);
      }
    } else {
      if (root.index(field, value)) {
        owner.__raiseInvalid();
      }
    }
  }

  /** there was a change in a primary key */
  @Override
  public boolean primary(int primaryKey) {
    if (children != null) {
      for (Map.Entry<Integer, FireGate> cv : children.entrySet()) {
        cv.getValue().primary(primaryKey);
      }
    } else {
      if (root.primary(primaryKey)) {
        owner.__raiseInvalid();
      }
    }
    return false;
  }

  /** reset the state */
  public void reset() {
    root.justReset();
    root.viewFired = false;
    current = root;
  }

  /** [capture] everything was read, so optimize for just that */
  public void readAll() {
    current.all = true;
  }

  /** [capture] a primary key was read */
  public void readPrimaryKey(int pkey) {
    if (current.all) {
      return;
    }
    if (current.primaryKeys == null) {
      current.primaryKeys = new TreeSet<>();
    }
    current.primaryKeys.add(pkey);
  }

  /** [capture] an index value was read */
  public void readIndexValue(int index, int value) {
    if (current.all) {
      return;
    }
    if (current.indices == null) {
      current.indices = new TreeMap<>();
    }
    TreeSet<Integer> vals = current.indices.get(index);
    if (vals == null) {
      vals = new TreeSet<>();
      current.indices.put(index, vals);
    }
    vals.add(value);
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

  public void __settle(Set<Integer> views) {
    if (children.size() > 2 * views.size()) {
      cleanupChildViews(views);
    }
  }

  public void cleanupChildViews(Set<Integer> views) {
    if (children != null) {
      Iterator<Map.Entry<Integer, FireGate>> it = children.entrySet().iterator();
      while (it.hasNext()) {
        if (!views.contains(it.next().getKey())) {
          it.remove();
        }
      }
    }
  }
}

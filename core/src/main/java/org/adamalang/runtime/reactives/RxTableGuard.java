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

import org.adamalang.common.template.tree.T;
import org.adamalang.runtime.reactives.tables.TableSubscription;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/** this filters incoming events against was read to minimize invalidations */
public class RxTableGuard implements TableSubscription {
  private final RxDependent owner;
  private boolean all;
  private TreeSet<Integer> primaryKeys;
  private TreeMap<Integer, TreeSet<Integer>> indices;
  private boolean fired;
  private TreeMap<Integer, ChildView> children;
  private ChildView current;

  private class ChildView {
    private TreeSet<Integer> primaryKeys;
    private TreeMap<Integer, TreeSet<Integer>> indices;
    private boolean fired;

    public void index(int primaryKey, int field, int value) {
      if (fired) {
        return;
      }
      if (primaryKeys != null) {
        if (primaryKeys.contains(primaryKey)) {
          fired = true;
        }
      }
      if (indices != null) {
        TreeSet<Integer> vals = indices.get(field);
        if (vals != null) {
          if (vals.contains(value)) {
            fired = true;
          }
        }
      }
    }

    public void primary(int primaryKey) {
      if (fired) {
        return;
      }
      if (primaryKeys != null) {
        if (primaryKeys.contains(primaryKey)) {
          fired = true;
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

  @Override
  public void index(int primaryKey, int field, int value) {
    if (fired) {
      return;
    }
    primary(primaryKey);
    if (all) {
      fireAndCleanup();
    }
    if (children != null) {
      for (Map.Entry<Integer, ChildView> cv : children.entrySet()) {
        cv.getValue().index(primaryKey, field, value);
      }
    }
    if (indices != null) {
      TreeSet<Integer> vals = indices.get(field);
      if (vals != null) {
        if (vals.contains(value)) {
          fireAndCleanup();
        }
      }
    }
  }

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
      for (Map.Entry<Integer, ChildView> cv : children.entrySet()) {
        cv.getValue().primary(primaryKey);
      }
    }
    if (primaryKeys != null) {
      if (primaryKeys.contains(primaryKey)) {
        fireAndCleanup();
      }
    }
  }

  @Override
  public void all() {
    if (fired) {
      return;
    }
    if (all) {
      fireAndCleanup();
    }
  }

  public void fireAndCleanup() {
    if (fired) {
      return;
    }
    all = true;
    fired = true;
    children = null;
    owner.__raiseInvalid();
    resetState();
  }

  public void reset() {
    fired = false;
    resetState();
  }

  public void resetState() {
    all = false;
    if (primaryKeys != null) {
      primaryKeys.clear();
    }
    if (indices != null) {
      indices.clear();
    }
  }

  public void readAll() {
    all = true;
    primaryKeys = null;
    indices = null;
    children = null;
  }

  public void readPrimaryKey(int pkey) {
    if (all) {
      return;
    }
    if (current != null) {
      if (current.primaryKeys == null) {
        current.primaryKeys = new TreeSet<>();
      }
      current.primaryKeys.add(pkey);
      return;
    }
    if (primaryKeys == null) {
      primaryKeys = new TreeSet<>();
    }
    primaryKeys.add(pkey);
  }

  public void readIndexValue(int index, int value) {
    if (all) {
      return;
    }
    TreeSet<Integer> vals;
    if (current != null) {
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
    if (all) {
      ChildView cv = new ChildView();
      current = cv;
      if (children == null) {
        children = new TreeMap<>();
      }
      children.put(viewId, cv);
    }
  }

  public void finishView() {
    current = null;
  }

  public boolean isFired(int viewId) {
    if (children != null) {
      ChildView cv = children.get(viewId);
      if (cv != null) {
        return cv.fired;
      }
    }
    return false;
  }
}

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

import java.util.TreeMap;
import java.util.TreeSet;

/** this filters incoming events against was read to minimize invalidations */
public class RxTableGuard implements TableSubscription {
  private final RxDependent owner;
  private boolean all;
  private TreeSet<Integer> primaryKeys;
  private TreeMap<Integer, TreeSet<Integer>> indices;
  private boolean fired;

  public RxTableGuard(RxDependent owner) {
    this.owner = owner;
    this.all = false;
    this.primaryKeys = null;
    this.indices = null;
    this.fired = false;
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
    if (all) {
      fireAndCleanup();
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
  }

  public void readPrimaryKey(int pkey) {
    if (all) {
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
    if (indices == null) {
      indices = new TreeMap<>();
    }
    TreeSet<Integer> vals = indices.get(index);
    if (vals == null) {
      vals = new TreeSet<>();
      indices.put(index, vals);
    }
    vals.add(value);
  }
}

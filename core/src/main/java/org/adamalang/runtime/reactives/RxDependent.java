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

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.reactives.maps.MapGuardTarget;

import java.util.ArrayList;

public abstract class RxDependent extends RxNerfedBase implements RxChild {
  protected ArrayList<GuardPairCommon> guards;

  /** combine guard pairs regardless of the data type */
  protected interface GuardPairCommon {
    /** for formulas */
    public void start();
    public void finish();

    /** for viewer centric bubbles */
    public void startView(int viewId);
    public void finishView();
    public boolean isFired(int viewId);
  }

  /** guard a table */
  protected class GuardPairTable implements GuardPairCommon {
    protected final RxTable<?> table;
    protected final RxTableGuard guard;

    protected GuardPairTable(RxTable<?> table, RxTableGuard guard) {
      this.table = table;
      this.guard = guard;
    }

    @Override
    public void start() {
      guard.reset();
      table.pushGuard(guard);
    }

    @Override
    public void finish() {
      table.popGuard();
    }

    @Override
    public void startView(int viewId) {
      guard.resetView(viewId);
      table.pushGuard(guard);
    }

    @Override
    public void finishView() {
      guard.finishView();
      table.popGuard();
    }

    @Override
    public boolean isFired(int viewId) {
      return guard.isFired(viewId);
    }
  }

  /** guard a map */
  protected class GuardPairMap implements GuardPairCommon {
    protected final MapGuardTarget map;
    protected final RxMapGuard<?> guard;

    protected GuardPairMap(MapGuardTarget map, RxMapGuard<?> guard) {
      this.map = map;
      this.guard = guard;
    }

    @Override
    public void start() {
      guard.reset();
      map.pushGuard(guard);
    }

    @Override
    public void finish() {
      map.popGuard();
    }

    @Override
    public void startView(int viewId) {
      guard.resetView(viewId);
      map.pushGuard(guard);
    }

    @Override
    public void finishView() {
      guard.finishView();
      map.popGuard();
    }

    @Override
    public boolean isFired(int viewId) {
      return guard.isFired(viewId);
    }
  }

  protected RxDependent(RxParent __parent) {
    super(__parent);
    this.guards = null;
  }

  /** is the thing alive */
  public abstract boolean alive();

  /** [formula mode] start capturing the reads */
  public void start() {
    if (guards != null) {
      for (GuardPairCommon gp : guards) {
        gp.start();
      }
    }
  }

  /** [formula mode] finish up capturing reads */
  public void finish() {
    if (guards != null) {
      for (GuardPairCommon gp : guards) {
        gp.finish();
      }
    }
  }

  /** connect a tableguard to a table */
  public void __guard(RxTable<?> table, RxTableGuard guard) {
    if (guards == null) {
      guards = new ArrayList<>();
    }
    guards.add(new GuardPairTable(table, guard));
  }

  public void __guard(MapGuardTarget map, RxMapGuard<?> guard) {
    if (guards == null) {
      guards = new ArrayList<>();
    }
    guards.add(new GuardPairMap(map, guard));
  }

  /** [bubble version] start capturing reads */
  public void startView(int viewId) {
    if (guards != null) {
      for (GuardPairCommon gp : guards) {
        gp.startView(viewId);
      }
    }
  }

  /** [bubble version] stop capturing reads */
  public void finishView() {
    if (guards != null) {
      for (GuardPairCommon gp : guards) {
        gp.finishView();
      }
    }
  }

  /** is the given view in a fired state */
  public boolean isFired(int viewId) {
    if (guards != null) {
      for (GuardPairCommon gp : guards) {
        if (gp.isFired(viewId)) {
          return true;
        }
      }
    }
    return false;
  }

  public void invalidateParent() {
    if (__parent != null) {
      __parent.__invalidateUp();
    }
  }
}

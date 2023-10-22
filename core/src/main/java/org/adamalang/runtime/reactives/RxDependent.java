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

import java.util.ArrayList;

public abstract class RxDependent extends RxBase implements RxChild {
  protected ArrayList<GuardPair> guards;

  protected class GuardPair {
    protected final RxTable<?> table;
    protected final RxTableGuard guard;

    protected GuardPair(RxTable<?> table, RxTableGuard guard) {
      this.table = table;
      this.guard = guard;
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
      for (GuardPair gp : guards) {
        gp.guard.reset();
        gp.table.setGuard(gp.guard);
      }
    }
  }

  /** [formula mode] finish up capturing reads */
  public void finish() {
    if (guards != null) {
      for (GuardPair gp : guards) {
        gp.table.setGuard(null);
      }
    }
  }

  /** connect a tableguard to a table */
  public void __guard(RxTable<?> table, RxTableGuard guard) {
    if (guards == null) {
      guards = new ArrayList<>();
      guards.add(new GuardPair(table, guard));
    }
  }

  /** [bubble version] start capturing reads */
  public void startView(int viewId) {
    if (guards != null) {
      for (GuardPair gp : guards) {
        gp.guard.resetView(viewId);
        gp.table.setGuard(gp.guard);
      }
    }
  }

  /** [bubble version] stop capturing reads */
  public void finishView() {
    if (guards != null) {
      for (GuardPair gp : guards) {
        gp.guard.finishView();
        gp.table.setGuard(null);
      }
    }
  }

  /** is the given view in a fired state */
  public boolean isFired(int viewId) {
    if (guards != null) {
      for (GuardPair gp : guards) {
        if (gp.guard.isFired(viewId)) {
          return true;
        }
      }
    }
    return false;
  }
}

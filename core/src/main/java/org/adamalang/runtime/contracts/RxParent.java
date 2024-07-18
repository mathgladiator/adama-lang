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
package org.adamalang.runtime.contracts;

import java.util.Set;

/** the parent (or data owner) of a reactive data type */
public interface RxParent {
  /** make this item dirty */
  void __raiseDirty();

  /** is the parent alive */
  boolean __isAlive();

  /** hidden costs made manifest up the parent chain */
  void __cost(int cost);

  /** children may request an upward invalidation */
  void __invalidateUp();

  /** settle down the reactivity */
  void __settle(Set<Integer> viewers);

  public static final RxParent DEAD = new RxParent() {
    @Override
    public void __raiseDirty() {
    }

    @Override
    public boolean __isAlive() {
      return false;
    }

    @Override
    public void __cost(int cost) {
    }

    @Override
    public void __invalidateUp() {
    }

    @Override
    public void __settle(Set<Integer> viewers) {
    }
  };
}

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
package org.adamalang.runtime.contracts;

import java.util.Set;

/** a simplified RxParent for proxying */
public abstract class RxParentIntercept implements RxParent {
  private final RxParent real;

  public RxParentIntercept(RxParent real) {
    this.real = real;
  }

  @Override
  public void __raiseDirty() {
    real.__raiseDirty();
  }

  @Override
  public boolean __isAlive() {
    return real.__isAlive();
  }

  @Override
  public void __cost(int cost) {
    real.__cost(cost);
  }

  @Override
  public void __settle(Set<Integer> viewers) {
    real.__settle(viewers);
  }
}

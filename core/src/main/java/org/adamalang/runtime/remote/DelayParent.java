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
package org.adamalang.runtime.remote;

import org.adamalang.runtime.contracts.RxParent;

/** allows delaying a dirty signal to an event */
public class DelayParent implements RxParent {
  private boolean dirty;
  private Runnable runnable;

  public DelayParent() {
    this.dirty = false;
    this.runnable = null;
  }

  @Override
  public void __raiseDirty() {
    if (this.runnable != null) {
      this.runnable.run();
    } else {
      this.dirty = true;
    }
  }

  @Override
  public boolean __isAlive() {
    return true;
  }

  public void bind(Runnable runnable) {
    this.runnable = runnable;
    if (dirty) {
      this.runnable.run();
      this.dirty = false;
    }
  }
}

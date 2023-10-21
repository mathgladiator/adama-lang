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

import org.adamalang.runtime.contracts.Indexable;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.reactives.tables.IndexInvalidate;

public abstract class RxIndexableBase extends RxBase implements Indexable {
  protected IndexInvalidate watcher;

  protected RxIndexableBase(RxParent __parent) {
    super(__parent);
  }

  @Override
  public void setWatcher(IndexInvalidate watcher) {
    this.watcher = watcher;
    this.watcher.invalidate(getIndexValue());
  }

  protected void trigger() {
    if (this.watcher != null) {
      this.watcher.invalidate(getIndexValue());
    }
  }
}

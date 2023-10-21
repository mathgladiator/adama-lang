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

public class RxTableGuard implements TableSubscription {
  private final RxLazy<?> owner;

  public RxTableGuard(RxLazy<?> owner) {
    this.owner = owner;
  }

  @Override
  public boolean alive() {
    if (owner != null) {
      return owner.alive();
    }
    return true;
  }

  @Override
  public void add(int primaryKey) {

  }

  @Override
  public void change(int primaryKey) {

  }

  @Override
  public void index(int primaryKey, int field, int value) {

  }

  @Override
  public void remove(int primaryKey) {

  }

  @Override
  public void all() {
  }

  public void reset() {
  }
}

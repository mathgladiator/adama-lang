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
package org.adamalang.runtime.reactives.tables;

import java.util.ArrayList;

public class MockTableSubscription implements TableSubscription {
  public boolean alive;
  public ArrayList<String> publishes;

  public MockTableSubscription() {
    this.alive = true;
    this.publishes = new ArrayList<>();
  }

  @Override
  public boolean alive() {
    return alive;
  }

  @Override
  public boolean primary(int primaryKey) {
    publishes.add("PKEY:" + primaryKey);
    return false;
  }

  @Override
  public void index(int index, int value) {
    publishes.add("IDX:" + index + "=" + value);
  }
}

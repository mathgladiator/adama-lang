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
package org.adamalang.runtime.reactives.tables;

public class TablePubSub implements TableSubscription {
  public void subscribe(TableSubscription ts) {

  }

  @Override
  public boolean alive() {
    return true;
  }

  @Override
  public void add(int primaryKey) {
    // a primary key was introduced
  }

  @Override
  public void change(int primaryKey) {
    // an object by a primary key was changed
  }

  @Override
  public void remove(int primaryKey) {
    // an object was removed
  }

  @Override
  public void index(int primaryKey, String field, int value) {
    // a field within an element with a primary key was changed
  }
}

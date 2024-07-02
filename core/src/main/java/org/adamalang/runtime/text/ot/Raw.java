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
package org.adamalang.runtime.text.ot;

/** a base string operand */
public class Raw implements Operand {
  public final String str;

  public Raw(String str) {
    this.str = str;
  }

  @Override
  public void transposeRangeIntoJoin(int at, int length, Join join) {
    join.children.add(new Range(this, at, length));
  }

  @Override
  public String get() {
    return str;
  }

  @Override
  public int length() {
    return str.length();
  }
}

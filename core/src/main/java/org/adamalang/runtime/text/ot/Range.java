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

/** a subrange of another operand */
public class Range implements Operand {

  public final Operand base;
  public final int at;
  public final int length;

  public Range(Operand base, int at, int length) {
    this.base = base;
    this.at = at;
    this.length = length;
  }

  @Override
  public void transposeRangeIntoJoin(int at, int length, Join join) {
    if (at == 0 && this.length == length) {
      join.children.add(this);
    } else {
      join.children.add(new Range(base, this.at + at, length));
    }
  }

  @Override
  public String get() {
    return base.get().substring(at, at + length);
  }

  @Override
  public int length() {
    return length;
  }
}

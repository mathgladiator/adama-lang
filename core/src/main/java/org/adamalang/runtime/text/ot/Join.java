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

import java.util.ArrayList;

/** many operands combined into one via concatenation */
public class Join implements Operand {
  public final ArrayList<Operand> children;

  public Join() {
    this.children = new ArrayList<>();
  }

  @Override
  public void transposeRangeIntoJoin(int at, int length, Join join) {
    int curAt = 0;
    for (Operand child : children) {
      int curLen = child.length();
      int iA = Math.max(curAt, at);
      int iB = Math.min(curAt + curLen, at + length);
      if (iA < iB) {
        child.transposeRangeIntoJoin(iA - curAt, iB - iA, join);
      }
      curAt += curLen;
    }
  }

  @Override
  public String get() {
    StringBuilder sb = new StringBuilder();
    for (Operand op : children) {
      sb.append(op.get());
    }
    return sb.toString();
  }

  @Override
  public int length() {
    int _length = 0;
    for (Operand op : children) {
      _length += op.length();
    }
    return _length;
  }
}

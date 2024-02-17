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
package org.adamalang.rxhtml.template.sp;

import org.adamalang.rxhtml.typing.ViewScope;

/** switch to a different tree (data or view) */
public class SwitchTo implements PathInstruction {
  public final String dest;

  public SwitchTo(String dest) {
    this.dest = dest;
  }

  @Override
  public ViewScope next(ViewScope vs) {
    return vs;
  }

  @Override
  public void visit(PathVisitor v) {
    if (dest.equals("view")) {
      v.view();
    } else {
      v.data();
    }
  }
}

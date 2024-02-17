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

import org.adamalang.rxhtml.template.StatePath;

/** visitor for a path */
public interface PathVisitor {

  public void data();

  public void view();

  public void root();

  public void parent();

  public void dive(String child);

  public void use(String field);

  public static void visit(String path, PathVisitor v) {
    StatePath sp = StatePath.resolve(path, "$");
    for (PathInstruction instruction : sp.instructions) {
      instruction.visit(v);
    }
    v.use(sp.name);
  }
}

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
package org.adamalang.rxhtml.atl.tree;

import org.adamalang.rxhtml.atl.Context;

import java.util.Map;

/** Negate a node (i.e. not for boolean) */
public class Negate implements Tree {
  private final Tree value;

  public Negate(Tree value) {
    this.value = value;
  }

  @Override
  public Map<String, String> variables() {
    return value.variables();
  }

  @Override
  public String debug() {
    return "!(" + value.debug() + ")";
  }

  @Override
  public String js(Context context, String env) {
    return "!(" + value.js(context, env) + ")";
  }
}

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
import java.util.TreeMap;

/** an if/then/else node */
public class Condition implements Tree {
  public final Tree guard;
  public Tree branchTrue;
  public Tree branchFalse;

  public Condition(Tree guard, Tree branchTrue, Tree branchFalse) {
    this.guard = guard;
    this.branchTrue = branchTrue;
    this.branchFalse = branchFalse;
  }

  @Override
  public Map<String, String> variables() {
    TreeMap<String, String> union = new TreeMap<>();
    union.putAll(guard.variables());
    union.putAll(branchTrue.variables());
    union.putAll(branchFalse.variables());
    return union;
  }

  @Override
  public String debug() {
    return "(" + guard.debug() + ") ? (" + branchTrue.debug() + ") : (" + branchFalse.debug() + ")";
  }

  @Override
  public String js(Context context, String env) {
    return "((" + guard.js(context, env) + ") ? (" + branchTrue.js(context, env) + ") : (" + branchFalse.js(context, env) + "))";
  }
}

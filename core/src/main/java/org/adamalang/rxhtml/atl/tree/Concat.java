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

public class Concat implements Tree {
  public final Tree[] children;

  public Concat(Tree... children) {
    this.children = children;
  }

  @Override
  public Map<String, String> variables() {
    TreeMap<String, String> union = new TreeMap<>();
    for (Tree child : children) {
      union.putAll(child.variables());
    }
    return union;
  }

  @Override
  public String debug() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append(children[0].debug());
    for (int k = 1; k < children.length; k++) {
      sb.append(",");
      sb.append(children[k].debug());
    }
    sb.append("]");
    return sb.toString();
  }

  @Override
  public String js(Context context, String env) {
    StringBuilder sb = new StringBuilder();
    sb.append(children[0].js(context, env));
    for (int k = 1; k < children.length; k++) {
      boolean skip = false;
      if (children[k] instanceof Text) {
        skip = ((Text) children[k]).skip(context);
      }
      if (!skip) {
        sb.append(" + ");
        sb.append(children[k].js(context, env));
      }
    }
    return sb.toString();
  }
}

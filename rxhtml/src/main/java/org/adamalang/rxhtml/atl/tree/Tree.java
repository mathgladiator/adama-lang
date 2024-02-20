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
import org.adamalang.rxhtml.typing.ViewScope;

import java.util.Map;
import java.util.Set;

/** common interface for the tree nodes */
public interface Tree {

  /** return a set of variables within the node */
  Map<String, String> variables();

  /** turn the node into an easy to debug string */
  String debug();

  /** javascript expression to build the string */
  String js(Context context, String env);

  /** does the tree have an auto variable */
  boolean hasAuto();

  /** write out the types */
  public void writeTypes(ViewScope vs);

  /** produce a set of queries made */
  public Set<String> queries();
}

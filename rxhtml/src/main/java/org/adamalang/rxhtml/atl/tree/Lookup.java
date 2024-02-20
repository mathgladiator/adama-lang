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
import org.adamalang.rxhtml.template.StatePath;
import org.adamalang.rxhtml.typing.ViewScope;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/** lookup a variable */
public class Lookup implements Tree {
  public final String name;
  public final String complete;
  private final StatePath sp;

  public Lookup(String variable) {
    this.sp = StatePath.resolve(variable, "$");
    this.name = sp.name;
    this.complete = variable;
  }

  @Override
  public Map<String, String> variables() {
    return Collections.singletonMap(name, complete);
  }

  @Override
  public Set<String> queries() {
    return Collections.singleton(complete);
  }

  @Override
  public String debug() {
    return "LOOKUP[" + name + "]";
  }

  @Override
  public String js(Context context, String env) {
    return "$.F(" + env + ",'" + name + "')";
  }

  @Override
  public boolean hasAuto() {
    return false;
  }

  @Override
  public void writeTypes(ViewScope vs) {
    vs.write(complete, "value", true);
  }
}

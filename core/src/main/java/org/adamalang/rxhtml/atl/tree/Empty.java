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

import java.util.Collections;
import java.util.Map;

public class Empty implements Tree {

  @Override
  public Map<String, String> variables() {
    return Collections.emptyMap();
  }

  @Override
  public String debug() {
    return "EMPTY";
  }

  @Override
  public String js(Context context, String env) {
    return "\"\"";
  }

  @Override
  public boolean hasAuto() {
    return false;
  }

  @Override
  public void writeTypes(ViewScope vs) {
  }
}

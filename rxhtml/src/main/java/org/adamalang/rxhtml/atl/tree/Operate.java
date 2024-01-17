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
import org.adamalang.rxhtml.atl.ParseException;
import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.typing.ViewScope;

import java.util.Map;
import java.util.TreeMap;

/** a simple way of doing string equality */
public class Operate implements Tree {
  public static final String[] OPERATORS = new String[] { "<=", ">=", "!=", "<", ">", "=" };

  public static String convertOp(String op) {
    if ("=".equals(op)) {
      return "==";
    }
    return op;
  }

  public final Tree tree;
  public final Tree value;
  public final String operator;

  public Operate(Tree tree, String value, String operator) throws ParseException  {
    this.tree = tree;
    this.value = Parser.parse(value);
    this.operator = operator;
  }

  @Override
  public Map<String, String> variables() {
    TreeMap<String, String> union = new TreeMap<>();
    union.putAll(tree.variables());
    union.putAll(value.variables());
    return union;
  }

  @Override
  public String debug() {
    return "OP(" + operator + ")[" + tree.debug() + ",'" + value.debug() + "']";
  }

  @Override
  public String js(Context context, String env) {
    return "(" + tree.js(Context.DEFAULT, env) + operator + value.js(Context.DEFAULT, env) + ")";
  }

  @Override
  public boolean hasAuto() {
    return tree.hasAuto() || value.hasAuto();
  }

  @Override
  public void writeTypes(ViewScope vs) {
    tree.writeTypes(vs);
    value.writeTypes(vs);
  }
}

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
package org.adamalang.rxhtml.acl.commands;

import org.adamalang.rxhtml.atl.ParseException;
import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.atl.tree.Tree;
import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.Escapes;
import org.adamalang.rxhtml.template.StatePath;

import java.util.Map;

/** set the string value of the path to the given value */
public class Set implements Command {
  public String path;
  public String value;
  public Tree tree;

  public Set(String path, String value) throws ParseException {
    if (path.startsWith("view:") | path.startsWith("data:")) {
      this.path = path;
    } else {
      this.path = "view:" + path;
    }
    this.value = value;
    this.tree = Parser.parse(value);
  }

  @Override
  public void write(Environment env, String type, String eVar) {
    StatePath pathSet = StatePath.resolve(this.path, env.stateVar);

    Map<String, String> vars = tree.variables();
    if (vars.size() == 0) {
      env.writer.tab().append("$.onS(").append(eVar).append(",'").append(type).append("',").append(pathSet.command).append(",'").append(pathSet.name).append("',").append(Escapes.constantOf(value)).append(");").newline();
    } else {
      var oVar = env.pool.ask();
      env.writer.tab().append("var ").append(oVar).append(" = {};").newline();
      for (Map.Entry<String, String> ve : vars.entrySet()) {
        StatePath pathVar = StatePath.resolve(ve.getValue(), env.stateVar);
        env.writer.tab().append("$.YS(").append(pathVar.command).append(",").append(oVar).append(",'").append(pathVar.name).append("');").newline();
      }
      env.writer.tab().append("$.onS(").append(eVar).append(",'").append(type).append("',").append(pathSet.command).append(",'").append(pathSet.name).append("',function(){ return ").append(tree.js(env.contextOf("event:" + type), oVar)).append(";});").newline();
    }
  }
}

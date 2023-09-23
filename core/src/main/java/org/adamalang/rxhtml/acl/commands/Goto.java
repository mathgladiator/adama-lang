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

/** send the view to another page within the forest */
public class Goto implements Command {
  public final String raw;
  public final Tree value;

  public Goto(String value) throws ParseException {
    this.raw = value;
    this.value = Parser.parse(value);
  }

  @Override
  public void write(Environment env, String type, String eVar) {
    Map<String, String> vars = value.variables();
    if (value.hasAuto()) {
      env.feedback.warn(env.element, "goto's can't use auto variables");
    }
    if (vars.size() == 0) {
      env.writer.tab().append("$.onGO(").append(eVar).append(",'").append(type).append("',").append(env.stateVar).append(",").append(Escapes.constantOf(raw)).append(");").newline();
    } else {
      var oVar = env.pool.ask();
      env.writer.tab().append("var ").append(oVar).append("={};").newline();
      for (Map.Entry<String, String> ve : vars.entrySet()) {
        StatePath pathVar = StatePath.resolve(ve.getValue(), env.stateVar);
        env.writer.tab().append("$.YS(").append(pathVar.command).append(",").append(oVar).append(",'").append(pathVar.name).append("');").newline();
      }
      env.writer.tab().append("$.onGO(").append(eVar).append(",'").append(type).append("',").append(env.stateVar).append(",function(){ return ").append(value.js(env.contextOf("event:" + type), oVar)).append(";});").newline();
    }
  }
}

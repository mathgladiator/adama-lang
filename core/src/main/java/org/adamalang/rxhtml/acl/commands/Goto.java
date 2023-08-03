/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
    if (vars.size() == 0) {
      env.writer.tab().append("$.onGO(").append(eVar).append(",'").append(type).append("',").append(env.stateVar).append(",").append(Escapes.constantOf(raw)).append(");").newline();
    } else {
      var oVar = env.pool.ask();
      env.writer.tab().append("var ").append(oVar).append(" = {};").newline();
      for (Map.Entry<String, String> ve : vars.entrySet()) {
        StatePath pathVar = StatePath.resolve(ve.getValue(), env.stateVar);
        env.writer.tab().append("$.YS(").append(pathVar.command).append(",").append(oVar).append(",'").append(pathVar.name).append("');").newline();
      }
      env.writer.tab().append("$.onGO(").append(eVar).append(",'").append(type).append("',").append(env.stateVar).append(",function(){ return ").append(value.js(env.contextOf("event:" + type), oVar)).append(";});").newline();
    }
  }
}

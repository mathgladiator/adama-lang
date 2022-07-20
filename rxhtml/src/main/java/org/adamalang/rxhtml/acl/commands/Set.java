/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.acl.commands;

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

  public Set(String path, String value) {
    if (path.startsWith("view:") | path.startsWith("data:")) {
      this.path = path;
    } else {
      this.path = "view:" + path;
    }
    this.value = value;
  }
  private String constant() {
    String processedValue = "'" + Escapes.escape39(value) + "'";
    try {
      Double.parseDouble(value);
      processedValue = value;
    } catch (NumberFormatException nfe) {
    }
    if (value.equals("true") || value.equals("false")) {
      processedValue = value;
    }
    return processedValue;
  }

  @Override
  public void write(Environment env, String type, String eVar) {
    StatePath pathSet = StatePath.resolve(this.path, env.stateVar);
    Tree tree = Parser.parse(value);
    Map<String, String> vars = tree.variables();
    if (vars.size() == 0) {
      env.writer.tab().append("$.onS(").append(eVar).append(",'").append(type).append("',").append(pathSet.command).append(",'").append(pathSet.name).append("',").append(constant()).append(");").newline();
    } else {
      var oVar = env.pool.ask();
      env.writer.tab().append("var ").append(oVar).append(" = {};").newline();
      for (Map.Entry<String, String> ve : vars.entrySet()) {
        StatePath pathVar = StatePath.resolve(ve.getValue(), env.stateVar);
        env.writer.tab().append("$.YS(").append(pathVar.command).append(",").append(oVar).append(",'").append(pathVar.name).append("');").newline();
      }
      env.writer.tab().append("$.onS(").append(eVar).append(",'").append(type).append("',").append(pathSet.command).append(",'").append(pathSet.name).append("',function(){ return ").append(tree.js(oVar)).append(";});").newline();
    }
  }
}

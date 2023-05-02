/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineWebDelete;
import org.adamalang.translator.tree.definitions.DefineWebGet;
import org.adamalang.translator.tree.definitions.DefineWebOptions;
import org.adamalang.translator.tree.definitions.DefineWebPut;
import org.adamalang.translator.tree.definitions.web.UriAction;
import org.adamalang.translator.tree.definitions.web.UriTable;
import org.adamalang.translator.tree.types.TyType;

import java.util.Map;
import java.util.TreeMap;

public class CodeGenWeb {
  private final Environment environment;
  private final UriTable table;
  private final TreeMap<String, String> translate;
  private final String method;

  public CodeGenWeb(Environment environment, UriTable table, String method) {
    this.environment = environment;
    this.table = table;
    this.translate = new TreeMap<>();
    this.method = method;
  }

  private void levelChild(final StringBuilderWithTabs sb, TreeMap<String, UriTable.UriLevel> next, int at, String field) {
    for (Map.Entry<String, UriTable.UriLevel> entry : next.entrySet()) {
      sb.append("if (_").append("" + at).append(".").append(field).append(" != null) {").tabUp().writeNewline();
      translate.put(entry.getKey(), "_" + at + "." + field);
      if (entry.getValue().tail) {
        translate.put("#tail-" + at + "-name", entry.getKey());
        translate.put("#tail-" + at + "-value", "_" + at + ".tail()");
      }
      level(sb, entry.getValue(), at + 1, true);
      sb.append("}").writeNewline();
    }
  }

  private void level(final StringBuilderWithTabs sb, UriTable.UriLevel level, int at, boolean tabDown) {
    if (!level.check()) {
      return;
    }
    sb.append("WebFragment ").append("_" + at).append(" = __path.at(").append("" + at).append(");").writeNewline();
    sb.append("if (_").append("" + at).append(" != null) {").tabUp().writeNewline();
    for (Map.Entry<String, UriTable.UriLevel> entry : level.fixed.entrySet()) {
      sb.append("if (_" + at).append(".fragment.equals(\"").append(entry.getKey()).append("\")) {").tabUp().writeNewline();
      level(sb, entry.getValue(), at + 1, true);
      sb.append("}").writeNewline();
    }
    levelChild(sb, level.bools, at, "val_boolean");
    levelChild(sb, level.ints, at, "val_int");
    levelChild(sb, level.longs, at, "val_long");
    levelChild(sb, level.doubles, at, "val_double");
    levelChild(sb, level.strings, at, "fragment");
    sb.append("/** END:" + at + "*/").tabDown().writeNewline();
    sb.append("}");
    if (level.action != null) {
      if (level.tail) {
        sb.writeNewline();
        sb.append("return ").append("__").append(method).append("_").append(level.name).append("(__request.context.who, __request");
        String paramName = translate.get("#tail-" + (at - 1) + "-name");
        if (paramName != null) {
          String paramValue = translate.get("#tail-" + (at - 1) + "-value");
          if (paramValue != null) {
            translate.put(paramName, paramValue);
          }
        }
        for (Map.Entry<String, TyType> param : level.action.parameters().entrySet()) {
          sb.append(", ").append(translate.get(param.getKey()));
        }
        sb.append(");").writeNewline();
      } else {
        sb.append(" else {").tabUp().writeNewline();
        sb.append("return ").append("__").append(method).append("_").append(level.name).append("(__request.context.who, __request");
        for (Map.Entry<String, TyType> param : level.action.parameters().entrySet()) {
          sb.append(", ").append(translate.get(param.getKey()));
        }
        sb.append(");").tabDown().writeNewline();
        sb.append("}");
      }
    }
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  private void table(StringBuilderWithTabs sb) {
    level(sb, this.table.root, 0, false);
  }

  private void writeGetHandler(StringBuilderWithTabs sb, Environment environment, String name, DefineWebGet get) {
    sb.append("private WebResponse __get_").append(name).append("(NtPrincipal __who, WebGet __request");
    for (Map.Entry<String, TyType> param : get.parameters().entrySet()) {
      sb.append(", ").append(param.getValue().getJavaConcreteType(environment)).append(" ").append(param.getKey());
    }
    sb.append(")");
    get.code.writeJava(sb, get.next(environment));
    sb.writeNewline();
  }

  private void writeOptionsHandler(StringBuilderWithTabs sb, Environment environment, String name, DefineWebOptions options) {
    sb.append("private WebResponse __options_").append(name).append("(NtPrincipal __who, WebGet __request");
    for (Map.Entry<String, TyType> param : options.parameters().entrySet()) {
      sb.append(", ").append(param.getValue().getJavaConcreteType(environment)).append(" ").append(param.getKey());
    }
    sb.append(")");
    options.code.writeJava(sb, options.next(environment));
    sb.writeNewline();
  }

  private void writePutHandler(StringBuilderWithTabs sb, Environment environment, String name, DefineWebPut put) {
    sb.append("private WebResponse __put_").append(name).append("(NtPrincipal __who, WebPut __request");
    for (Map.Entry<String, TyType> param : put.parameters().entrySet()) {
      sb.append(", ").append(param.getValue().getJavaConcreteType(environment)).append(" ").append(param.getKey());
    }
    sb.append(") {").tabUp().writeNewline();
    sb.append("RTx").append(put.messageType.text).append(" ").append(put.messageVariable.text).append(" = new RTx").append(put.messageType.text).append("(__request.body());").writeNewline();
    put.code.specialWriteJava(sb, put.next(environment), false, true);
    sb.append("}").writeNewline();
  }

  private void writeDeleteHandler(StringBuilderWithTabs sb, Environment environment, String name, DefineWebDelete delete) {
    sb.append("private WebResponse __delete_").append(name).append("(NtPrincipal __who, WebDelete __request");
    for (Map.Entry<String, TyType> param : delete.parameters().entrySet()) {
      sb.append(", ").append(param.getValue().getJavaConcreteType(environment)).append(" ").append(param.getKey());
    }
    sb.append(")");
    delete.code.writeJava(sb, delete.next(environment));
    sb.writeNewline();
  }

  public static void writeWebHandlers(final StringBuilderWithTabs sb, Environment environment) {
    {
      sb.append("@Override").writeNewline();
      sb.append("public WebResponse __get(WebGet __request) {").tabUp().writeNewline();
      sb.append("WebPath __path = new WebPath(__request.uri);").writeNewline();
      TreeMap<String, UriAction> actions = environment.document.webGet.ready("GET");
      CodeGenWeb get = new CodeGenWeb(environment, environment.document.webGet, "get");
      get.table(sb);
      sb.append("return null;").tabDown().writeNewline();
      sb.append("}").writeNewline();
      for (Map.Entry<String, UriAction> action : actions.entrySet()) {
        get.writeGetHandler(sb, environment, action.getKey(), (DefineWebGet) action.getValue());
      }
    }
    {
      sb.append("@Override").writeNewline();
      sb.append("protected WebResponse __put_internal(WebPut __request) {").tabUp().writeNewline();
      sb.append("WebPath __path = new WebPath(__request.uri);").writeNewline();
      TreeMap<String, UriAction> actions = environment.document.webPut.ready("PUT");
      CodeGenWeb put = new CodeGenWeb(environment, environment.document.webPut, "put");
      put.table(sb);
      sb.append("return null;").tabDown().writeNewline();
      sb.append("}").writeNewline();
      for (Map.Entry<String, UriAction> action : actions.entrySet()) {
        put.writePutHandler(sb, environment, action.getKey(), (DefineWebPut) action.getValue());
      }
    }
    {
      sb.append("@Override").writeNewline();
      sb.append("protected WebResponse __delete_internal(WebDelete __request) {").tabUp().writeNewline();
      sb.append("WebPath __path = new WebPath(__request.uri);").writeNewline();
      TreeMap<String, UriAction> actions = environment.document.webDelete.ready("DELETE");
      CodeGenWeb delete = new CodeGenWeb(environment, environment.document.webDelete, "delete");
      delete.table(sb);
      sb.append("return null;").tabDown().writeNewline();
      sb.append("}").writeNewline();
      for (Map.Entry<String, UriAction> action : actions.entrySet()) {
        delete.writeDeleteHandler(sb, environment, action.getKey(), (DefineWebDelete) action.getValue());
      }
    }
    {
      sb.append("@Override").writeNewline();
      sb.append("public WebResponse __options(WebGet __request) {").tabUp().writeNewline();
      sb.append("WebPath __path = new WebPath(__request.uri);").writeNewline();
      TreeMap<String, UriAction> actions = environment.document.webOptions.ready("OPTIONS");
      CodeGenWeb options = new CodeGenWeb(environment, environment.document.webOptions, "options");
      options.table(sb);
      sb.append("return null;").tabDown().writeNewline();
      sb.append("}").writeNewline();
      for (Map.Entry<String, UriAction> action : actions.entrySet()) {
        options.writeOptionsHandler(sb, environment, action.getKey(), (DefineWebOptions) action.getValue());
      }
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.rxhtml.acl.commands;

import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.Escapes;
import org.adamalang.rxhtml.template.StatePath;

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

  @Override
  public void write(Environment env, String type, String eVar) {
    StatePath path = StatePath.resolve(this.path, env.stateVar);
    String processedValue = "'" + Escapes.escape39(value) + "'";
    try {
      Double.parseDouble(value);
      processedValue = value;
    } catch (NumberFormatException nfe) {
    }
    if (value.equals("true") || value.equals("false")) {
      processedValue = value;
    }
    env.writer.tab().append("$.onS(").append(eVar).append(",'").append(type).append("',").append(path.command).append(",'").append(path.name).append("',").append(processedValue).append(");").newline();

  }
}

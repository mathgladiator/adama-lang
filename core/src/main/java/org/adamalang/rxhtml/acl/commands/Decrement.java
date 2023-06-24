/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.acl.commands;

import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.StatePath;

/** Decrement a number by 1 */
public class Decrement implements Command {
  public final String path;

  public Decrement(String path) {
    if (path.startsWith("view:") | path.startsWith("data:")) {
      this.path = path;
    } else {
      this.path = "view:" + path;
    }
  }

  @Override
  public void write(Environment env, String type, String eVar) {
    StatePath path = StatePath.resolve(this.path, env.stateVar);
    env.writer.tab().append("$.onD(").append(eVar).append(",'").append(type).append("',").append(path.command).append(",'").append(path.name).append("', -1);").newline();
  }
}

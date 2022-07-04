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
import org.adamalang.rxhtml.template.StatePath;

/** set the string value of the path to the given value */
public class Decide implements Command {
  public final String channel;
  public final String key;
  public final String path;

  public Decide(String channel, String key, String path) {
    this.channel = channel;
    this.key = key;
    this.path = path;
  }

  @Override
  public void write(Environment env, String type, String eVar) {
    StatePath path = StatePath.resolve(this.path, env.stateVar);
    env.writer.tab().append("$.exD(").append(eVar).append(",'").append(type).append("',").append(path.command).append(",'").append(path.name).append("','").append(channel).append("','").append(key).append("');").newline();
  }
}

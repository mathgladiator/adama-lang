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

/** choose a value out of a set */
public class Choose implements Command {
  public final String channel;
  public final String key;
  public final String path;

  public Choose(String channel, String key, String path) {
    this.channel = channel;
    this.key = key;
    this.path = path;
  }

  @Override
  public void write(Environment env, String type, String eVar) {
    StatePath path = StatePath.resolve(this.path, env.stateVar);
    env.writer.tab().append("$.exCH(").append(eVar).append(",'").append(type).append("',").append(path.command).append(",'").append(path.name).append("','").append(channel).append("','").append(key).append("');").newline();
  }
}

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

public class Fire implements Command {
  private final String channel;

  public Fire(String channel) {
    this.channel = channel;
  }

  @Override
  public void write(Environment env, String type, String eVar) {
    env.writer.tab().append("$.onFR(").append(env.stateVar).append(",").append(eVar).append(",'").append(type).append("','").append(channel).append("');").newline();
  }
}

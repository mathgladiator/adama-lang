/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.acl.commands;

import org.adamalang.rxhtml.template.Environment;

/** a way for testing users at the developer stage to inject an authentication token into the system */
public class ForceAuth implements Command {
  public String name;
  public String identity;

  public ForceAuth(String name, String identity) {
    this.name = name;
    this.identity = identity;
  }

  @Override
  public void write(Environment env, String type, String eVar) {
    env.writer.tab().append("$.onFORCE_AUTH(").append(eVar).append(",'").append(type).append("','").append(name).append("','").append(identity).append("');").newline();
  }
}

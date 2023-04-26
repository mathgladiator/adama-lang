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
    env.writer.tab().append("$.onFORCE_AUTH('").append(name).append("','").append(identity).append("');").newline();
  }
}

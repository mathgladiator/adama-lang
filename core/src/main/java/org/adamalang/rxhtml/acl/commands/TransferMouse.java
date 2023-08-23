package org.adamalang.rxhtml.acl.commands;

import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.StatePath;

public class TransferMouse implements Command {
  private final String path;
  private final int offX;
  private final int offY;

  public TransferMouse(String path, int offX, int offY) {
    if (path.startsWith("view:") | path.startsWith("data:")) {
      this.path = path;
    } else {
      this.path = "view:" + path;
    }
    this.offX = offX;
    this.offY = offY;
  }

  @Override
  public void write(Environment env, String type, String eVar) {
    StatePath pathSet = StatePath.resolve(this.path, env.stateVar);
    env.writer.tab().append("$.onTM(").append(eVar).append(",'").append(type).append("',").append(pathSet.command).append(",'").append(pathSet.name).append("',").append("" + offX).append(",").append("" + offY).append(");").newline();
  }

  public static int parseIntOrZero(String x) {
    try {
      return Integer.parseInt(x);
    } catch (NumberFormatException nfe) {
      return 0;
    }
  }
}

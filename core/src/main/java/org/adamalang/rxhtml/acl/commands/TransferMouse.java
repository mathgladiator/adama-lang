/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.rxhtml.acl.commands;

import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.StatePath;
import org.adamalang.rxhtml.typing.ViewScope;

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

  @Override
  public void writeTypes(ViewScope vs) {
    vs.write(path, "mouse", false);
  }

  public static int parseIntOrZero(String x) {
    try {
      return Integer.parseInt(x);
    } catch (NumberFormatException nfe) {
      return 0;
    }
  }
}

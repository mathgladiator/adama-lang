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

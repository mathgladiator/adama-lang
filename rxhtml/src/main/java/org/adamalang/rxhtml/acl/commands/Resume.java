/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
import org.adamalang.rxhtml.typing.ViewScope;

public class Resume implements Command, BulkCommand {
  @Override
  public void writeBulk(Environment env, String eVar, String appendTo) {
    env.writer.tab().append(appendTo).append(".push(").append("$.bR());").newline();
  }

  @Override
  public void write(Environment env, String type, String eVar) {
  }

  @Override
  public void writeTypes(ViewScope vs) {
  }
}

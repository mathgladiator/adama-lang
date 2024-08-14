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
package org.adamalang.rxhtml;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.rxhtml.routing.Table;
import org.adamalang.rxhtml.template.Shell;
import org.adamalang.rxhtml.template.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class RxHtmlBundle {
  public final String javascript;
  public final String style;
  public final Shell shell;
  public final HashMap<String, Integer> cssFreq;
  public final ArrayList<Task> tasks;
  public final ObjectNode viewSchema;
  public final Table table;

  public RxHtmlBundle(String javascript, String style, Shell shell, HashMap<String, Integer> cssFreq, ArrayList<Task> tasks, ObjectNode viewSchema, Table table) {
    this.javascript = javascript;
    this.style = style;
    this.shell = shell;
    this.cssFreq = cssFreq;
    this.tasks = tasks;
    this.viewSchema = viewSchema;
    this.table = table;
  }

  @Override
  public String toString() {
    return "JavaScript:" + javascript.trim() + "\nStyle:" + style.trim() + "\nShell:" + shell.makeShell(this);
  }
}

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

import org.adamalang.rxhtml.routing.Table;
import org.adamalang.rxhtml.template.Shell;

/** a bundle of the results from producing RxHTML */
public class RxHtmlBundle {
  public final String javascript;
  public final String style;
  public final Shell shell;
  public final Table table;
  public final Diagnostics diagnostics;

  public RxHtmlBundle(String javascript, String style, Shell shell, Diagnostics diagnostics, Table table) {
    this.javascript = javascript;
    this.style = style;
    this.shell = shell;
    this.table = table;
    this.diagnostics = diagnostics;
  }

  @Override
  public String toString() {
    return "JavaScript:" + javascript.trim() + "\nStyle:" + style.trim() + "\nShell:" + shell.makeShell(this);
  }
}

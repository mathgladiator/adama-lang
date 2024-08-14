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
package org.adamalang.runtime.sys.web.rxhtml;

import org.adamalang.rxhtml.Diagnostics;
import org.adamalang.rxhtml.RxHtmlBundle;
import org.adamalang.rxhtml.routing.Table;
import org.adamalang.rxhtml.template.Shell;

import java.util.TreeMap;

/** result of executing RxHtml */
public class RxHtmlResult {
  public final RxHtmlBundle bundle;
  public final Shell shell;
  public final Table table;
  public final Diagnostics diagnostics;

  public RxHtmlResult(RxHtmlBundle bundle) {
    this.bundle = bundle;
    this.shell = bundle.shell;
    this.table = bundle.table;
    this.diagnostics = bundle.diagnostics;
  }

  public boolean test(String uri) {
    return table.route(uri, new TreeMap<>()) != null;
  }
}
